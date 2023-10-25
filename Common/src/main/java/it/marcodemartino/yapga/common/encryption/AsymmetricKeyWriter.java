package it.marcodemartino.yapga.common.encryption;

import java.nio.file.Path;
import java.security.KeyPair;

public interface AsymmetricKeyWriter {

    void writeToFile(KeyPair keyPair, Path path);

}
