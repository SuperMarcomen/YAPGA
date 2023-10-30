package it.marcodemartino.yapga.client.logic.services;

import it.marcodemartino.yapga.client.logic.actions.Action;
import it.marcodemartino.yapga.client.logic.actions.RequestIdentityCertificate;
import it.marcodemartino.yapga.client.logic.certificates.CertificateReaderWriter;
import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;

public class AuthenticationService {

    private final OutputEmitter outputEmitter;
    private final CertificateReaderWriter certificateReaderWriter;
    private final EncryptionService encryptionService;

    public AuthenticationService(OutputEmitter outputEmitter, CertificateReaderWriter certificateReaderWriter, EncryptionService encryptionService) {
        this.outputEmitter = outputEmitter;
        this.certificateReaderWriter = certificateReaderWriter;
        this.encryptionService = encryptionService;
    }

    public void login() {
        if (!certificateReaderWriter.doesCertificateExist()) {
            Action action = new RequestIdentityCertificate(outputEmitter, encryptionService, encryptionService.getLocalPublicKeyAsString());
            action.execute();
        }
    }
}
