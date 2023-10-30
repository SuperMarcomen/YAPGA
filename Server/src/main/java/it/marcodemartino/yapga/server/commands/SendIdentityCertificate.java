package it.marcodemartino.yapga.server.commands;

import it.marcodemartino.yapga.common.commands.JsonCommand;
import it.marcodemartino.yapga.common.json.RequestIdentityCertificateObject;
import it.marcodemartino.yapga.server.services.CertificatesService;

public class SendIdentityCertificate extends JsonCommand<RequestIdentityCertificateObject> {

    private final CertificatesService certificatesService;

    public SendIdentityCertificate(CertificatesService certificatesService) {
        super(RequestIdentityCertificateObject.class);
        this.certificatesService = certificatesService;
    }

    @Override
    protected void execute(RequestIdentityCertificateObject requestIdentityCertificateObject) {

    }
}
