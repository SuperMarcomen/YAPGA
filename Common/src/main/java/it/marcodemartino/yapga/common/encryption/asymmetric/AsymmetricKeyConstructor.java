package it.marcodemartino.yapga.common.encryption.asymmetric;

import java.security.*;

public interface AsymmetricKeyConstructor {

    void generateKeyPair();
    PublicKey getPublicKey();
    PrivateKey getPrivateKey();
    void setKeys(KeyPair keyPair);
    PublicKey constructPublicKeyFromString(String key);
    PrivateKey constructPrivateKeyFromString(String key);
    String publicKeyToString(PublicKey publicKey);
    String privateKeyToString(PrivateKey publicKey);

}
