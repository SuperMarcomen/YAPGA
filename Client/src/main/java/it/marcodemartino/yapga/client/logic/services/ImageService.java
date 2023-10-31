package it.marcodemartino.yapga.client.logic.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImageService {

    private final Logger logger = LogManager.getLogger(ImageService.class);

    public byte[] loadImage(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            logger.warn("There was an error while reading the image {}", path, e);
            return new byte[0];
        }
    }
}
