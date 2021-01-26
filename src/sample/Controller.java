package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.IOException;


public class Controller {
    public static ImageEditor imageEditor;
    @FXML
    private AnchorPane AnchorPaneLayout;
    @FXML
    private Button addBlackAndWhiteNode;
    @FXML
    private Button addBlurMode;
    @FXML
    private Button addReflectionNode;
    @FXML
    private MenuItem selectButton;
    @FXML
    private MenuItem saveButton;

    @FXML
    void initialize() {

        selectButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            var file = fileChooser.showOpenDialog(selectButton.getParentPopup().getScene().getWindow());
            if (file != null) {
                try {
                    imageEditor = new ImageEditor(AnchorPaneLayout, file.getPath());
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

            }
        });

        saveButton.setOnAction(actionEvent -> {
            if (imageEditor != null) {
                try {
                    imageEditor.saveToFile();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });

        addBlackAndWhiteNode.setOnAction(actionEvent -> {
            if (imageEditor != null)
                imageEditor.addNode(DragType.BLACKANDWHITE);
        });

        addBlurMode.setOnAction(actionEvent -> {
            if (imageEditor != null)
                imageEditor.addNode(DragType.MOTIONBLUR);
        });

        addReflectionNode.setOnAction(actionEvent -> {
            if (imageEditor != null)
                imageEditor.addNode(DragType.Reflection);
        });

    }
}

