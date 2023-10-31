package it.marcodemartino.yapga.client.logic.actions;

import it.marcodemartino.yapga.client.logic.services.ImageService;
import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;
import it.marcodemartino.yapga.common.json.JSONObject;
import it.marcodemartino.yapga.common.json.SendImageObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SendImageAction implements Action {

    private final Logger logger = LogManager.getLogger(SendImageAction.class);
    private final OutputEmitter outputEmitter;
    private final ImageService imageService;
    private final Path path;

    public SendImageAction(OutputEmitter outputEmitter, ImageService imageService, Path path) {
        this.outputEmitter = outputEmitter;
        this.imageService = imageService;
        this.path = path;
    }

    @Override
    public void execute() {
        try {
            FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
            long fileSize = fileChannel.size();
            JSONObject object = new SendImageObject(path.getFileName().toString(), fileSize);
            outputEmitter.sendOutput(object);

            // Send the binary picture data using NIO and a buffer
            int bufferSize = 4096; // Adjust the buffer size as needed
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            int bytesRead;
            long totalBytesSent = 0;

            while ((bytesRead = fileChannel.read(buffer)) != -1) {
                buffer.flip(); // Prepare the buffer for writing
                while (buffer.hasRemaining()) {
                    outputEmitter.sendRaw(buffer.array(), buffer.position(), buffer.remaining());
                    totalBytesSent += buffer.remaining();
                    buffer.position(buffer.limit()); // Mark the buffer as fully read
                    // Calculate and display progress if needed
                    double progress = (double) totalBytesSent / fileSize;
                    logger.info("Sending picture... Progress: {}", progress);
                    // Update your UI with the progress value
                }
                buffer.clear(); // Prepare the buffer for reading
            }

            // Close the file channel and the socket when done
            fileChannel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
