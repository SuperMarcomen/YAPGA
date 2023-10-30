package it.marcodemartino.yapga.common.json;

import it.marcodemartino.yapga.common.certificates.IdentityCertificate;

public class EncryptedSignedMessageObject implements JSONObject {

    private final JSONMethods method = JSONMethods.ENCRYPTED_SIGNED_MESSAGE;
    private final IdentityCertificate identityCertificate;
    private final byte[][] encryptedMessage;
    private final byte[][] signature;

    public EncryptedSignedMessageObject(IdentityCertificate identityCertificate, byte[][] encryptedMessage, byte[][] signature) {
        this.identityCertificate = identityCertificate;
        this.encryptedMessage = encryptedMessage;
        this.signature = signature;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public byte[][] getEncryptedMessage() {
        return encryptedMessage;
    }

    public byte[][] getSignature() {
        return signature;
    }

    public IdentityCertificate getIdentityCertificate() {
        return identityCertificate;
    }
}
