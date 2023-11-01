package it.marcodemartino.yapga.client.logic.certificates;

import com.google.gson.Gson;
import it.marcodemartino.yapga.common.certificates.IdentityCertificate;
import it.marcodemartino.yapga.common.encryption.asymmetric.*;
import it.marcodemartino.yapga.common.encryption.symmetric.*;
import it.marcodemartino.yapga.common.encryption.symmetric.aes.AESEncryption;
import it.marcodemartino.yapga.common.json.*;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;

public class EncryptionService2 {

    private final AsymmetricEncryption localSignature;
    private final AsymmetricEncryption remoteEncryption;
    private final SymmetricEncryption localEncryption;
    private final Gson gson;

    public EncryptionService2(AsymmetricEncryption localSignature, AsymmetricEncryption remoteEncryption, SymmetricEncryption localEncryption) {
        this.localSignature = localSignature;
        this.remoteEncryption = remoteEncryption;
        this.localEncryption = localEncryption;
        this.gson = GsonInstance.get();
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

    public JSONObject encryptMessage(JSONObject object) {
        String jsonOfObject = gson.toJson(object);
        byte[][] encryptedJson = remoteEncryption.encryptFromString(jsonOfObject);
        return new EncryptedMessageObject(encryptedJson);
    }

    public boolean inputMainPasswordAndInit(String password) {
        ISymmetricFileHandler symmetricFileHandler = new SymmetricFileHandler(localEncryption);
        SymmetricEncryption keysDecrypter = new AESEncryption(128);

        byte[][] saltAndIv = readOrGenerateSaltAndIv(symmetricFileHandler);

        keysDecrypter.generateKeyFromPassword(password, saltAndIv[0], saltAndIv[1]);
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

    public void setRemotePublicKey(String publicKey) {
        remoteEncryption.setKeys(new KeyPair(localSignature.constructPublicKeyFromString(publicKey), null));
    }

    public String getLocalPublicKeyAsString() {
        return localSignature.publicKeyToString(localSignature.getPublicKey());
    }

    public SymmetricEncryption getLocalEncryption() {
        return localEncryption;
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