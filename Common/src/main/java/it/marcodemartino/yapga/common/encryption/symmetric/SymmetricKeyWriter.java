package it.marcodemartino.yapga.common.encryption.symmetric;

public interface SymmetricKeyWriter {

    void writeKey(SymmetricKeyContainer symmetricKeyContainer);
    void writeSalt(byte[] salt);

}
