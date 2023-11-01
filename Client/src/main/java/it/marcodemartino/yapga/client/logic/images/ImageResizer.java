package it.marcodemartino.yapga.client.logic.images;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

public interface ImageResizer {

    BufferedImage scaleImage(Path path);
    byte[] getBytes(BufferedImage bufferedImage);
}
