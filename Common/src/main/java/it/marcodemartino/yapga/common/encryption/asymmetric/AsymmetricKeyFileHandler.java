package it.marcodemartino.yapga.common.encryption.asymmetric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.security.*;

public class AsymmetricKeyFileHandler implements IAsymmetricKeyFileHandler {

    private final Logger logger = LogManager.getLogger(AsymmetricKeyFileHandler.class);
    private final AsymmetricKeyConstructor keyConstructor;

    public AsymmetricKeyFileHandler(AsymmetricKeyConstructor keyConstructor) {
        this.keyConstructor = keyConstructor;
    }

    @Override
    public KeyPair readKeyPair(String publicName, String privateName) {
        try {
            PublicKey publicKey = getPublicKey(publicName);
            PrivateKey privateKey = getPrivateKey(privateName);
            return new KeyPair(publicKey, privateKey);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void writeToFile(KeyPair keyPair, Path path) {
        String publicText = keyConstructor.publicKeyToString(keyPair.getPublic());
        String privateText = keyConstructor.privateKeyToString(keyPair.getPrivate());
        try {
            Files.writeString(Paths.get(path.toAbsolutePath().toString(), "public_key.pem"), publicText);
            Files.writeString(Paths.get(path.toAbsolutePath().toString(), "private_key.pem"), privateText);
        } catch (IOException e) {
            logger.error("There was an error writing the rsa keys to file", e);
        }
    }

    private PublicKey getPublicKey(String publicName) throws IOException {
        String fileContent = Files.readString(Paths.get(publicName));
        return keyConstructor.constructPublicKeyFromString(fileContent);
    }

    private PrivateKey getPrivateKey(String privateName) throws IOException {
        String fileContent = Files.readString(Paths.get(privateName));
        return keyConstructor.constructPrivateKeyFromString(fileContent);
    }
}
