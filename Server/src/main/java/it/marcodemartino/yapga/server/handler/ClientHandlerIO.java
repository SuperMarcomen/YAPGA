package it.marcodemartino.yapga.server.handler;

import com.google.gson.Gson;
import it.marcodemartino.yapga.common.application.ApplicationIO;
import it.marcodemartino.yapga.common.io.EventManager;
import it.marcodemartino.yapga.common.io.listeners.InputListener;
import it.marcodemartino.yapga.common.json.GsonInstance;
import it.marcodemartino.yapga.common.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class ClientHandlerIO implements ApplicationIO {

    private final Logger logger = LogManager.getLogger(ClientHandlerIO.class);
    private final BufferedReader in;
    private final PrintWriter out;
    private final OutputStream rawOut;
    private final EventManager eventManager;
    private final Gson gson;
    private boolean running;

    public ClientHandlerIO(InputStream inputStream, OutputStream outputStream) {
        this.in = new BufferedReader(new InputStreamReader(inputStream));
        this.out = new PrintWriter(outputStream, true);
        rawOut = outputStream;
        eventManager = new EventManager();
        gson = GsonInstance.get();
        running = true;
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        logger.info("Started listening for inputs from the client");
        while (running) {
            String input = getInput();
            if (!running) return;
            eventManager.notifyInputListeners(input);
        }
    }

    @Override
    public void stop() {
        running = false;
        tryClose();
        out.close();
    }

    @Override
    public void registerInputListener(InputListener inputListener) {
        eventManager.registerInputListener(inputListener);
    }

    private String getInput() {
        try {
            return in.readLine();
        } catch (IOException e) {
            if (e.getMessage().equals("Connection reset")) {
                logger.warn("The client disconnected suddenly! Closing the socket");
                stop();
                return "";
            }
            throw new RuntimeException(e);
        }
    }

    private void tryClose() {
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
