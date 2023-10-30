package it.marcodemartino.yapga.client.ui.scenes;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

public class ScenesSwitcher {

    private final Map<String, Pane> scenes;
    private Scene main;

    public ScenesSwitcher() {
        scenes = new HashMap<>();
    }

    public void switchScreen(String name) {
        main.setRoot(scenes.get(name));
    }

    public void addScreen(String name, Pane pane) {
        scenes.put(name, pane);
    }

    public void setMain(Scene main) {
        this.main = main;
    }
}
