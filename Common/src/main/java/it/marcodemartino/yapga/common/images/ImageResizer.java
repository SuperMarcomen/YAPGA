package it.marcodemartino.yapga.common.images;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

public interface ImageResizer {

    BufferedImage scaleImage(Path path);
    byte[] getBytes(BufferedImage bufferedImage);
}
