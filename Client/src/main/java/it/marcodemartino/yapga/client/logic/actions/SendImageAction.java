package it.marcodemartino.yapga.client.logic.actions;

import it.marcodemartino.yapga.client.logic.services.CertificatesService;
import it.marcodemartino.yapga.common.services.ImageService;
import javafx.scene.control.ProgressBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.function.Consumer;

public class SendImageAction implements Action {

    private final Logger logger = LogManager.getLogger(SendImageAction.class);
    private final ImageService imageService;
    private final CertificatesService certificatesService;
    private final Path path;
    private final Consumer<Double> consumer;
    private final ProgressBar progressBar;

    public SendImageAction(ImageService imageService, CertificatesService certificatesService, Path path, Consumer<Double> consumer, ProgressBar progressBar) {
        this.imageService = imageService;
        this.certificatesService = certificatesService;
        this.path = path;
        this.consumer = consumer;
        this.progressBar = progressBar;
    }

    @Override
    public void execute() {
        imageService.sendImage(path, certificatesService.getIdentityCertificate().getUser().getUuid(), consumer);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
