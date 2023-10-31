package it.marcodemartino.yapga.common.commands;

import com.google.gson.Gson;
import it.marcodemartino.yapga.common.io.listeners.InputListener;
import it.marcodemartino.yapga.common.json.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JsonCommandManager implements InputListener {

    private final Logger logger = LogManager.getLogger(JsonCommandManager.class);
    private final InputStream inputStream;
    private final Map<JSONMethods, JsonCommand<?>> commands;
    private final Gson gson;

    public JsonCommandManager(InputStream inputStream) {
        this.inputStream = inputStream;
        commands = new HashMap<>();
        gson = GsonInstance.get();
    }

    @Override
    public void notify(String input) {
        JSONObject emptyJsonObject = gson.fromJson(input, EmptyJsonObject.class);
        JSONMethods jsonMethod = emptyJsonObject.getMethod();
        JsonCommand<?> jsonCommand = commands.get(jsonMethod);
        if (jsonMethod.equals(JSONMethods.SEND_IMAGE)) {
            SendImageObject object = gson.fromJson(input, SendImageObject.class);
            ByteBuffer buffer = ByteBuffer.allocate(4096); // Adjust buffer size as needed
            int dataLength = (int) object.getFileSize();
            ByteArrayOutputStream pictureData = new ByteArrayOutputStream(dataLength);

            int bytesRead;
            int totalBytesRead = 0;

            while (totalBytesRead < dataLength) {
                try {
                    bytesRead = inputStream.read(buffer.array());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                pictureData.write(buffer.array(), 0, bytesRead);
                totalBytesRead += bytesRead;
                buffer.clear();
            }

            // Handle the picture data
            byte[] pictureBytes = pictureData.toByteArray();
            try {
                Files.write(Paths.get(object.getFileName()), pictureBytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
