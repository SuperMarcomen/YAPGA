package it.marcodemartino.yapga.common.encryption.symmetric;

import javax.crypto.Cipher;

public interface SymmetricEncryption extends SymmetricKeyConstructor {

    byte[] encryptFromString(String input);
    byte[] encrypt(byte[] input);
    String decryptToString(byte[] input);
    byte[] decrypt(byte[] input);
    Cipher getEncryptCipher();
    Cipher getDecryptCipher();

}
