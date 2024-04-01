package it.marcodemartino.yapga.client.ui.scenes.elements;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MenuButton extends Button {

    private final boolean active;

    public MenuButton(String text, String iconPath, boolean active, ReadOnlyDoubleProperty widthProperty, Runnable onClick) {
        super(text);
        this.active = active;
        prefWidthProperty().bind(widthProperty.multiply(.8));
        getStyleClass().add("side_menu_button");

        Image icon = new Image(getClass().getResource(iconPath).toExternalForm());
        setGraphic(new ImageView(icon));

        if (active) {
            getStyleClass().add("active_side_menu_button");
        }

        setOnAction((event) -> {
            if (this.active) return;
            onClick.run();
        });
    }
}
