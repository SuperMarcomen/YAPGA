package it.marcodemartino.yapga.client.logic.services;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class GalleryService {

    private final List<Image> images;
    private final List<Consumer<Image>> listeners;
    private final Map<Path, Consumer<Double>> progressTracker;

    public GalleryService() {
        this.images = new ArrayList<>();
        this.listeners = new ArrayList<>();
        progressTracker = new HashMap<>();
    }

    public void addImageFromBytes(byte[] imageBytes) {
        Image image = new Image(new ByteArrayInputStream(imageBytes));
        notify(image);
    }

    public void track(Path path, Consumer<Double> consumer) {
        progressTracker.put(path, consumer);
    }

    public void updateTracking(Path path, Double value) {
        progressTracker.get(path).accept(value);
    }

    public void registerListeners(Consumer<Image> listener) {
        listeners.add(listener);
    }

    public void notify(Image image) {
        for (Consumer<Image> listener : listeners) {
            listener.accept(image);
        }
    }

    public List<Image> getImages() {
        return images;
    }
}
