package it.marcodemartino.yapga.server.handler;

import it.marcodemartino.yapga.common.application.Application;
import it.marcodemartino.yapga.common.application.ApplicationIO;
import it.marcodemartino.yapga.common.commands.JsonCommandManager;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Application {

    private final Socket socket;
    private final ApplicationIO applicationIO;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.applicationIO = new ClientHandlerIO(socket.getInputStream(), socket.getOutputStream());

        JsonCommandManager commandManager = new JsonCommandManager();
        this.applicationIO.registerInputListener(commandManager);
    }

    @Override
    public ApplicationIO getIO() {
        return applicationIO;
    }

    @Override
    public void stop() {
        applicationIO.stop();
        tryClose();
    }

    private void tryClose() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        applicationIO.start();
    }
}
