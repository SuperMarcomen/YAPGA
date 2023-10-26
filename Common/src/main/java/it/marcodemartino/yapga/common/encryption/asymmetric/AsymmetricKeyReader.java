package it.marcodemartino.yapga.common.encryption.asymmetric;

import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;

import java.security.KeyPair;

public interface AsymmetricKeyReader {

    KeyPair readKeyPair(SymmetricEncryption symmetricEncryption);

}
