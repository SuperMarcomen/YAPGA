package it.marcodemartino.yapga.client.ui.scenes;

import it.marcodemartino.yapga.client.logic.actions.Action;
import it.marcodemartino.yapga.client.logic.actions.InputMainPassword;
import it.marcodemartino.yapga.client.logic.results.Result;
import it.marcodemartino.yapga.client.logic.results.ResultBroadcaster;
import it.marcodemartino.yapga.common.services.EncryptionService;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EnterMainPasswordScene extends StackPane {

    private final ResultBroadcaster resultBroadcaster;

    public EnterMainPasswordScene(ResultBroadcaster resultBroadcaster, EncryptionService encryptionService) {
        this.resultBroadcaster = resultBroadcaster;

        TextField passwordField = new TextField();
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnMousePressed(event -> {
            Action action = new InputMainPassword(encryptionService, resultBroadcaster, passwordField.getText());
            action.execute();
        });

        VBox vBox = new VBox(passwordField, confirmButton);
        getChildren().add(vBox);

        resultBroadcaster.registerListener(Result.CORRECT_MAIN_PASSWORD, () -> {
            System.out.println("Password is correct!");
        });

        resultBroadcaster.registerListener(Result.WRONG_MAIN_PASSWORD, () -> {
            System.out.println("Password is wrong!");
        });
    }
}
