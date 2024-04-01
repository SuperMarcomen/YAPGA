package it.marcodemartino.yapga.client.ui;


import it.marcodemartino.yapga.client.logic.results.ResultBroadcaster;
import it.marcodemartino.yapga.client.logic.services.CertificatesService;
import it.marcodemartino.yapga.client.logic.services.GalleryService;
import it.marcodemartino.yapga.client.ui.scenes.*;
import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;
import it.marcodemartino.yapga.common.services.EncryptionService;
import it.marcodemartino.yapga.common.services.ImageService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class YAPGAUI extends Application {

    private static OutputEmitter outputEmitter;
    private static ResultBroadcaster resultBroadcaster;
    private static EncryptionService encryptionService;
    private static ImageService imageService;
    private static CertificatesService certificatesService;
    private static GalleryService galleryService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Hi");
        HBox hBox = new HBox(label);
        Scene scene = new Scene(hBox, 1500, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        Font.loadFont(getClass().getResourceAsStream("/nexus.ttf"), 24);

        ScenesSwitcher scenesSwitcher = new ScenesSwitcher(resultBroadcaster);
        scenesSwitcher.addScreen("main_password", new EnterMainPasswordScene(resultBroadcaster, encryptionService));
        scenesSwitcher.addScreen("gallery", new GalleryScene(scene.widthProperty(), scene.heightProperty(), scenesSwitcher, outputEmitter, certificatesService, imageService, galleryService));
        scenesSwitcher.addScreen("albums", new AlbumScene(scene.widthProperty(), scene.heightProperty(), scenesSwitcher));
        scenesSwitcher.setMain(scene);
        scenesSwitcher.switchScreen("main_password");

        primaryStage.setTitle("Cards against humanity");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    public static void setOutputEmitter(OutputEmitter outputEmitter) {
        YAPGAUI.outputEmitter = outputEmitter;
    }

    public static void setResultBroadcaster(ResultBroadcaster resultBroadcaster) {
        YAPGAUI.resultBroadcaster = resultBroadcaster;
    }

    public static void setEncryptionService(EncryptionService encryptionService) {
        YAPGAUI.encryptionService = encryptionService;
    }

    public static void setImageService(ImageService imageService) {
        YAPGAUI.imageService = imageService;
    }

    public static void setCertificatesService(CertificatesService certificatesService) {
        YAPGAUI.certificatesService = certificatesService;
    }

    public static void setGalleryService(GalleryService galleryService) {
        YAPGAUI.galleryService = galleryService;
    }
}
