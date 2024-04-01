package it.marcodemartino.yapga.common.encryption.symmetric.aes;

import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricEncryption;
import it.marcodemartino.yapga.common.encryption.symmetric.SymmetricFileHandler;
import org.junit.jupiter.api.Test;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AESEncryptionTest {

    @Test
    void encrypt() {
        SymmetricEncryption aesEncryption = new AESEncryption(128);
        aesEncryption.generateKey();
        String input = "Sono un ragazzo molto bello";
        byte[] encryptedInput = aesEncryption.encryptFromString(input);
        String decryptedInput = aesEncryption.decryptToString(encryptedInput);
        assertEquals(input, decryptedInput);
    }

    @Test
    void generateKeyFromPassword() {
        SymmetricEncryption aesEncryption = new AESEncryption(128);
        byte[][] saltAndIv = aesEncryption.generateSaltAndIv();
        aesEncryption.generateKeyFromPassword("123Sorella!", saltAndIv[0], saltAndIv[1]);
        String input = "Sono un ragazzo molto bello";
        byte[] encryptedInput = aesEncryption.encryptFromString(input);
        aesEncryption.generateKeyFromPassword("123Sorella!", saltAndIv[0], saltAndIv[1]);
        String decryptedInput = aesEncryption.decryptToString(encryptedInput);
        assertEquals(input, decryptedInput);
    }

    @Test
    void encryptAndDecryptFile() throws IOException {
        SymmetricEncryption encryption = new AESEncryption(128);
        encryption.generateKey();

        Path path = Paths.get("1.jpeg");
        InputStream inputStream = Files.newInputStream(path);

        // Create temporary file to write encrypted content
        Path encryptedFile = getEncryptedFile(encryption, inputStream);

        Path output = Paths.get("1-decrypted.jpeg");
        decryptFile(output, encryption, encryptedFile);
    }

    @Test
    void encryptOtherFile() throws IOException {
        SymmetricEncryption encryption = new AESEncryption(128);
        SymmetricFileHandler symmetricFileHandler = new SymmetricFileHandler(encryption);
        byte[][] bytes = symmetricFileHandler.readSaltAndIv();
        encryption.generateKeyFromPassword("123", bytes[0], bytes[1]);

        byte[] allBytes = Files.readAllBytes(Paths.get("1.jpeg"));
        byte[] dec = encryption.encrypt(allBytes);
        Files.write(Paths.get("1-test-enc.jpeg"), dec);
    }

    @Test
    void decryptOtherFile() throws IOException {
        SymmetricEncryption encryption = new AESEncryption(128);
        SymmetricFileHandler symmetricFileHandler = new SymmetricFileHandler(encryption);
        byte[][] bytes = symmetricFileHandler.readSaltAndIv();
        encryption.generateKeyFromPassword("123", bytes[0], bytes[1]);

        byte[] allBytes = Files.readAllBytes(Paths.get("1-test.jpeg"));
        byte[] dec = encryption.decrypt(allBytes);
        Files.write(Paths.get("1-test-dec.jpeg"), dec);
    }



    private static void decryptFile(Path output, SymmetricEncryption encryption, Path encryptedFile) {
        try (FileChannel fileChannel = FileChannel.open(encryptedFile, StandardOpenOption.READ);
             InputStream inputStream2 = Channels.newInputStream(fileChannel);
             CipherInputStream cipherInputStream = new CipherInputStream(inputStream2, encryption.getDecryptCipher());
             FileOutputStream fileOutputStream = new FileOutputStream(output.toAbsolutePath().toString());
        ) {

            byte[] buffer = new byte[4096];
            int length;
            while ((length = cipherInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static Path getEncryptedFile(SymmetricEncryption encryption, InputStream inputStream) throws IOException {
        Path encryptedFile = Files.createFile(Paths.get("1-enc.jpg"));
        File tempFile = encryptedFile.toFile();

        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            // Create CipherOutputStream to encrypt data as it's being written
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, encryption.getEncryptCipher());
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                cipherOutputStream.write(buffer, 0, length);
            }
            cipherOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return encryptedFile;
    }


}