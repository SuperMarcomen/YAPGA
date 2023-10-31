package it.marcodemartino.yapga.common.json;

import it.marcodemartino.yapga.common.certificates.IdentityCertificate;

public class SendIdentityCerificateObject implements JSONObject {

    private final JSONMethods method = JSONMethods.SEND_IDENTITY_CERTIFICATE;
    private final IdentityCertificate identityCertificate;

    public SendIdentityCerificateObject(IdentityCertificate identityCertificate) {
        this.identityCertificate = identityCertificate;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public IdentityCertificate getIdentityCertificate() {
        return identityCertificate;
    }
}
