package it.marcodemartino.yapga.common.json;

public class SendRemotePublicKeyObject implements JSONObject {

    private final JSONMethods method = JSONMethods.SEND_REMOTE_PUBLIC_KEY;
    private final String publicKey;

    public SendRemotePublicKeyObject(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
