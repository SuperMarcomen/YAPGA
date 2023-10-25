package it.marcodemartino.yapga.common.encryption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class AsymmetricKeyFileReader implements AsymmetricKeyReader {

    @Override
    public KeyPair readKeyPair(String publicName, String privateName) {
        try {
            PublicKey publicKey = getPublicKey(publicName);
            PrivateKey privateKey = getPrivateKey(privateName);
            return new KeyPair(publicKey, privateKey);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            return null;
        }
    }

    private PublicKey getPublicKey(String publicName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(Files.readAllBytes(Paths.get(publicName)));

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private PrivateKey getPrivateKey(String privateName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(Files.readAllBytes(Paths.get(privateName)));

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
