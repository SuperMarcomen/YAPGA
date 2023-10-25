package it.marcodemartino.yapga.common.encryption;

import java.security.*;

public interface AsymmetricKeyConstructor {

    void generateKeyPair();
    PublicKey getPublicKey();
    PrivateKey getPrivateKey();
    void setKeys(KeyPair keyPair);
    PublicKey constructKeyFromString(String key);
    String publicKeyToString(PublicKey publicKey);

}
