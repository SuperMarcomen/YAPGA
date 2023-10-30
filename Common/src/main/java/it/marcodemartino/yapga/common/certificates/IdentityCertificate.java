package it.marcodemartino.yapga.common.certificates;

import it.marcodemartino.yapga.common.entities.User;

public class IdentityCertificate {

    private final User user;
    private final byte[][] signature;

    public IdentityCertificate(User user, byte[][] signature) {
        this.user = user;
        this.signature = signature;
    }

    public User getUser() {
        return user;
    }

    public byte[][] getSignature() {
        return signature;
    }
}
