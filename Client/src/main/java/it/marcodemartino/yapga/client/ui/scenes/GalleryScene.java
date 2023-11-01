package it.marcodemartino.yapga.client.ui.scenes;

import it.marcodemartino.yapga.client.logic.actions.Action;
import it.marcodemartino.yapga.client.logic.actions.SendImageAction;
import it.marcodemartino.yapga.client.logic.services.CertificatesService;
import it.marcodemartino.yapga.client.logic.services.GalleryService;
import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;
import it.marcodemartino.yapga.common.services.ImageService;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

import java.io.File;

public class GalleryScene extends StackPane {

    private final OutputEmitter outputEmitter;
    private final ImageService imageService;
    private final GalleryService galleryService;

    public GalleryScene(OutputEmitter outputEmitter, CertificatesService certificatesService, ImageService imageService, GalleryService galleryService) {
        this.outputEmitter = outputEmitter;
        this.imageService = imageService;
        this.galleryService = galleryService;
        Label label = new Label("Drag a file to me.");
        Label dropped = new Label("");
        VBox dragTarget = new VBox();
        GridPane grid = new GridPane();
        grid.setHgap(10); // Horizontal gap between images
        grid.setVgap(10); // Vertical gap between images
        dragTarget.getChildren().addAll(label, dropped, grid);

        // Define the number of rows and columns in the grid
        int numRows = 4; // You can change this as needed
        int numCols = 4; // You can change this as needed


        getChildren().addAll(dragTarget);

        galleryService.registerListeners(image -> {
            System.out.println("called");
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

        dragTarget.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                dropped.setText(db.getFiles().toString());

                // Get the image file
                for (File file : db.getFiles()) {

                    // Create a progress bar
                    ProgressBar progressBar = new ProgressBar(0);
                    progressBar.setPrefWidth(150); // Set the width to match the image size
                    progressBar.setMaxWidth(150);

                    // Create an ImageView for the image
                    Image image = new Image(file.toURI().toString());
                    ImageView imageView = createImageView(image);

                    // Create a VBox to hold the image and progress bar
                    StackPane imageBox = new StackPane();
                    imageBox.setAlignment(Pos.BOTTOM_CENTER);
                    imageBox.getChildren().addAll(imageView, progressBar);

                    // Add the image and progress bar to the grid
                    insertImageInGrid(grid, imageBox, numRows, numCols);

                    // Perform the upload action in the background
                    Action action = new SendImageAction(outputEmitter, imageService, certificatesService, file.toPath(), progressBar::setProgress);
                    action.execute();
                }
        }});
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
