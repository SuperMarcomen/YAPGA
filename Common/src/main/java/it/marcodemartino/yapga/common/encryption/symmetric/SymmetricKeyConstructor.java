package it.marcodemartino.yapga.common.encryption.symmetric;

public interface SymmetricKeyConstructor {

    void generateKey();
    SymmetricKeyContainer getKey();
    void setKey(SymmetricKeyContainer secretKey);
    SymmetricKeyContainer constructKeyFromString(String key);
    String keyToString(SymmetricKeyContainer secretKey);

}
