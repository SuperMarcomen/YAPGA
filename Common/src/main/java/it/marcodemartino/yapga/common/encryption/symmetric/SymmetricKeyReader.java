package it.marcodemartino.yapga.common.encryption.symmetric;

public interface SymmetricKeyReader {

    boolean doesKeyExist();
    SymmetricKeyContainer readKey();
    SymmetricKeyContainer readKeyEncrypted(SymmetricEncryption otherEncryption);
    boolean doesSaltExist();
    byte[] readSalt();

}
