package it.marcodemartino.yapga.client.logic.socket.io;

import com.google.gson.Gson;
import it.marcodemartino.yapga.common.application.ApplicationIO;
import it.marcodemartino.yapga.common.io.EventManager;
import it.marcodemartino.yapga.common.io.listeners.InputListener;
import it.marcodemartino.yapga.common.json.GsonInstance;
import it.marcodemartino.yapga.common.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class SocketIO implements ApplicationIO {

    private static final String QUIT_MESSAGE = "quit";
    private final Logger logger = LogManager.getLogger(SocketIO.class);
    private final EventManager eventManager;
    private final BufferedReader in;
    private final PrintWriter out;
    private final OutputStream rawOut;
    private final Gson gson;
    private boolean running;

    public SocketIO(InputStream inputStream, OutputStream outputStream) {
        this.in = new BufferedReader(new InputStreamReader(inputStream));
        this.out = new PrintWriter(outputStream, true);
        this.rawOut = outputStream;
        this.eventManager = new EventManager();
        this.gson = GsonInstance.get();
        this.running = true;
    }

    @Override
    public void start() {
        logger.info("Starting to listen for inputs from the server");
        while (running) {
            String input = tryGetInput();
            if (input.equals(QUIT_MESSAGE)) return;

            eventManager.notifyInputListeners(input);
        }
    }

    @Override
    public void stop() {
        running = false;
        tryToCloseInput();
        out.close();
    }

    @Override
    public void sendOutput(JSONObject object) {
        String output = gson.toJson(object);
        out.println(output);
    }

    @Override
    public void sendRaw(byte[] bytes, int position, int length) {
        try {
            rawOut.write(bytes, position, length);
        } catch (IOException e) {
            logger.warn("There was an error sending raw data to the server", e);
        }
    }

    @Override
    public void registerInputListener(InputListener inputListener) {
        eventManager.registerInputListener(inputListener);
    }

    private String tryGetInput() {
        try {
            return in.readLine();
        } catch (IOException e) {
            if (!running) return QUIT_MESSAGE;
            if (e.getMessage().equals("Connection reset")) {
                logger.warn("The server shut down unexpectedly! Closing the app");
                System.exit(1);
            }
            throw new RuntimeException(e);
        }
    }

    private void tryToCloseInput() {
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }
}
