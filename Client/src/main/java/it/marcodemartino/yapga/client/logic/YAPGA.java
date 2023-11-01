package it.marcodemartino.yapga.client.logic;

import it.marcodemartino.yapga.client.logic.actions.*;
import it.marcodemartino.yapga.client.logic.certificates.CertificateFileReaderWriter;
import it.marcodemartino.yapga.client.logic.certificates.CertificateReaderWriter;
import it.marcodemartino.yapga.client.logic.commands.*;
import it.marcodemartino.yapga.client.logic.results.Result;
import it.marcodemartino.yapga.client.logic.results.ResultBroadcaster;
import it.marcodemartino.yapga.client.logic.services.*;
import it.marcodemartino.yapga.client.logic.socket.SSLSocketClient;
import it.marcodemartino.yapga.client.ui.UIStarter;
import it.marcodemartino.yapga.client.ui.YAPGAUI;
import it.marcodemartino.yapga.common.application.Application;
import it.marcodemartino.yapga.common.commands.JsonCommandManager;
import it.marcodemartino.yapga.common.encryption.asymmetric.*;
import it.marcodemartino.yapga.common.encryption.asymmetric.rsa.RSAEncryption;
import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;
import it.marcodemartino.yapga.common.encryption.symmetric.aes.AESEncryption;
import it.marcodemartino.yapga.common.images.ImageResizer;
import it.marcodemartino.yapga.common.images.STDImageResizer;
import it.marcodemartino.yapga.common.json.JSONMethods;
import it.marcodemartino.yapga.common.services.EncryptionService;
import it.marcodemartino.yapga.common.services.ImageService;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;

public class YAPGA {

    public static void main(String[] args) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        Application client = new SSLSocketClient("127.0.0.1", 8443);
        Thread thread = new Thread(client);
        thread.start();

        AsymmetricEncryption localSignature = new RSAEncryption(2048);
        AsymmetricEncryption remoteEncryption = new RSAEncryption(2048);
        SymmetricEncryption localEncryption = new AESEncryption(128);
        IAsymmetricKeyFileHandler asymmetricKeyFileHandler = new AsymmetricKeyFileHandler(localSignature);

        ResultBroadcaster resultBroadcaster = new ResultBroadcaster();

        EncryptionService encryptionService = new EncryptionService(localSignature, remoteEncryption, localEncryption, asymmetricKeyFileHandler);
        CertificateReaderWriter certificateReaderWriter = new CertificateFileReaderWriter(Paths.get(""));
        CertificatesService certificatesService = new CertificatesService(certificateReaderWriter, encryptionService.getLocalSymmetricEncryption(), resultBroadcaster);
        AuthenticationService authenticationService = new AuthenticationService(client.getIO(), encryptionService, certificatesService);
        GalleryService galleryService = new GalleryService();

        JsonCommandManager commandManager = new JsonCommandManager();
        commandManager.registerCommand(JSONMethods.SEND_REMOTE_PUBLIC_KEY, new ReceiveRemotePublicKeyCommand(encryptionService));
        commandManager.registerCommand(JSONMethods.ENCRYPTED_MESSAGE, new EncryptedMessageCommand(client.getIO().getEventManager(), encryptionService));
        commandManager.registerCommand(JSONMethods.SEND_IDENTITY_CERTIFICATE, new ReceiveIdentityCertificate(encryptionService, certificatesService));
        commandManager.registerCommand(JSONMethods.SEND_IMAGE, new ReceiveImageCommand(client.getInputStream(), galleryService));
        client.getIO().registerInputListener(commandManager);

        resultBroadcaster.registerListener(Result.CORRECT_MAIN_PASSWORD, authenticationService::login);
        resultBroadcaster.registerListener(Result.RECEIVE_IDENTITY_CERTIFICATE, () -> {
            Action action = new RequestImageAction(client.getIO(), certificatesService, encryptionService, 50);
            action.execute();
        });
        Action action = new RequestRemotePublicKey(client.getIO());
        action.execute();

        ImageResizer imageResizer = new STDImageResizer();
        ImageService imageService = new ImageService(imageResizer, client.getIO(), encryptionService);
        Thread imageThread = new Thread(imageService);
        imageThread.start();

        Runnable runnable = new UIStarter();
        YAPGAUI.setResultBroadcaster(resultBroadcaster);
        YAPGAUI.setEncryptionService(encryptionService);
        YAPGAUI.setImageService(imageService);
        YAPGAUI.setCertificatesService(certificatesService);
        YAPGAUI.setOutputEmitter(client.getIO());
        YAPGAUI.setGalleryService(galleryService);
        Thread uiThread = new Thread(runnable);
        uiThread.start();
    }
}
