package it.marcodemartino.yapga.common.encryption.asymmetric;

import java.security.KeyPair;

public interface AsymmetricKeyReader {

    KeyPair readKeyPair(String publicName, String privateName);

}
