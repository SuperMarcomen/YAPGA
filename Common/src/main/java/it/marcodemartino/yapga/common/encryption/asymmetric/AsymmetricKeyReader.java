package it.marcodemartino.yapga.common.encryption.asymmetric;

import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;

import java.security.KeyPair;

public interface AsymmetricKeyReader {

    boolean doKeysExist();
    KeyPair readKeyPair(SymmetricEncryption symmetricEncryption);

}
