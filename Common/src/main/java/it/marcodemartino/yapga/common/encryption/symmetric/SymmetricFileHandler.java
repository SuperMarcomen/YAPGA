package it.marcodemartino.yapga.common.encryption.symmetric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;

public class SymmetricFileHandler implements ISymmetricFileHandler {

    private static final String KEY_FILE_NAME = "aes_key.pem";
    private static final String SALT_FILE_NAME = "main_salt.pem";
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
    public SymmetricKeyContainer readKeyEncrypted(SymmetricEncryption otherEncryption) {
        try {
            byte[] fileContent = Files.readAllBytes(getKeyPath());
            return keyConstructor.constructKeyFromString(otherEncryption.decryptToString(fileContent));
        } catch (IOException e) {
            logger.error("There was an error reading the symmetric key from disk", e);
            return null;
        }
    }

    @Override
    public byte[] readSalt() {
        try {
            String fileContent = Files.readString(Paths.get(SALT_FILE_NAME));
            return Base64.getDecoder().decode(fileContent);
        } catch (IOException e) {
            logger.error("There was an error reading the salt from disk", e);
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

    @Override
    public void writeKeyEncrypted(SymmetricKeyContainer symmetricKeyContainer, SymmetricEncryption otherEncryption) {
        String fileContent = keyConstructor.keyToString(symmetricKeyContainer);
        try {
            Files.write(getKeyPath(), otherEncryption.encryptFromString(fileContent));
        } catch (IOException e) {
            logger.error("There was an error writing the symmetric key to disk", e);
        }
    }

    @Override
    public void writeSalt(byte[] salt) {
        String fileContent = Base64.getEncoder().encodeToString(salt);
        try {
            Files.writeString(Paths.get(SALT_FILE_NAME), fileContent);
        } catch (IOException e) {
            logger.error("There was an error writing the salt to disk", e);
        }
    }

    private Path getKeyPath() {
        return Paths.get(KEY_FILE_NAME);
    }
}
