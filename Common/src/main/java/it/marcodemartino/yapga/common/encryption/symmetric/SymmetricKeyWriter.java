package it.marcodemartino.yapga.common.encryption.symmetric;

public interface SymmetricKeyWriter {

    void writeKey(SymmetricKeyContainer symmetricKeyContainer);
    void writeKeyEncrypted(SymmetricKeyContainer symmetricKeyContainer, SymmetricEncryption otherEncryption);

    void writeSalt(byte[] salt);

}
