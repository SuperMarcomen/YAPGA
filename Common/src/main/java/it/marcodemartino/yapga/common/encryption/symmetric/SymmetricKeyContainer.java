package it.marcodemartino.yapga.common.encryption.symmetric;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public interface SymmetricKeyContainer {

    SecretKey getSecretKey();
    IvParameterSpec getIV();
}
