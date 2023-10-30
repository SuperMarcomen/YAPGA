package it.marcodemartino.yapga.common.encryption.asymmetric;

import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;

import java.security.KeyPair;

public interface AsymmetricKeyWriter {

    void writeToFile(KeyPair keyPair, SymmetricEncryption symmetricEncryption);
    void writeToFile(KeyPair keyPair);

}
