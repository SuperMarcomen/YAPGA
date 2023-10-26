package it.marcodemartino.yapga.common.encryption.symmetric;

public interface SymmetricKeyReader {

    boolean doesKeyExist();
    SymmetricKeyContainer readKey();
}
