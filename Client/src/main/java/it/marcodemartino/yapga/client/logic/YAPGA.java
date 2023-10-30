package it.marcodemartino.yapga.client.logic;

import it.marcodemartino.yapga.client.logic.actions.Action;
import it.marcodemartino.yapga.client.logic.actions.RequestRemotePublicKey;
import it.marcodemartino.yapga.client.logic.certificates.CertificateFileReaderWriter;
import it.marcodemartino.yapga.client.logic.certificates.CertificateReaderWriter;
import it.marcodemartino.yapga.client.logic.commands.ReceiveRemotePublicKeyCommand;
import it.marcodemartino.yapga.client.logic.results.Result;
import it.marcodemartino.yapga.client.logic.results.ResultBroadcaster;
import it.marcodemartino.yapga.client.logic.services.AuthenticationService;
import it.marcodemartino.yapga.client.logic.services.EncryptionService;
import it.marcodemartino.yapga.client.logic.socket.SSLSocketClient;
import it.marcodemartino.yapga.client.ui.UIStarter;
import it.marcodemartino.yapga.client.ui.YAPGAUI;
import it.marcodemartino.yapga.common.application.Application;
import it.marcodemartino.yapga.common.commands.JsonCommandManager;
import it.marcodemartino.yapga.common.encryption.asymmetric.AsymmetricEncryption;
import it.marcodemartino.yapga.common.encryption.asymmetric.rsa.RSAEncryption;
import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;
import it.marcodemartino.yapga.common.encryption.symmetric.aes.AESEncryption;
import it.marcodemartino.yapga.common.json.JSONMethods;

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
        EncryptionService encryptionService = new EncryptionService(localSignature, remoteEncryption, localEncryption);
        CertificateReaderWriter certificateReaderWriter = new CertificateFileReaderWriter(Paths.get(""));
        ResultBroadcaster resultBroadcaster = new ResultBroadcaster();
        AuthenticationService authenticationService = new AuthenticationService(client.getIO(), certificateReaderWriter, encryptionService);
        resultBroadcaster.registerListener(Result.CORRECT_MAIN_PASSWORD, authenticationService::login);

        JsonCommandManager commandManager = new JsonCommandManager();
        commandManager.registerCommand(JSONMethods.SEND_REMOTE_PUBLIC_KEY, new ReceiveRemotePublicKeyCommand(encryptionService));
        client.getIO().registerInputListener(commandManager);

        Action action = new RequestRemotePublicKey(client.getIO());
        action.execute();

        Runnable runnable = new UIStarter();
        YAPGAUI.setResultBroadcaster(resultBroadcaster);
        YAPGAUI.setEncryptionService(encryptionService);
        Thread uiThread = new Thread(runnable);
        uiThread.start();
    }
}
