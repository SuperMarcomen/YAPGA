package it.marcodemartino.yapga.common.encryption.symmetric.aes;

import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AESEncryptionTest {

    @Test
    void encrypt() {
        SymmetricEncryption aesEncryption = new AESEncryption(128);
        aesEncryption.generateKey();
        String input = "Sono un ragazzo molto bello";
        byte[] encryptedInput = aesEncryption.encryptFromString(input);
        String decryptedInput = aesEncryption.decryptToString(encryptedInput);
        assertEquals(input, decryptedInput);
    }
}