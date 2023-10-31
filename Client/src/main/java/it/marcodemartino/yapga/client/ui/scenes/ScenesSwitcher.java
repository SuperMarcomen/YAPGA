package it.marcodemartino.yapga.client.ui.scenes;

import it.marcodemartino.yapga.client.logic.results.Result;
import it.marcodemartino.yapga.client.logic.results.ResultBroadcaster;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

public class ScenesSwitcher {

    private final Map<String, Pane> scenes;
    private final ResultBroadcaster resultBroadcaster;
    private Scene main;

    public ScenesSwitcher(ResultBroadcaster resultBroadcaster) {
        this.resultBroadcaster = resultBroadcaster;
        resultBroadcaster.registerListener(Result.CORRECT_MAIN_PASSWORD, () -> switchScreen("gallery"));
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
