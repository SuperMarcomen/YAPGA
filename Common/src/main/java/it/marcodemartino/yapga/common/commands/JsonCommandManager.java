package it.marcodemartino.yapga.common.commands;

import com.google.gson.Gson;
import it.marcodemartino.yapga.common.io.listeners.InputListener;
import it.marcodemartino.yapga.common.json.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class JsonCommandManager implements InputListener {

    private final Logger logger = LogManager.getLogger(JsonCommandManager.class);
    private final Map<JSONMethods, JsonCommand<?>> commands;
    private final Gson gson;

    public JsonCommandManager() {
        commands = new HashMap<>();
        gson = GsonInstance.get();
    }

    @Override
    public void notify(String input) {
        JSONObject emptyJsonObject = gson.fromJson(input, EmptyJsonObject.class);
        JSONMethods jsonMethod = emptyJsonObject.getMethod();
        JsonCommand<?> jsonCommand = commands.get(jsonMethod);
        if (jsonCommand == null) {
            logger.warn("Received a command that doesn't exist: {}", jsonMethod.toString());
            return;
        }

        jsonCommand.execute(input);
    }

    public void registerCommand(JSONMethods jsonMethods, JsonCommand<?> jsonCommand) {
        commands.put(jsonMethods, jsonCommand);
    }
}
