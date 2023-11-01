package it.marcodemartino.yapga.client.logic.actions;

import it.marcodemartino.yapga.client.logic.services.ImageService;
import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

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
        imageService.sendImage(path);
    }
}
