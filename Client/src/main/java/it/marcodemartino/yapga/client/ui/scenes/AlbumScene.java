package it.marcodemartino.yapga.client.ui.scenes;

import it.marcodemartino.yapga.client.ui.scenes.elements.SideMenu;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class AlbumScene extends StackPane {

    public AlbumScene(ReadOnlyDoubleProperty widthProp, ReadOnlyDoubleProperty heightProp, ScenesSwitcher scenesSwitcher) {
        prefWidthProperty().bind(widthProp);
        prefHeightProperty().bind(heightProp);
        VBox sideMenu = new SideMenu(false, true, scenesSwitcher, prefWidthProperty());
        HBox container = new HBox(sideMenu, new Label("Yet to be implemented"));
        container.setSpacing(20);
        getChildren().add(container);
    }
}
