package it.marcodemartino.yapga.common.encryption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.security.*;
import java.util.Base64;

public class AsymmetricKeyFileWriter implements AsymmetricKeyWriter {

    private final Logger logger = LogManager.getLogger(AsymmetricKeyFileWriter.class);

    @Override
    public void writeToFile(KeyPair keyPair, Path path) {
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        String publicText = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateText = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        try {
            Files.writeString(Paths.get(path.toAbsolutePath().toString(), "public_key.pem"), publicText);
            Files.writeString(Paths.get(path.toAbsolutePath().toString(), "private_key.pem"), privateText);
        } catch (IOException e) {
            logger.error("There was an error writing the rsa keys to file", e);
        }
    }
}
