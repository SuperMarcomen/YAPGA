package it.marcodemartino.yapga.common.application;

import it.marcodemartino.yapga.common.io.EventManager;
import it.marcodemartino.yapga.common.io.emitters.InputEmitter;
import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;

public interface ApplicationIO extends InputEmitter, OutputEmitter {

    EventManager getEventManager();

}
