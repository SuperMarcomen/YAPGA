package it.marcodemartino.yapga.common.application;

import java.io.InputStream;

public interface Application extends Runnable {

    InputStream getInputStream();
    ApplicationIO getIO();
    void stop();

}
