package it.marcodemartino.yapga.common.encryption.symmetric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;

public class SymmetricFileHandler implements ISymmetricFileHandler {

    private static final String KEY_FILE_NAME = "aes_key.pem";
    private final Logger logger = LogManager.getLogger(SymmetricFileHandler.class);
    private final SymmetricKeyConstructor keyConstructor;

    public SymmetricFileHandler(SymmetricKeyConstructor keyConstructor) {
        this.keyConstructor = keyConstructor;
    }

    @Override
    public boolean doesKeyExist() {
        return Files.exists(getKeyPath());
    }

    @Override
    public SymmetricKeyContainer readKey() {
        try {
            String fileContent = Files.readString(getKeyPath());
            return keyConstructor.constructKeyFromString(fileContent);
        } catch (IOException e) {
            logger.error("There was an error reading the symmetric key from disk", e);
            return null;
        }
    }

    @Override
    public void writeKey(SymmetricKeyContainer symmetricKeyContainer) {
        String fileContent = keyConstructor.keyToString(symmetricKeyContainer);
        try {
            Files.writeString(getKeyPath(), fileContent);
        } catch (IOException e) {
            logger.error("There was an error writing the symmetric key to disk", e);
        }
    }

    private Path getKeyPath() {
        return Paths.get(KEY_FILE_NAME);
    }
}
