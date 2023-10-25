package it.marcodemartino.yapga.common.io.emitters;

import it.marcodemartino.yapga.common.Startable;
import it.marcodemartino.yapga.common.io.listeners.InputListener;

public interface InputEmitter extends Startable {

    void registerInputListener(InputListener inputListener);

}
