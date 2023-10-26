package it.marcodemartino.yapga.common.encryption.symmetric;

import it.marcodemartino.yapga.common.encryption.symmetric.aes.AESEncryption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SymmetricFileHandlerTest {

    @Test
    void writeKey() {
        SymmetricEncryption symmetricEncryption = new AESEncryption(128);
        symmetricEncryption.generateKey();
        SymmetricKeyContainer keyContainer = symmetricEncryption.getKey();
        ISymmetricFileHandler symmetricFileHandler = new SymmetricFileHandler(symmetricEncryption);
        assertFalse(symmetricFileHandler.doesKeyExist());
        symmetricFileHandler.writeKey(keyContainer);
        assertTrue(symmetricFileHandler.doesKeyExist());
        SymmetricKeyContainer readKey = symmetricFileHandler.readKey();
        assertArrayEquals(keyContainer.getSecretKey().getEncoded(), readKey.getSecretKey().getEncoded());
        assertArrayEquals(keyContainer.getIV().getIV(), readKey.getIV().getIV());
    }
}