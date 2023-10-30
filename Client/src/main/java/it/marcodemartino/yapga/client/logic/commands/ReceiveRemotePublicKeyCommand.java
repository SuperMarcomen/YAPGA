package it.marcodemartino.yapga.client.logic.commands;

import it.marcodemartino.yapga.client.logic.services.EncryptionService;
import it.marcodemartino.yapga.common.commands.JsonCommand;
import it.marcodemartino.yapga.common.json.SendRemotePublicKeyObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReceiveRemotePublicKeyCommand extends JsonCommand<SendRemotePublicKeyObject> {

    private final Logger logger = LogManager.getLogger(ReceiveRemotePublicKeyCommand.class);
    private final EncryptionService encryptionService;

    public ReceiveRemotePublicKeyCommand(EncryptionService encryptionService) {
        super(SendRemotePublicKeyObject.class);
        this.encryptionService = encryptionService;
    }

    @Override
    protected void execute(SendRemotePublicKeyObject sendRemotePublicKeyObject) {
        encryptionService.setRemotePublicKey(sendRemotePublicKeyObject.getPublicKey());
        logger.info("Received the remote public key");
    }
}
