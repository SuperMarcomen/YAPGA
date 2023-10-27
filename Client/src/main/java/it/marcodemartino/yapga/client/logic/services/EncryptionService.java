package it.marcodemartino.yapga.client.logic.services;

import it.marcodemartino.yapga.common.encryption.asymmetric.*;
import it.marcodemartino.yapga.common.encryption.symmetric.*;
import it.marcodemartino.yapga.common.encryption.symmetric.aes.AESEncryption;

import java.security.KeyPair;

public class EncryptionService {

    private final AsymmetricEncryption localSignature;
    private final AsymmetricEncryption remoteEncryption;
    private final SymmetricEncryption localEncryption;

    public EncryptionService(AsymmetricEncryption localSignature, AsymmetricEncryption remoteEncryption, SymmetricEncryption localEncryption) {
        this.localSignature = localSignature;
        this.remoteEncryption = remoteEncryption;
        this.localEncryption = localEncryption;
    }

    public boolean inputMainPasswordAndInit(String password) {
        ISymmetricFileHandler symmetricFileHandler = new SymmetricFileHandler(localEncryption);
        SymmetricEncryption keysDecrypter = new AESEncryption(128);

        byte[] salt = readOrGenerateSalt(symmetricFileHandler);

        keysDecrypter.generateKeyFromPassword(password, salt);
        IAsymmetricKeyFileHandler asymmetricKeyFileHandler = new AsymmetricKeyFileHandler(localSignature);

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

        return true;
    }

    private byte[] readOrGenerateSalt(ISymmetricFileHandler symmetricFileHandler) {
        byte[] salt;
        if (symmetricFileHandler.doesSaltExist()) {
            salt = symmetricFileHandler.readSalt();
        } else {
            salt = localEncryption.generateSalt();
            symmetricFileHandler.writeSalt(salt);
        }
        return salt;
    }
}
