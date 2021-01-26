package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.layout.AnchorPane;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageEditor {
    private final DraggableNode endNode;
    AnchorPane rootLayout;
    String imagePath;

    ImageEditor(AnchorPane layout, String path) throws IOException {
        rootLayout = layout;
        imagePath = path;
        DraggableNode startNode = new DraggableNode(imagePath);
        endNode = new DraggableNode(imagePath);

        startNode.setType(DragType.STARTNODE);
        endNode.setType(DragType.ENDNODE);

        Graph.nodes.add(startNode);
        Graph.nodes.add(endNode);
        Graph.startNode = startNode.getId();
        Graph.endNode = endNode.getId();

        rootLayout.getChildren().add(startNode);
        rootLayout.getChildren().add(endNode);

        startNode.setLayoutX(20);
        startNode.setLayoutY(100);
        endNode.setLayoutX(450);
        endNode.setLayoutY(100);
    }

    void addNode(DragType type) {
        try {
            var newNode = new DraggableNode(imagePath);
            newNode.setType(type);
            Graph.nodes.add(newNode);
            rootLayout.getChildren().add(newNode);
            newNode.setLayoutX(rootLayout.getWidth() / 2);
            newNode.setLayoutY(rootLayout.getHeight() / 2);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void saveToFile() throws IOException {
        File output = new File("Photo" + ".png");
        ImageIO.write(SwingFXUtils.fromFXImage(endNode.content.snapshot(null, null), null), "png", output);
    }
}
