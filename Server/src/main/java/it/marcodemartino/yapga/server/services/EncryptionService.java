package it.marcodemartino.yapga.server.services;

import com.google.gson.Gson;
import it.marcodemartino.yapga.common.encryption.asymmetric.AsymmetricEncryption;
import it.marcodemartino.yapga.common.encryption.asymmetric.AsymmetricKeyReader;
import it.marcodemartino.yapga.common.json.*;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PublicKey;

public class EncryptionService {

    private final AsymmetricEncryption localEncryption;
    private final AsymmetricEncryption remoteEncryption;
    private final AsymmetricKeyReader asymmetricKeyReader;
    private final Gson gson;

    public EncryptionService(AsymmetricEncryption localEncryption, AsymmetricEncryption remoteEncryption, AsymmetricKeyReader asymmetricKeyReader) {
        this.localEncryption = localEncryption;
        this.remoteEncryption = remoteEncryption;
        this.asymmetricKeyReader = asymmetricKeyReader;
        gson = GsonInstance.get();
    }

    public boolean verifyOtherSignature(byte[][] toBeChecked, String shouldBe, String otherPubKey) {
        initRemoteCipherWithKey(otherPubKey);
        return remoteEncryption.checkSignature(toBeChecked, shouldBe.getBytes(StandardCharsets.UTF_8), remoteEncryption.getPublicKey());
    }

    public String decryptMessage(byte[][] content) {
        return localEncryption.decryptToString(content);
    }

    public JSONObject encryptWithKeyAndSign(String otherPublicKey, JSONObject object) {
        initRemoteCipherWithKey(otherPublicKey);
        String json = gson.toJson(object);
        byte[][] encryptedJson = remoteEncryption.encryptFromString(json);
        byte[][] signature = localEncryption.signFromString(json);
        return new EncryptedMessageObject(encryptedJson);
    }

    private void initRemoteCipherWithKey(String otherPublicKeyString) {
        PublicKey otherPublicKey = remoteEncryption.constructPublicKeyFromString(otherPublicKeyString);
        remoteEncryption.setKeys(new KeyPair(otherPublicKey, null));
    }

    public String getLocalPublicKeyAsString() {
        return localEncryption.publicKeyToString(localEncryption.getPublicKey());
    }

    public void loadKeys() {
        localEncryption.setKeys(asymmetricKeyReader.readKeyPair());
    }

    public AsymmetricEncryption getLocalEncryption() {
        return localEncryption;
    }
}
