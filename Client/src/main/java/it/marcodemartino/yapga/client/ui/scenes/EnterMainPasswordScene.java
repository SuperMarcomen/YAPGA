package it.marcodemartino.yapga.client.ui.scenes;

import it.marcodemartino.yapga.client.logic.actions.Action;
import it.marcodemartino.yapga.client.logic.actions.InputMainPassword;
import it.marcodemartino.yapga.client.logic.results.Result;
import it.marcodemartino.yapga.client.logic.results.ResultBroadcaster;
import it.marcodemartino.yapga.common.services.EncryptionService;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

public class EnterMainPasswordScene extends StackPane {

    public EnterMainPasswordScene(ResultBroadcaster resultBroadcaster, EncryptionService encryptionService) {
        VBox vBox = new VBox();

        Label yapgaLogo = new Label("YAPGA");
        yapgaLogo.setId("logo");

        Label passwordLabel = new Label("Password");
        passwordLabel.setId("password_label");

        TextField passwordField = new TextField();
        passwordField.setId("password_input");

        Button confirmButton = new Button("Confirm");
        confirmButton.setId("login_button");
        confirmButton.prefWidthProperty().bind(vBox.widthProperty());

        passwordField.setOnKeyPressed(event -> {
            if (!event.getCode().equals(KeyCode.ENTER)) return;
            tryLogin(resultBroadcaster, encryptionService, passwordField);
        });
        confirmButton.setOnMousePressed(event -> {
            tryLogin(resultBroadcaster, encryptionService, passwordField);
        });

        Label wrongPassword = new Label("Wrong password!");
        wrongPassword.getStyleClass().add("error_text");
        wrongPassword.setVisible(false);

        VBox passwordInputContainer = new VBox(passwordLabel, passwordField, wrongPassword);
        passwordInputContainer.setSpacing(5);

        vBox.getChildren().addAll(yapgaLogo, passwordInputContainer, confirmButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(100);

        HBox container = new HBox(vBox);
        container.setAlignment(Pos.CENTER);
        getChildren().add(container);

        resultBroadcaster.registerListener(Result.CORRECT_MAIN_PASSWORD, () -> {
            System.out.println("Password is correct!");
            wrongPassword.setVisible(false);
        });

        resultBroadcaster.registerListener(Result.WRONG_MAIN_PASSWORD, () -> {
            System.out.println("Password is wrong!");
            wrongPassword.setVisible(true);
        });
    }

    private void tryLogin(ResultBroadcaster resultBroadcaster, EncryptionService encryptionService, TextField passwordField) {
        Action action = new InputMainPassword(encryptionService, resultBroadcaster, passwordField.getText());
        action.execute();
    }
}
