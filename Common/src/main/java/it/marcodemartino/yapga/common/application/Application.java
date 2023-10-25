package it.marcodemartino.yapga.common.application;

public interface Application extends Runnable {

    ApplicationIO getIO();
    void stop();

}
