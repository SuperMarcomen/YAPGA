package it.marcodemartino.yapga.client.logic.actions;

import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;
import it.marcodemartino.yapga.common.json.RequestRemotePublicKeyObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestRemotePublicKey implements Action {

    private final Logger logger = LogManager.getLogger(RequestRemotePublicKey.class);
    private final OutputEmitter outputEmitter;

    public RequestRemotePublicKey(OutputEmitter outputEmitter) {
        this.outputEmitter = outputEmitter;
    }

    @Override
    public void execute() {
        outputEmitter.sendOutput(new RequestRemotePublicKeyObject());
        logger.info("Requested remote public key");
    }
}
