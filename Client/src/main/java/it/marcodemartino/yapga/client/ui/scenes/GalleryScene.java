package it.marcodemartino.yapga.client.ui.scenes;

import it.marcodemartino.yapga.client.logic.actions.Action;
import it.marcodemartino.yapga.client.logic.actions.SendImageAction;
import it.marcodemartino.yapga.client.logic.services.CertificatesService;
import it.marcodemartino.yapga.client.logic.services.GalleryService;
import it.marcodemartino.yapga.client.ui.scenes.elements.SideMenu;
import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;
import it.marcodemartino.yapga.common.services.ImageService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GalleryScene extends StackPane {


    public GalleryScene(ReadOnlyDoubleProperty widthProp, ReadOnlyDoubleProperty heightProp, ScenesSwitcher scenesSwitcher, OutputEmitter outputEmitter, CertificatesService certificatesService, ImageService imageService, GalleryService galleryService) {
        prefWidthProperty().bind(widthProp);
        prefHeightProperty().bind(heightProp);
        Label label = new Label("Drag a file to me.");
        Label dropped = new Label("");
        VBox dragTarget = new VBox();
        GridPane grid = new GridPane();
        grid.setHgap(10); // Horizontal gap between images
        grid.setVgap(10); // Vertical gap between images
        dragTarget.getChildren().addAll(label, dropped, grid);

        // Define the number of rows and columns in the grid
        int numRows = 9; // You can change this as needed
        int numCols = 9; // You can change this as needed

        VBox sideMenu = new SideMenu(true, false, scenesSwitcher, prefWidthProperty());

        HBox container = new HBox(sideMenu, dragTarget);
        container.setSpacing(20);

        getChildren().addAll(container);

        galleryService.registerListeners(image -> {
            ImageView imageView = createImageView(image);
            insertImageInGrid(grid, imageView, numRows, numCols);
        });

        dragTarget.setOnDragOver(event -> {
            if (event.getGestureSource() != dragTarget && event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });


// Define a blocking queue to store image send requests.

        BlockingQueue<File> imageQueue = new LinkedBlockingQueue<>();

        dragTarget.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            event.consume();
            if (db.hasFiles()) {
                dropped.setText(db.getFiles().toString());
                imageQueue.addAll(db.getFiles());
            }
        });


// Create a separate thread to process the image send requests.
        Thread sendImageThread = new Thread(() -> {
            while (true) {
                List<Action> actions = new ArrayList<>();
                try {
                    File file = imageQueue.take();
                    System.out.println("Started processing " + file.getName());

                    // Create a progress bar
                    ProgressBar progressBar = new ProgressBar(0);
                    progressBar.setPrefWidth(150); // Set the width to match the image size
                    progressBar.setMaxWidth(150);

                    // Create an ImageView for the image
                    Image image = new Image("file:/" + file.getAbsolutePath(), 150, 150, true, true);
                    ImageView imageView = createImageView(image);

                    // Create a VBox to hold the image and progress bar
                    StackPane imageBox = new StackPane();
                    imageBox.setAlignment(Pos.BOTTOM_CENTER);
                    imageBox.getChildren().addAll(imageView, progressBar);

                    // Add the image and progress bar to the grid
                    Platform.runLater(() -> insertImageInGrid(grid, imageBox, numRows, numCols));

                    Action action = new SendImageAction(outputEmitter, imageService, certificatesService, file.toPath(), progress -> {
                        Platform.runLater(() -> {
                            progressBar.setProgress(progress);
                            if (progress == 1.0) {
                                progressBar.setVisible(false);
                            }
                        });
                    }, progressBar);
                    actions.add(action);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (Action action : actions) {
                    action.execute();
                }
            }
        });

        sendImageThread.start(); // Start the thread
    }

    private Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }

        return new ImageView(wr).getImage();
    }


    private ImageView createImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(150); // Set the desired width
        imageView.setFitHeight(150); // Set the desired height
        imageView.setPreserveRatio(true); // Maintain aspect ratio
        return imageView;
    }

    private void insertImageInGrid(GridPane grid, Node node, int numRows, int numCols) {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                if (!isGridCellOccupied(grid, row, col)) {
                    grid.add(node, col, row);
                    return;
                }
            }
        }
    }

    private boolean isGridCellOccupied(GridPane grid, int row, int col) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return true;
            }
        }
        return false;
    }
}
