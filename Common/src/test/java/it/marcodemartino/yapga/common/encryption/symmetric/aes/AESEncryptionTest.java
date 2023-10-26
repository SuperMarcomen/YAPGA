package it.marcodemartino.yapga.common.encryption.symmetric.aes;

import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

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

    @Test
    void generateKeyFromPassword() {
        SymmetricEncryption aesEncryption = new AESEncryption(128);
        byte[] salt = new byte[8];
        new SecureRandom().nextBytes(salt);
        aesEncryption.generateKeyFromPassword("123Sorella!", salt);
        String input = "Sono un ragazzo molto bello";
        byte[] encryptedInput = aesEncryption.encryptFromString(input);
        String decryptedInput = aesEncryption.decryptToString(encryptedInput);
        assertEquals(input, decryptedInput);
    }
}