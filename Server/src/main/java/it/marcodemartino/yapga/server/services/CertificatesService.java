package it.marcodemartino.yapga.server.services;

import com.google.gson.Gson;
import it.marcodemartino.yapga.common.certificates.IdentityCertificate;
import it.marcodemartino.yapga.common.encryption.asymmetric.AsymmetricEncryption;
import it.marcodemartino.yapga.common.entities.User;
import it.marcodemartino.yapga.common.json.GsonInstance;

public class CertificatesService {

    private final AsymmetricEncryption asymmetricEncryption;
    private final Gson gson;

    public CertificatesService(AsymmetricEncryption asymmetricEncryption) {
        this.asymmetricEncryption = asymmetricEncryption;
        gson = GsonInstance.get();
    }

    public IdentityCertificate generateCertificate(User user) {
        String userJson = gson.toJson(user);
        byte[][] signedJson = asymmetricEncryption.signFromString(userJson);
        return new IdentityCertificate(user, signedJson);
    }
}
