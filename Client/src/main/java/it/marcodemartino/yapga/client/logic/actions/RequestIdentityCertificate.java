package it.marcodemartino.yapga.client.logic.actions;

import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;
import it.marcodemartino.yapga.common.json.JSONObject;
import it.marcodemartino.yapga.common.json.RequestIdentityCertificateObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestIdentityCertificate implements Action {

    private final Logger logger = LogManager.getLogger(RequestRemotePublicKey.class);
    private final OutputEmitter outputEmitter;
    private final String publicKey;

    public RequestIdentityCertificate(OutputEmitter outputEmitter, String publicKey) {
        this.outputEmitter = outputEmitter;
        this.publicKey = publicKey;
    }

    @Override
    public void execute() {
        JSONObject object = new RequestIdentityCertificateObject(publicKey);
        outputEmitter.sendOutput(object);
        logger.info("Requested identity certificate");
    }
}
