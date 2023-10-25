package it.marcodemartino.yapga.common.io;

import it.marcodemartino.yapga.common.io.listeners.InputListener;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private final List<InputListener> inputListeners;

    public EventManager() {
        inputListeners = new ArrayList<>();
    }

    public void registerInputListener(InputListener inputListener) {
        inputListeners.add(inputListener);
    }

    public void notifyInputListeners(String input) {
        for (InputListener inputListener : inputListeners) {
            inputListener.notify(input);
        }
    }
}
