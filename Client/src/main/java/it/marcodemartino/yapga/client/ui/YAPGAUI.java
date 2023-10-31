package it.marcodemartino.yapga.client.ui;


import it.marcodemartino.yapga.client.logic.results.ResultBroadcaster;
import it.marcodemartino.yapga.client.logic.services.EncryptionService;
import it.marcodemartino.yapga.client.ui.scenes.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class YAPGAUI extends Application {

    private static ResultBroadcaster resultBroadcaster;
    private static EncryptionService encryptionService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Hi");
        HBox hBox = new HBox(label);
        Scene scene = new Scene(hBox, 1500, 800);


        ScenesSwitcher scenesSwitcher = new ScenesSwitcher(resultBroadcaster);
        scenesSwitcher.addScreen("main_password", new EnterMainPasswordScene(resultBroadcaster, encryptionService));
        scenesSwitcher.addScreen("gallery", new GalleryScene());
        scenesSwitcher.setMain(scene);
        scenesSwitcher.switchScreen("main_password");

        primaryStage.setTitle("Cards against humanity");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void setResultBroadcaster(ResultBroadcaster resultBroadcaster) {
        YAPGAUI.resultBroadcaster = resultBroadcaster;
    }

    public static void setEncryptionService(EncryptionService encryptionService) {
        YAPGAUI.encryptionService = encryptionService;
    }
}
