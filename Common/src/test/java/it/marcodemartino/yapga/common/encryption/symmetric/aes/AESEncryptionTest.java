package it.marcodemartino.yapga.common.encryption.symmetric.aes;

import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        byte[][] saltAndIv = aesEncryption.generateSaltAndIv();
        aesEncryption.generateKeyFromPassword("123Sorella!", saltAndIv[0], saltAndIv[1]);
        String input = "Sono un ragazzo molto bello";
        byte[] encryptedInput = aesEncryption.encryptFromString(input);
        aesEncryption.generateKeyFromPassword("123Sorella!", saltAndIv[0], saltAndIv[1]);
        String decryptedInput = aesEncryption.decryptToString(encryptedInput);
        assertEquals(input, decryptedInput);
    }
}