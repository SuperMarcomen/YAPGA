package it.marcodemartino.yapga.common.entities;

import java.util.UUID;

public class User {

    private final UUID uuid;
    private final String publicKey;

    public User(UUID uuid, String publicKey) {
        this.uuid = uuid;
        this.publicKey = publicKey;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
