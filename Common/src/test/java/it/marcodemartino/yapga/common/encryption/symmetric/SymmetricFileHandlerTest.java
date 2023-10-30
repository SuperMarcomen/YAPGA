package it.marcodemartino.yapga.common.encryption.symmetric;

import it.marcodemartino.yapga.common.encryption.symmetric.aes.AESEncryption;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class SymmetricFileHandlerTest {

    @Test
    void writeKey() throws IOException {
        Files.deleteIfExists(Paths.get("aes_key.pem"));
        SymmetricEncryption symmetricEncryption = new AESEncryption(128);
        symmetricEncryption.generateKey();
        SymmetricKeyContainer keyContainer = symmetricEncryption.getKey();
        ISymmetricFileHandler symmetricFileHandler = new SymmetricFileHandler(symmetricEncryption);
        assertFalse(symmetricFileHandler.doesKeyExist());
        symmetricFileHandler.writeKey(keyContainer);
        assertTrue(symmetricFileHandler.doesKeyExist());
        SymmetricKeyContainer readKey = symmetricFileHandler.readKey();
        assertArrayEquals(keyContainer.getSecretKey().getEncoded(), readKey.getSecretKey().getEncoded());
        assertArrayEquals(keyContainer.getIV().getIV(), readKey.getIV().getIV());
    }

    @Test
    void writeKeyEncrypted() throws IOException {
        Files.deleteIfExists(Paths.get("aes_key.pem"));
        SymmetricEncryption symmetricEncryption = new AESEncryption(128);
        symmetricEncryption.generateKey();

        SymmetricEncryption otherEncryption = new AESEncryption(128);
        otherEncryption.generateKey();

        SymmetricKeyContainer keyContainer = symmetricEncryption.getKey();
        ISymmetricFileHandler symmetricFileHandler = new SymmetricFileHandler(symmetricEncryption);
        assertFalse(symmetricFileHandler.doesKeyExist());
        symmetricFileHandler.writeKeyEncrypted(keyContainer, otherEncryption);
        assertTrue(symmetricFileHandler.doesKeyExist());
        SymmetricKeyContainer readKey = symmetricFileHandler.readKeyEncrypted(otherEncryption);
        assertArrayEquals(keyContainer.getSecretKey().getEncoded(), readKey.getSecretKey().getEncoded());
        assertArrayEquals(keyContainer.getIV().getIV(), readKey.getIV().getIV());
    }

    @Test
    void writeSalt() throws IOException {
        Path saltPath = Paths.get("main_salt.pem");
        Files.deleteIfExists(saltPath);
        SymmetricEncryption symmetricEncryption = new AESEncryption(128);
        byte[][] salt = symmetricEncryption.generateSaltAndIv();
        assertFalse(Files.exists(saltPath));
        ISymmetricFileHandler symmetricFileHandler = new SymmetricFileHandler(symmetricEncryption);
        symmetricFileHandler.writeSaltAndIv(salt);
        assertTrue(Files.exists(saltPath));
        byte[][] readSalt = symmetricFileHandler.readSaltAndIv();
        assertArrayEquals(salt, readSalt);
    }
}