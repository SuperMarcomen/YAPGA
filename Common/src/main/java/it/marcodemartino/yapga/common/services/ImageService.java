package it.marcodemartino.yapga.common.services;

import it.marcodemartino.yapga.common.images.ImageResizer;
import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;
import it.marcodemartino.yapga.common.json.JSONObject;
import it.marcodemartino.yapga.common.json.SendImageObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageService implements ImageResizer, Runnable {

    private final Logger logger = LogManager.getLogger(ImageService.class);
    private final ImageResizer imageResizer;
    private final OutputEmitter outputEmitter;
    private final EncryptionService encryptionService;

    public ImageService(ImageResizer imageResizer, OutputEmitter outputEmitter, EncryptionService encryptionService) {
        this.imageResizer = imageResizer;
        this.outputEmitter = outputEmitter;
        this.encryptionService = encryptionService;
    }

    public void sendImages(int quantity, UUID senderUUID) {
        List<Path> pictures = getImages(quantity, senderUUID);
        if (pictures == null || pictures.isEmpty()) return;
        for (Path picture : pictures) {
            try {
                trySendImage(picture, senderUUID, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<Path> getImages(int quantity, UUID senderUUID) {
        try (Stream<Path> stream = Files.list(Paths.get(senderUUID.toString()))) {
            return stream
                    .limit(quantity)
                    .collect(Collectors.toList());
        } catch (IOException e) {

        }
        return null;
    }

    public void sendImage(Path path, UUID senderUUID, Consumer<Double> consumer) {
        try {
            trySendImage(path, senderUUID, consumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendImage(Path path, UUID senderUUID) {
        try {
            trySendImage(path, senderUUID, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void trySendImage(Path path, UUID senderUUID, Consumer<Double> consumer) throws IOException {
        FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
        long fileSize = fileChannel.size();

        JSONObject object = new SendImageObject(path.getFileName().toString(), fileSize, senderUUID);
        outputEmitter.sendOutput(object);

        // Send the binary picture data using NIO and a buffer
        int bufferSize = 4096; // Adjust the buffer size as needed
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        long totalBytesSent = 0;

        logger.info("Sending picture...");
        while (fileChannel.read(buffer) != -1) {
            buffer.flip(); // Prepare the buffer for writing
            while (buffer.hasRemaining()) {
                outputEmitter.sendRaw(buffer.array(), buffer.position(), buffer.remaining());
                totalBytesSent += buffer.remaining();
                buffer.position(buffer.limit()); // Mark the buffer as fully read
                // Calculate and display progress if needed
                // Update your UI with the progress value
                if (consumer != null) {
                    double progress = (double) totalBytesSent / fileSize;
                    consumer.accept(progress);
                }
            }
            buffer.clear(); // Prepare the buffer for reading
        }

        // Close the file channel and the socket when done
        fileChannel.close();
    }


    @Override
    public BufferedImage scaleImage(Path path) {
        return imageResizer.scaleImage(path);
    }

    @Override
    public byte[] getBytes(BufferedImage bufferedImage) {
        return imageResizer.getBytes(bufferedImage);
    }

    @Override
    public void run() {

    }
}
