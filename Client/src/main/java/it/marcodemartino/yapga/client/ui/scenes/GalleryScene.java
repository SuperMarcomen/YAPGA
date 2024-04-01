package it.marcodemartino.yapga.client.ui.scenes;

import it.marcodemartino.yapga.client.logic.actions.Action;
import it.marcodemartino.yapga.client.logic.actions.SendImageAction;
import it.marcodemartino.yapga.client.logic.services.CertificatesService;
import it.marcodemartino.yapga.client.logic.services.GalleryService;
import it.marcodemartino.yapga.client.ui.scenes.elements.SideMenu;
import it.marcodemartino.yapga.common.services.ImageService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GalleryScene extends StackPane {


    public GalleryScene(ReadOnlyDoubleProperty widthProp, ReadOnlyDoubleProperty heightProp, ScenesSwitcher scenesSwitcher, CertificatesService certificatesService, ImageService imageService, GalleryService galleryService) {
        prefWidthProperty().bind(widthProp);
        prefHeightProperty().bind(heightProp);
        GridPane grid = new GridPane();

        grid.setHgap(10); // Horizontal gap between images
        grid.setVgap(10); // Vertical gap between images

        // Define the number of rows and columns in the grid
        int numRows = 9; // You can change this as needed
        int numCols = 9; // You can change this as needed

        VBox sideMenu = new SideMenu(true, false, scenesSwitcher, prefWidthProperty());

        HBox container = new HBox(sideMenu, grid);
        container.setSpacing(20);

        getChildren().addAll(container);

        galleryService.registerListeners(image -> {
            ImageView imageView = createImageView(image);
            insertImageInGrid(grid, imageView, numRows, numCols);
        });

        setOnDragOver(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });


        // Define a blocking queue to store image send requests.

        BlockingQueue<File> imageQueue = new LinkedBlockingQueue<>();

        setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            event.consume();
            if (db.hasFiles()) {
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

                    Action action = new SendImageAction(imageService, certificatesService, file.toPath(), progress -> {
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
