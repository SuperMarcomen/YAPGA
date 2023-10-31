package it.marcodemartino.yapga.client.ui.scenes;

import it.marcodemartino.yapga.client.logic.actions.Action;
import it.marcodemartino.yapga.client.logic.actions.SendImageAction;
import it.marcodemartino.yapga.client.logic.services.ImageService;
import it.marcodemartino.yapga.common.io.emitters.OutputEmitter;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GalleryScene extends StackPane {

    private final OutputEmitter outputEmitter;
    private final ImageService imageService;

    public GalleryScene(OutputEmitter outputEmitter, ImageService imageService) {
        this.outputEmitter = outputEmitter;
        this.imageService = imageService;
        Label label = new Label("Drag a file to me.");
        Label dropped = new Label("");
        VBox dragTarget = new VBox();
        dragTarget.getChildren().addAll(label,dropped);
        getChildren().add(dragTarget);

        dragTarget.setOnDragOver(event -> {
            if (event.getGestureSource() != dragTarget && event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        dragTarget.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                dropped.setText(db.getFiles().toString());
                success = true;
            }
            /* let the source know whether the string was successfully
             * transferred and used */
            event.setDropCompleted(success);

            event.consume();

            Action action = new SendImageAction(outputEmitter, imageService, db.getFiles().get(0).toPath());
            action.execute();
        });
    }
}
