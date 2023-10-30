package it.marcodemartino.yapga.common.entities;

public class User {

    private final String publicKey;

    public User(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
