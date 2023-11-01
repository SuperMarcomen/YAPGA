package it.marcodemartino.yapga.client.logic.images;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class STDImageResizer implements ImageResizer {

    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 600;

    @Override
    public byte[] getBytes(BufferedImage bufferedImage) {
        return new byte[0];
    }

    @Override
    public BufferedImage scaleImage(Path sourcePath) {
        try {
            BufferedImage originalImage = ImageIO.read(sourcePath.toFile());
            int orientation = getOrientation(sourcePath);
            ImageSize newImageSize = calculateImageSize(originalImage);
            BufferedImage scaledImage = createScaledImage(originalImage, newImageSize);
            scaleImage(originalImage, newImageSize, scaledImage);
            scaledImage = setImageRotation(orientation, newImageSize, scaledImage);
            return scaledImage;
        } catch (IOException | ImageProcessingException | MetadataException e) {
            e.printStackTrace();
        }

        return null;
    }

    private BufferedImage createScaledImage(BufferedImage originalImage, ImageSize newImageSize) {
        return new BufferedImage(newImageSize.width(), newImageSize.height(), originalImage.getType());
    }

    private void scaleImage(BufferedImage originalImage, ImageSize newImageSize, BufferedImage scaledImage) {
        scaledImage.getGraphics().drawImage(originalImage, 0, 0, newImageSize.width(), newImageSize.height(), null);
    }

    private BufferedImage setImageRotation(int orientation, ImageSize newImageSize, BufferedImage scaledImage) {
        // Handle image rotation based on the orientation
        if (orientation == 6 || orientation == 8) {
            // Rotate the image 90 degrees clockwise
            AffineTransform transform = new AffineTransform();
            transform.translate(newImageSize.height() / 2.0, newImageSize.width() / 2.0);
            transform.rotate(Math.toRadians(orientation == 6 ? 90 : 270));
            transform.translate(-newImageSize.width() / 2.0, -newImageSize.height() / 2.0);
            BufferedImage rotatedImage = new BufferedImage(newImageSize.height(), newImageSize.width(), scaledImage.getType());
            rotatedImage.createGraphics().drawImage(scaledImage, transform, null);
            scaledImage = rotatedImage;
        }
        return scaledImage;
    }

    private int getOrientation(Path sourcePath) throws ImageProcessingException, IOException, MetadataException {
        Metadata metadata = ImageMetadataReader.readMetadata(sourcePath.toFile());
        ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        return exifIFD0Directory != null ? exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION) : 1;
    }

    private ImageSize calculateImageSize(BufferedImage originalImage) {
        int newWidth, newHeight;
        if (originalImage.getWidth() > MAX_WIDTH || originalImage.getHeight() > MAX_HEIGHT) {
            double widthRatio = (double) MAX_WIDTH / originalImage.getWidth();
            double heightRatio = (double) MAX_HEIGHT / originalImage.getHeight();
            double scaleRatio = Math.min(widthRatio, heightRatio);
            newWidth = (int) (originalImage.getWidth() * scaleRatio);
            newHeight = (int) (originalImage.getHeight() * scaleRatio);
        } else {
            newWidth = originalImage.getWidth();
            newHeight = originalImage.getHeight();
        }
        return new ImageSize(newWidth, newHeight);
    }

    private record ImageSize(int width, int height) {}
}
