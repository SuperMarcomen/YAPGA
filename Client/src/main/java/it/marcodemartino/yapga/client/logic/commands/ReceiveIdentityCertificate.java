package it.marcodemartino.yapga.client.logic.commands;

import it.marcodemartino.yapga.client.logic.services.CertificatesService;
import it.marcodemartino.yapga.client.logic.services.EncryptionService;
import it.marcodemartino.yapga.common.certificates.IdentityCertificate;
import it.marcodemartino.yapga.common.commands.JsonCommand;
import it.marcodemartino.yapga.common.json.SendIdentityCerificateObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReceiveIdentityCertificate extends JsonCommand<SendIdentityCerificateObject> {

    private final Logger logger = LogManager.getLogger(ReceiveIdentityCertificate.class);
    private final EncryptionService encryptionService;
    private final CertificatesService certificatesService;

    public ReceiveIdentityCertificate(EncryptionService encryptionService, CertificatesService certificatesService) {
        super(SendIdentityCerificateObject.class);
        this.encryptionService = encryptionService;
        this.certificatesService = certificatesService;
    }

    @Override
    protected void execute(SendIdentityCerificateObject sendIdentityCerificateObject) {
        logger.info("Received and identity certificate");
        IdentityCertificate identityCertificate = sendIdentityCerificateObject.getIdentityCertificate();
        boolean valid = encryptionService.verifyIdentityCertificate(identityCertificate);
        if (!valid) {
            logger.warn("The signature of the identity certificate is invalid!");
            return;
        }

        certificatesService.writeCertificate(identityCertificate);
    }
}
