package it.marcodemartino.yapga.server.commands;

import it.marcodemartino.yapga.common.commands.JsonCommand;
import it.marcodemartino.yapga.common.json.RequestImagesObject;
import it.marcodemartino.yapga.common.services.ImageService;

public class SendImagesCommand extends JsonCommand<RequestImagesObject> {

    private final ImageService imageService;

    public SendImagesCommand(ImageService imageService) {
        super(RequestImagesObject.class);
        this.imageService = imageService;
    }

    @Override
    protected void execute(RequestImagesObject requestImagesObject) {
        imageService.sendImages(requestImagesObject.getQuantity(), requestImagesObject.getIdentityCertificate().getUser().getUuid());
    }
}
