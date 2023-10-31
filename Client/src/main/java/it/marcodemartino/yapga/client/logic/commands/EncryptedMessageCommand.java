package it.marcodemartino.yapga.client.logic.commands;

import it.marcodemartino.yapga.client.logic.services.EncryptionService;
import it.marcodemartino.yapga.common.commands.JsonCommand;
import it.marcodemartino.yapga.common.io.EventManager;
import it.marcodemartino.yapga.common.json.EncryptedMessageObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EncryptedMessageCommand extends JsonCommand<EncryptedMessageObject> {


    private final Logger logger = LogManager.getLogger(EncryptedMessageCommand.class);
    private final EventManager inputEmitter;
    private final EncryptionService encryptionService;

    public EncryptedMessageCommand(EventManager inputEmitter, EncryptionService encryptionService) {
        super(EncryptedMessageObject.class);
        this.inputEmitter = inputEmitter;
        this.encryptionService = encryptionService;
    }

    @Override
    protected void execute(EncryptedMessageObject encryptedMessageObject) {
        logger.info("Received an encrypted message");
        String decryptedMessage = encryptionService.decryptMessage(encryptedMessageObject.getEncryptedMessage());
        inputEmitter.notifyInputListeners(decryptedMessage);
    }
}
