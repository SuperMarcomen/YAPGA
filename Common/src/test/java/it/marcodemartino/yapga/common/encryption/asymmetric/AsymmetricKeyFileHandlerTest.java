package it.marcodemartino.yapga.common.encryption.asymmetric;

import it.marcodemartino.yapga.common.encryption.asymmetric.rsa.RSAEncryption;
import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;
import it.marcodemartino.yapga.common.encryption.symmetric.aes.AESEncryption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class AsymmetricKeyFileHandlerTest {

    private static AsymmetricKeyFileHandler asymmetricKeyFileHandler;
    private static KeyPair keyPair;
    private static SymmetricEncryption symmetricEncryption;

    @BeforeAll
    static void init() {
        AsymmetricEncryption asymmetricEncryption = new RSAEncryption(2048);
        asymmetricEncryption.generateKeyPair();
        keyPair = new KeyPair(asymmetricEncryption.getPublicKey(), asymmetricEncryption.getPrivateKey());
        asymmetricKeyFileHandler = new AsymmetricKeyFileHandler(asymmetricEncryption);
        symmetricEncryption = new AESEncryption(128);
        symmetricEncryption.generateKey();
    }

    @Test
    void readAndWrite() {
        asymmetricKeyFileHandler.writeToFile(keyPair, symmetricEncryption);
        KeyPair readKey = asymmetricKeyFileHandler.readKeyPair(symmetricEncryption);
        assertArrayEquals(keyPair.getPublic().getEncoded(), readKey.getPublic().getEncoded());
        assertArrayEquals(keyPair.getPrivate().getEncoded(), readKey.getPrivate().getEncoded());
    }
}