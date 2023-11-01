package it.marcodemartino.yapga.client.logic.commands;

import it.marcodemartino.yapga.client.logic.services.GalleryService;
import it.marcodemartino.yapga.common.commands.JsonCommand;
import it.marcodemartino.yapga.common.json.SendImageObject;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReceiveImageCommand extends JsonCommand<SendImageObject> {

    private final Logger logger = LogManager.getLogger(ReceiveImageCommand.class);
    private final InputStream inputStream;
    private final GalleryService galleryService;

    public ReceiveImageCommand(InputStream inputStream, GalleryService galleryService) {
        super(SendImageObject.class);
        this.inputStream = inputStream;
        this.galleryService = galleryService;
    }

    @Override
    protected void execute(SendImageObject sendImageObject) {
        logger.info("Received the image {}", sendImageObject.getFileName());
        byte[] pictureBytes = readPictureBytes((int) sendImageObject.getFileSize());
        Platform.runLater(() -> galleryService.addImageFromBytes(pictureBytes));

        try {
            String senderUUID = sendImageObject.getSenderUUID().toString();
            Files.createDirectories(Paths.get(senderUUID));
            Files.write(Paths.get(senderUUID, sendImageObject.getFileName()), pictureBytes);
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