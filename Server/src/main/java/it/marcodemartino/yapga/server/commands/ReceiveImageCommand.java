package it.marcodemartino.yapga.server.commands;

import it.marcodemartino.yapga.common.commands.JsonCommand;
import it.marcodemartino.yapga.common.json.SendImageObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReceiveImageCommand extends JsonCommand<SendImageObject> {

    private final Logger logger = LogManager.getLogger(ReceiveImageCommand.class);
    private final InputStream inputStream;

    public ReceiveImageCommand(InputStream inputStream) {
        super(SendImageObject.class);
        this.inputStream = inputStream;
    }

    @Override
    protected void execute(SendImageObject sendImageObject) {
        logger.info("Received the image {}", sendImageObject.getFileName());
        ByteBuffer buffer = ByteBuffer.allocate(4096); // Adjust buffer size as needed
        int dataLength = (int) sendImageObject.getFileSize();
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
            Files.write(Paths.get(sendImageObject.getFileName()), pictureBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
