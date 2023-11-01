package it.marcodemartino.yapga.server.commands;

import it.marcodemartino.yapga.common.certificates.IdentityCertificate;
import it.marcodemartino.yapga.common.commands.JsonCommand;
import it.marcodemartino.yapga.common.entities.User;
import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;
import it.marcodemartino.yapga.common.json.*;
import it.marcodemartino.yapga.common.services.EncryptionService;
import it.marcodemartino.yapga.server.services.CertificatesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class SendIdentityCertificateCommand extends JsonCommand<RequestIdentityCertificateObject> {

    private final Logger logger = LogManager.getLogger(SendIdentityCertificateCommand.class);
    private final OutputEmitter outputEmitter;
    private final EncryptionService encryptionService;
    private final CertificatesService certificatesService;

    public SendIdentityCertificateCommand(OutputEmitter outputEmitter, EncryptionService encryptionService, CertificatesService certificatesService) {
        super(RequestIdentityCertificateObject.class);
        this.outputEmitter = outputEmitter;
        this.encryptionService = encryptionService;
        this.certificatesService = certificatesService;
    }

    @Override
    protected void execute(RequestIdentityCertificateObject requestIdentityCertificateObject) {
        logger.info("Sending identity certificate to a user");
        User user = new User(UUID.randomUUID(), requestIdentityCertificateObject.getPublicKey());
        IdentityCertificate identityCertificate = certificatesService.generateCertificate(user);
        JSONObject object = new SendIdentityCerificateObject(identityCertificate);
        outputEmitter.sendOutput(encryptionService.encryptMessageWithKey(user.getPublicKey(), object));
    }
}
