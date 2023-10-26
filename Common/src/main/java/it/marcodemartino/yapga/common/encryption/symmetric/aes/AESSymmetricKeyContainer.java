package it.marcodemartino.yapga.common.encryption.symmetric.aes;

import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricKeyContainer;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AESSymmetricKeyContainer implements SymmetricKeyContainer {

    private final SecretKey secretKey;
    private final IvParameterSpec ivParameterSpec;

    public AESSymmetricKeyContainer(SecretKey secretKey, IvParameterSpec ivParameterSpec) {
        this.secretKey = secretKey;
        this.ivParameterSpec = ivParameterSpec;
    }

    @Override
    public SecretKey getSecretKey() {
        return secretKey;
    }

    @Override
    public IvParameterSpec getIV() {
        return ivParameterSpec;
    }
}
