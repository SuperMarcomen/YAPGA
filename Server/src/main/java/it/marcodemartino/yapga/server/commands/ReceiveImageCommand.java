package it.marcodemartino.yapga.server.commands;

import it.marcodemartino.yapga.common.commands.JsonCommand;
import it.marcodemartino.yapga.common.json.SendImageObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;

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
        byte[] pictureBytes = readPictureBytes((int) sendImageObject.getFileSize());

        try {
            String senderUUID = sendImageObject.getSenderUUID().toString();
            Files.createDirectories(Paths.get(senderUUID));
            Path path = Paths.get(senderUUID, sendImageObject.getFileName());
            Files.write(path, pictureBytes);

            FileTime time = FileTime.fromMillis(sendImageObject.getMillisCreationDate());
            Files.setAttribute(path, "creationTime", time);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readPictureBytes(int fileSize) {
        ByteBuffer buffer = ByteBuffer.allocate(4096); // Adjust buffer size as needed
        ByteArrayOutputStream pictureData = new ByteArrayOutputStream(fileSize);

        int bytesRead;
        int totalBytesRead = 0;

        while (totalBytesRead < fileSize) {
            try {
                bytesRead = inputStream.read(buffer.array());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            pictureData.write(buffer.array(), 0, bytesRead);
            totalBytesRead += bytesRead;
            buffer.clear();
        }

        return pictureData.toByteArray();
    }
}
