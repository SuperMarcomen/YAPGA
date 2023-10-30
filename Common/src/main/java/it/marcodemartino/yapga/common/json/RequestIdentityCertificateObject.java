package it.marcodemartino.yapga.common.json;

public class RequestIdentityCertificateObject implements JSONObject {

    private final JSONMethods method = JSONMethods.REQUEST_IDENTITY_CERTIFICATE;
    private final String publicKey;

    public RequestIdentityCertificateObject(String publicKey) {
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
