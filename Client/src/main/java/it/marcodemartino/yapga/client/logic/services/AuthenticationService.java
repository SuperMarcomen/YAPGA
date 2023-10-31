package it.marcodemartino.yapga.client.logic.services;

import it.marcodemartino.yapga.client.logic.actions.Action;
import it.marcodemartino.yapga.client.logic.actions.RequestIdentityCertificate;
import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;

public class AuthenticationService {

    private final OutputEmitter outputEmitter;
    private final EncryptionService encryptionService;
    private final CertificatesService certificatesService;

    public AuthenticationService(OutputEmitter outputEmitter, EncryptionService encryptionService, CertificatesService certificatesService) {
        this.outputEmitter = outputEmitter;
        this.encryptionService = encryptionService;
        this.certificatesService = certificatesService;
    }

    public void login() {
        if (!certificatesService.doesCertificateExist()) {
            Action action = new RequestIdentityCertificate(outputEmitter, encryptionService, encryptionService.getLocalPublicKeyAsString());
            action.execute();
        } else {
            certificatesService.readCertificate();
        }
    }
}
