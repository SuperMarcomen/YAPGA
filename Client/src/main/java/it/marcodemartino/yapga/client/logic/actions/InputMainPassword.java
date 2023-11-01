package it.marcodemartino.yapga.client.logic.actions;

import it.marcodemartino.yapga.client.logic.results.Result;
import it.marcodemartino.yapga.client.logic.results.ResultBroadcaster;
import it.marcodemartino.yapga.common.services.EncryptionService;

public class InputMainPassword implements Action {

    private final EncryptionService encryptionService;
    private final ResultBroadcaster resultBroadcaster;
    private final String password;

    public InputMainPassword(EncryptionService encryptionService, ResultBroadcaster resultBroadcaster, String password) {
        this.encryptionService = encryptionService;
        this.resultBroadcaster = resultBroadcaster;
        this.password = password;
    }

    @Override
    public void execute() {
        boolean result = encryptionService.inputMainPasswordAndInit(password);

        if (result) {
            resultBroadcaster.notify(Result.CORRECT_MAIN_PASSWORD);
        } else {
            resultBroadcaster.notify(Result.WRONG_MAIN_PASSWORD);
        }
    }
}
