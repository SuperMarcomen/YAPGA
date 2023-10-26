package it.marcodemartino.yapga.common.encryption.symmetric;

public interface SymmetricKeyConstructor {

    void generateKey();
    void generateKeyFromPassword(String password, byte[] salt);
    byte[] generateSalt();
    SymmetricKeyContainer getKey();
    void setKey(SymmetricKeyContainer secretKey);
    SymmetricKeyContainer constructKeyFromString(String key);
    String keyToString(SymmetricKeyContainer secretKey);

}
