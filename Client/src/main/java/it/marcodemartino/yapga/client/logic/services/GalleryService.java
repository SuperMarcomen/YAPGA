package it.marcodemartino.yapga.client.logic.services;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GalleryService {

    private final List<Image> images;
    private final List<Consumer<Image>> listeners;

    public GalleryService() {
        this.images = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    public void addImageFromBytes(byte[] imageBytes) {
        Image image = new Image(new ByteArrayInputStream(imageBytes));
        notify(image);
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
