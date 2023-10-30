package it.marcodemartino.yapga.server.commands;

import it.marcodemartino.yapga.common.commands.JsonCommand;
import it.marcodemartino.yapga.common.json.EncryptedSignedMessageObject;
import it.marcodemartino.yapga.server.services.EncryptionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EncryptedSignedMessage extends JsonCommand<EncryptedSignedMessageObject> {

    private final Logger logger = LogManager.getLogger(EncryptedSignedMessage.class);
    private final EncryptionService encryptionService;

    public EncryptedSignedMessage(EncryptionService encryptionService) {
        super(EncryptedSignedMessageObject.class);
        this.encryptionService = encryptionService;
    }

    @Override
    protected void execute(EncryptedSignedMessageObject encryptedSignedMessageObject) {
        logger.info("Received an encrypted and signed message");
    }
}
