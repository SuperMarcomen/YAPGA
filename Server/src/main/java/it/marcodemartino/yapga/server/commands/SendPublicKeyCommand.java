package it.marcodemartino.yapga.server.commands;

import it.marcodemartino.yapga.common.commands.JsonCommand;
import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;
import it.marcodemartino.yapga.common.json.*;
import it.marcodemartino.yapga.common.services.EncryptionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SendPublicKeyCommand extends JsonCommand<RequestRemotePublicKeyObject> {

    private final Logger logger = LogManager.getLogger(SendPublicKeyCommand.class);
    private final OutputEmitter outputEmitter;
    private final EncryptionService encryptionService;

    public SendPublicKeyCommand(OutputEmitter outputEmitter, EncryptionService encryptionService) {
        super(RequestRemotePublicKeyObject.class);
        this.outputEmitter = outputEmitter;
        this.encryptionService = encryptionService;
    }

    @Override
    protected void execute(RequestRemotePublicKeyObject requestRemotePublicKeyObject) {
        JSONObject object = new SendRemotePublicKeyObject(encryptionService.getLocalPublicKeyAsString());
        outputEmitter.sendOutput(object);
        logger.info("Sent the public key to a client");
    }
}
