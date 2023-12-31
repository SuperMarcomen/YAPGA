package it.marcodemartino.yapga.common.encryption.asymmetric;

import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.security.*;

public class AsymmetricKeyFileHandler implements IAsymmetricKeyFileHandler {

    private static final String PUBLIC_KEY_FILE_NAME = "public_key.pem";
    private static final String PRIVATE_KEY_FILE_NAME = "private_key.pem";
    private final Logger logger = LogManager.getLogger(AsymmetricKeyFileHandler.class);
    private final AsymmetricKeyConstructor keyConstructor;

    public AsymmetricKeyFileHandler(AsymmetricKeyConstructor keyConstructor) {
        this.keyConstructor = keyConstructor;
    }

    @Override
    public boolean doKeysExist() {
        return Files.exists(Paths.get(PUBLIC_KEY_FILE_NAME)) && Files.exists(Paths.get(PRIVATE_KEY_FILE_NAME));
    }

    @Override
    public KeyPair readKeyPair(SymmetricEncryption symmetricEncryption) {
        try {
            PublicKey publicKey = getPublicKey(symmetricEncryption);
            PrivateKey privateKey = getPrivateKey(symmetricEncryption);
            return new KeyPair(publicKey, privateKey);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public KeyPair readKeyPair() {
        try {
            PublicKey publicKey = getPublicKey();
            PrivateKey privateKey = getPrivateKey();
            return new KeyPair(publicKey, privateKey);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void writeToFile(KeyPair keyPair, SymmetricEncryption symmetricEncryption) {
        String publicText = keyConstructor.publicKeyToString(keyPair.getPublic());
        byte[] encryptedPublic = symmetricEncryption.encryptFromString(publicText);
        String privateText = keyConstructor.privateKeyToString(keyPair.getPrivate());
        byte[] encryptedPrivate = symmetricEncryption.encryptFromString(privateText);
        try {
            Files.write(Paths.get(PUBLIC_KEY_FILE_NAME), encryptedPublic);
            Files.write(Paths.get(PRIVATE_KEY_FILE_NAME), encryptedPrivate);
        } catch (IOException e) {
            logger.error("There was an error writing the rsa keys to file", e);
        }
    }

    @Override
    public void writeToFile(KeyPair keyPair) {
        String publicText = keyConstructor.publicKeyToString(keyPair.getPublic());
        String privateText = keyConstructor.privateKeyToString(keyPair.getPrivate());
        try {
            Files.writeString(Paths.get(PUBLIC_KEY_FILE_NAME), publicText);
            Files.writeString(Paths.get(PRIVATE_KEY_FILE_NAME), privateText);
        } catch (IOException e) {
            logger.error("There was an error writing the rsa keys to file", e);
        }
    }

    private PublicKey getPublicKey(SymmetricEncryption symmetricEncryption) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(PUBLIC_KEY_FILE_NAME));
        String decryptedContent = symmetricEncryption.decryptToString(fileContent);
        if (decryptedContent.isEmpty()) return null;
        return keyConstructor.constructPublicKeyFromString(decryptedContent);
    }

    private PrivateKey getPrivateKey(SymmetricEncryption symmetricEncryption) throws IOException {
        byte[] fileContent = Files.readAllBytes(Paths.get(PRIVATE_KEY_FILE_NAME));
        String decryptedContent = symmetricEncryption.decryptToString(fileContent);
        if (decryptedContent.isEmpty()) return null;
        return keyConstructor.constructPrivateKeyFromString(decryptedContent);
    }

    private PublicKey getPublicKey() throws IOException {
        String fileContent = Files.readString(Paths.get(PUBLIC_KEY_FILE_NAME));
        return keyConstructor.constructPublicKeyFromString(fileContent);
    }

    private PrivateKey getPrivateKey() throws IOException {
        String fileContent = Files.readString(Paths.get(PRIVATE_KEY_FILE_NAME));
        return keyConstructor.constructPrivateKeyFromString(fileContent);
    }
}
