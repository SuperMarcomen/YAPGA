package it.marcodemartino.yapga.client.logic.actions;

import it.marcodemartino.yapga.client.logic.services.EncryptionService;
import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;
import it.marcodemartino.yapga.common.json.JSONObject;
import it.marcodemartino.yapga.common.json.RequestIdentityCertificateObject;

public class RequestIdentityCertificate implements Action {

    private final OutputEmitter outputEmitter;
    private final EncryptionService encryptionService;
    private final String publicKey;

    public RequestIdentityCertificate(OutputEmitter outputEmitter, EncryptionService encryptionService, String publicKey) {
        this.outputEmitter = outputEmitter;
        this.encryptionService = encryptionService;
        this.publicKey = publicKey;
    }

    @Override
    public void execute() {
        JSONObject object = new RequestIdentityCertificateObject(publicKey);
        outputEmitter.sendOutput(encryptionService.encryptAndSignMessage(object));
    }
}
