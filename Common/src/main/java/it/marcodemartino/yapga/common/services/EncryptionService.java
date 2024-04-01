package it.marcodemartino.yapga.common.services;

import com.google.gson.Gson;
import it.marcodemartino.yapga.common.certificates.IdentityCertificate;
import it.marcodemartino.yapga.common.encryption.asymmetric.*;
import it.marcodemartino.yapga.common.encryption.symmetric.*;
import it.marcodemartino.yapga.common.encryption.symmetric.aes.AESEncryption;
import it.marcodemartino.yapga.common.json.*;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.KeyPair;
import java.security.PublicKey;

public class EncryptionService {

    private final AsymmetricEncryption localSignature;
    private final AsymmetricEncryption remoteEncryption;
    private final SymmetricEncryption localEncryption;
    private final IAsymmetricKeyFileHandler asymmetricKeyFileHandler;
    private final Gson gson;

    public EncryptionService(AsymmetricEncryption localSignature, AsymmetricEncryption remoteEncryption, IAsymmetricKeyFileHandler asymmetricKeyFileHandler) {
        this(localSignature, remoteEncryption, null, asymmetricKeyFileHandler);
    }

    public EncryptionService(AsymmetricEncryption localSignature, AsymmetricEncryption remoteEncryption, SymmetricEncryption localEncryption, IAsymmetricKeyFileHandler asymmetricKeyFileHandler) {
        this.localSignature = localSignature;
        this.remoteEncryption = remoteEncryption;
        this.localEncryption = localEncryption;
        this.asymmetricKeyFileHandler = asymmetricKeyFileHandler;
        this.gson = GsonInstance.get();
    }

    public Path encryptFile(Path input) throws IOException {
        Path encryptedFile = Files.createTempFile("encrypted-file", null);
        System.out.println(encryptedFile.toAbsolutePath());
        File tempFile = encryptedFile.toFile();
        InputStream inputStream = Files.newInputStream(input);

        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            // Create CipherOutputStream to encrypt data as it's being written
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, localEncryption.getEncryptCipher());
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



    public void decryptFile(Path output, Path encryptedFile) {
        try (FileChannel fileChannel = FileChannel.open(encryptedFile, StandardOpenOption.READ);
             InputStream inputStream2 = Channels.newInputStream(fileChannel);
             CipherInputStream cipherInputStream = new CipherInputStream(inputStream2, localEncryption.getDecryptCipher());
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

    public boolean verifyIdentityCertificate(IdentityCertificate identityCertificate) {
        String json = gson.toJson(identityCertificate.getUser());
        return verifyRemoteSignature(identityCertificate.getSignature(), json);
    }

    public boolean verifyRemoteSignature(byte[][] toBeChecked, String shouldBe) {
        return remoteEncryption.checkSignature(toBeChecked, shouldBe.getBytes(StandardCharsets.UTF_8), remoteEncryption.getPublicKey());
    }
    public JSONObject encryptSignAndCertifySignMessage(JSONObject object, IdentityCertificate identityCertificate) {
        String jsonOfObject = gson.toJson(object);
        byte[][] encryptedJson = remoteEncryption.encryptFromString(jsonOfObject);
        byte[][] signature = localSignature.signFromString(jsonOfObject);
        return new EncryptedSignedCertifiedMessageObject(identityCertificate, encryptedJson, signature);
    }

    public JSONObject encryptMessageWithKey(String publicKey, JSONObject object) {
        initRemoteCipherWithKey(publicKey);
        return encryptMessage(object);
    }

    public JSONObject encryptMessage(JSONObject object) {
        String jsonOfObject = gson.toJson(object);
        byte[][] encryptedJson = remoteEncryption.encryptFromString(jsonOfObject);
        return new EncryptedMessageObject(encryptedJson);
    }

    public byte[] encryptBytes(byte[] toEncrypt) {
        return localEncryption.encrypt(toEncrypt);
    }

    public byte[] decryptBytes(byte[] toDecrypt) {
        return localEncryption.decrypt(toDecrypt);
    }

    public boolean inputMainPasswordAndInit(String password) {
        ISymmetricFileHandler symmetricFileHandler = new SymmetricFileHandler(localEncryption);
        SymmetricEncryption keysDecrypter = new AESEncryption(128);

        byte[][] saltAndIv = readOrGenerateSaltAndIv(symmetricFileHandler);

        keysDecrypter.generateKeyFromPassword(password, saltAndIv[0], saltAndIv[1]);

        KeyPair localAsymmetricKeys = null;
        if (asymmetricKeyFileHandler.doKeysExist()) {
            localAsymmetricKeys = asymmetricKeyFileHandler.readKeyPair(keysDecrypter);
            if (localAsymmetricKeys == null) return false;
        }

        SymmetricKeyContainer symmetricKeyContainer = null;
        if (symmetricFileHandler.doesKeyExist()) {
            symmetricKeyContainer = symmetricFileHandler.readKeyEncrypted(keysDecrypter);
            if (symmetricKeyContainer == null) return false;
        }

        // This point is reached only if the password is not wrong
        // But it could be possible that no key exists or one of the keys is missing
        // (I do not generate these before because it could be that one of the keys exists
        // and I still need to check if the password is correct)

        if (localAsymmetricKeys == null) {
            localSignature.generateKeyPair();
            localAsymmetricKeys = localSignature.getKeys();
            asymmetricKeyFileHandler.writeToFile(localAsymmetricKeys, keysDecrypter);
        }

        if (symmetricKeyContainer == null) {
            localEncryption.generateKey();
            symmetricKeyContainer = localEncryption.getKey();
            symmetricFileHandler.writeKeyEncrypted(symmetricKeyContainer, keysDecrypter);
        }


        localSignature.setKeys(localAsymmetricKeys);
        localEncryption.setKey(symmetricKeyContainer);
        System.out.println("AES key: " + localEncryption.keyToString(localEncryption.getKey()));

        return true;
    }

    public void setRemotePublicKey(String publicKey) {
        remoteEncryption.setKeys(new KeyPair(localSignature.constructPublicKeyFromString(publicKey), null));
    }

    public String getLocalPublicKeyAsString() {
        return localSignature.publicKeyToString(localSignature.getPublicKey());
    }

    public AsymmetricEncryption getLocalAsymmetricEncryption() {
        return localSignature;
    }

    public SymmetricEncryption getLocalSymmetricEncryption() {
        return localEncryption;
    }

    public void loadKeys() {
        localSignature.setKeys(asymmetricKeyFileHandler.readKeyPair());
    }

    private void initRemoteCipherWithKey(String otherPublicKeyString) {
        PublicKey otherPublicKey = remoteEncryption.constructPublicKeyFromString(otherPublicKeyString);
        remoteEncryption.setKeys(new KeyPair(otherPublicKey, null));
    }

    private byte[][] readOrGenerateSaltAndIv(ISymmetricFileHandler symmetricFileHandler) {
        byte[][] saltAndIv;
        if (symmetricFileHandler.doesSaltExist()) {
            saltAndIv = symmetricFileHandler.readSaltAndIv();
        } else {
            saltAndIv = localEncryption.generateSaltAndIv();
            symmetricFileHandler.writeSaltAndIv(saltAndIv);
        }
        return saltAndIv;
    }

    public String decryptMessage(byte[][] encryptedMessage) {
        return localSignature.decryptToString(encryptedMessage);
    }

}
