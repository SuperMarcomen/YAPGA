package it.marcodemartino.yapga.client.ui.scenes.elements;

import it.marcodemartino.yapga.client.ui.scenes.ScenesSwitcher;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SideMenu extends VBox {

    public SideMenu(boolean galleryActive, boolean albumActive, ScenesSwitcher scenesSwitcher, DoubleProperty doubleProperty) {
        Label logo = new Label("YAPGA");
        logo.setId("gallery_logo");
        HBox logoWrapper = new HBox(logo);
        logoWrapper.setAlignment(Pos.CENTER);

        MenuButton gallery = new MenuButton("Gallery", "/gallery_icon.png", galleryActive, widthProperty(), () -> scenesSwitcher.switchScreen("gallery"));
        MenuButton album = new MenuButton("Album", "/album_icon.png", albumActive, widthProperty(), () -> scenesSwitcher.switchScreen("albums"));

        getChildren().addAll(logoWrapper, gallery, album);
        setSpacing(20);
        prefWidthProperty().bind(doubleProperty.divide(5));
        setId("gallery_side_menu");
    }
}
