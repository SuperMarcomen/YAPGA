package it.marcodemartino.yapga.client.logic;

import it.marcodemartino.yapga.client.logic.results.ResultBroadcaster;
import it.marcodemartino.yapga.client.logic.services.EncryptionService;
import it.marcodemartino.yapga.client.logic.socket.SSLSocketClient;
import it.marcodemartino.yapga.client.ui.UIStarter;
import it.marcodemartino.yapga.client.ui.YAPGAUI;
import it.marcodemartino.yapga.common.application.Application;
import it.marcodemartino.yapga.common.encryption.asymmetric.AsymmetricEncryption;
import it.marcodemartino.yapga.common.encryption.asymmetric.rsa.RSAEncryption;
import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;
import it.marcodemartino.yapga.common.encryption.symmetric.aes.AESEncryption;

import java.io.IOException;
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
        ResultBroadcaster resultBroadcaster = new ResultBroadcaster();

        Runnable runnable = new UIStarter();
        YAPGAUI.setResultBroadcaster(resultBroadcaster);
        YAPGAUI.setEncryptionService(encryptionService);
        Thread uiThread = new Thread(runnable);
        uiThread.start();
    }
}
