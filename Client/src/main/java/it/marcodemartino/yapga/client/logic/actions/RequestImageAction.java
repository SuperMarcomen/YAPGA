package it.marcodemartino.yapga.client.logic.actions;

import it.marcodemartino.yapga.client.logic.services.CertificatesService;
import it.marcodemartino.yapga.common.certificates.IdentityCertificate;
import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;
import it.marcodemartino.yapga.common.json.JSONObject;
import it.marcodemartino.yapga.common.json.RequestImagesObject;
import it.marcodemartino.yapga.common.services.EncryptionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestImageAction implements Action {

    private final Logger logger = LogManager.getLogger(RequestImageAction.class);
    private final OutputEmitter outputEmitter;
    private final CertificatesService certificatesService;
    private final EncryptionService encryptionService;
    private final int amount;

    public RequestImageAction(OutputEmitter outputEmitter, CertificatesService certificatesService, EncryptionService encryptionService, int amount) {
        this.outputEmitter = outputEmitter;
        this.certificatesService = certificatesService;
        this.encryptionService = encryptionService;
        this.amount = amount;
    }

    @Override
    public void execute() {
        IdentityCertificate identityCertificate = certificatesService.getIdentityCertificate();
        JSONObject object = new RequestImagesObject(amount, identityCertificate);
        outputEmitter.sendOutput(encryptionService.encryptSignAndCertifySignMessage(object, identityCertificate));
    }
}
