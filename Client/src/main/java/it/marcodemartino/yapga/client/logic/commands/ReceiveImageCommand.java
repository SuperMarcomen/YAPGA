package it.marcodemartino.yapga.client.logic.commands;

import it.marcodemartino.yapga.client.logic.services.GalleryService;
import it.marcodemartino.yapga.common.commands.JsonCommand;
import it.marcodemartino.yapga.common.json.SendImageObject;
import it.marcodemartino.yapga.common.services.EncryptionService;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;

public class ReceiveImageCommand extends JsonCommand<SendImageObject> {

    private final Logger logger = LogManager.getLogger(ReceiveImageCommand.class);
    private final InputStream inputStream;
    private final GalleryService galleryService;
    private final EncryptionService encryptionService;

    public ReceiveImageCommand(InputStream inputStream, GalleryService galleryService, EncryptionService encryptionService) {
        super(SendImageObject.class);
        this.inputStream = inputStream;
        this.galleryService = galleryService;
        this.encryptionService = encryptionService;
    }

    @Override
    protected void execute(SendImageObject sendImageObject) {
        logger.info("Received the image {}", sendImageObject.getFileName());
        byte[] pictureBytes = readPictureBytes((int) sendImageObject.getFileSize());

        Platform.runLater(() -> galleryService.addImageFromBytes(encryptionService.decryptBytes(pictureBytes)));
    }

    private byte[] readPictureBytes(int fileSize) {
        ByteBuffer buffer = ByteBuffer.allocate(4096); // Adjust buffer size as needed
        ByteArrayOutputStream pictureData = new ByteArrayOutputStream();

        int bytesRead;
        int totalBytesRead = 0;

        try {
            while (totalBytesRead < fileSize && (bytesRead = inputStream.read(buffer.array())) != -1) {
                pictureData.write(buffer.array(), 0, bytesRead);
                totalBytesRead += bytesRead;
                buffer.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pictureData.toByteArray();
    }
}