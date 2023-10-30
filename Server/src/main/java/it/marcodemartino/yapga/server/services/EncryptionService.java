package it.marcodemartino.yapga.server.services;

import it.marcodemartino.yapga.common.encryption.asymmetric.AsymmetricEncryption;
import it.marcodemartino.yapga.common.encryption.asymmetric.AsymmetricKeyReader;

public class EncryptionService {

    private final AsymmetricEncryption localEncryption;
    private final AsymmetricEncryption remoteEncryption;
    private final AsymmetricKeyReader asymmetricKeyReader;

    public EncryptionService(AsymmetricEncryption localEncryption, AsymmetricEncryption remoteEncryption, AsymmetricKeyReader asymmetricKeyReader) {
        this.localEncryption = localEncryption;
        this.remoteEncryption = remoteEncryption;
        this.asymmetricKeyReader = asymmetricKeyReader;
    }



    public String getLocalPublicKeyAsString() {
        return localEncryption.publicKeyToString(localEncryption.getPublicKey());
    }

    public void loadKeys() {
        localEncryption.setKeys(asymmetricKeyReader.readKeyPair());
    }

    public AsymmetricEncryption getLocalEncryption() {
        return localEncryption;
    }
}
