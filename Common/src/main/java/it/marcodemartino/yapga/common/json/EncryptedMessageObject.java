package it.marcodemartino.yapga.common.json;

public class EncryptedMessageObject implements JSONObject {

    private final JSONMethods method = JSONMethods.ENCRYPTED_MESSAGE;
    private final byte[][] encryptedMessage;

    public EncryptedMessageObject(byte[][] encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public byte[][] getEncryptedMessage() {
        return encryptedMessage;
    }

}
