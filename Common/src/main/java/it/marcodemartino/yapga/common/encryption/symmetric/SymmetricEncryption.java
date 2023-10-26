package it.marcodemartino.yapga.common.encryption.symmetric;

public interface SymmetricEncryption extends SymmetricKeyConstructor {

    byte[] encryptFromString(String input);
    byte[] encrypt(byte[] input);
    String decryptToString(byte[] input);
    byte[] decrypt(byte[] input);

}
