package it.marcodemartino.yapga.common.json;

import it.marcodemartino.yapga.common.certificates.IdentityCertificate;

public class RequestImagesObject implements JSONObject {

    private final JSONMethods method = JSONMethods.REQUEST_IMAGES;
    private final int quantity;
    private final IdentityCertificate identityCertificate;

    public RequestImagesObject(int quantity, IdentityCertificate identityCertificate) {
        this.quantity = quantity;
        this.identityCertificate = identityCertificate;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public int getQuantity() {
        return quantity;
    }

    public IdentityCertificate getIdentityCertificate() {
        return identityCertificate;
    }
}
