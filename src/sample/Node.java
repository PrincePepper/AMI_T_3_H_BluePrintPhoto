package sample;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.UUID;

interface Effective {
    void effect();
}

class DraggableNode extends AnchorPane {
    static String imagePath = null;
    private final ArrayList<String> linkIds = new ArrayList<>();
    public Effective effect;
    public boolean checkEffect = false;
    public ArrayList<Pair<String, Effect>> prevEffects = new ArrayList<>();
    @FXML
    AnchorPane rootPane;
    @FXML
    AnchorPane leftLinkPane;
    @FXML
    AnchorPane rightLinkPane;
    @FXML
    ImageView content;

    EventHandler<DragEvent> contextDragOver;
    EventHandler<DragEvent> contextDragDropped;
    EventHandler<MouseEvent> linkDragDetected;
    EventHandler<DragEvent> linkDragDropped;
    EventHandler<DragEvent> contextLinkDragOver;
    EventHandler<DragEvent> contextLinkDagDropped;

    NodeLink link = new NodeLink();
    boolean checkLine = false;
    Point2D offset = new Point2D(0.0, 0.0);
    AnchorPane superParent = new AnchorPane();
    DragType dragType = null;
    @FXML
    private Button deleteButton;
    @FXML
    private Label effectName;


    DraggableNode(String path) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../res/Node.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
        imagePath = path;
        if (imagePath != null)
            content.setImage(new Image(new FileInputStream(imagePath)));
        setId(UUID.randomUUID().toString());

    }

    @FXML
    void initialize() {
        nodeHandlers();
        linkHandlers();

        deleteButton.setOnAction(actionEvent -> {
            if (this.getId().equals(Graph.startNode)) {
                Graph.deleteCheckLine(Graph.startNode);
                Graph.deleteNode(Graph.startNode);
                Graph.deleteCheckLine(Graph.endNode);
                Graph.deleteNode(Graph.endNode);
                AnchorPane parent = (AnchorPane) this.getParent();
                ObservableList<Node> Node = parent.getChildren();
                parent.getChildren().removeAll(Node);
                linkIds.clear();
                Controller.imageEditor = null;
                Graph.startNode = "";
                Graph.endNode = "";
                Graph.nodes.clear();
                System.out.println(parent.getChildren());
                System.out.println(linkIds);
            } else {
                Graph.deleteCheckLine(this.getId());
                Graph.deleteNode(this.getId());
                for (var node : Graph.nodes) {
                    node.prevEffects.clear();
                    node.content.setEffect(null);
                    node.checkEffect = false;
                }
                Graph.detoir();

                AnchorPane parent = (AnchorPane) this.getParent();
                parent.getChildren().removeAll(this);
                System.out.println(parent.getChildren());
                System.out.println(linkIds);
                for (ListIterator<String> iterId = linkIds.listIterator(); iterId.hasNext(); ) {
                    String id = iterId.next();
                    for (ListIterator<Node> iterNode = parent.getChildren().listIterator(); iterNode.hasNext(); ) {
                        Node node = iterNode.next();
                        if (node.getId() == null)
                            continue;
                        if (node.getId().equals(id))
                            iterNode.remove();
                    }
                    iterId.remove();
                }
            }
        });

        leftLinkPane.setOnMouseMoved(mouseEvent -> {
            leftLinkPane.setStyle("-fx-background-color: #f88686");
        });

        leftLinkPane.setOnMouseExited(mouseEvent -> {
            leftLinkPane.setStyle("-fx-background-color:  #dcdcdc");
        });

        rightLinkPane.setOnMouseMoved(mouseEvent -> {
            rightLinkPane.setStyle("-fx-background-color: #f88686");
        });

        rightLinkPane.setOnMouseExited(mouseEvent -> {
            rightLinkPane.setStyle("-fx-background-color:  #dcdcdc");
        });

        leftLinkPane.setOnDragDetected(linkDragDetected);
        leftLinkPane.setOnDragDropped(linkDragDropped);
        rightLinkPane.setOnDragDetected(linkDragDetected);
        rightLinkPane.setOnDragDropped(linkDragDropped);

    }

    void setType(DragType type) {
        dragType = type;

        switch (dragType) {
            case MOTIONBLUR:
                effect = () -> {
                    MotionBlur motionBlur = new MotionBlur();
                    motionBlur.setRadius(10.5);
                    motionBlur.setAngle(45);
                    Graph.addPrevEffects(getId());
                    for (var effect : prevEffects) {
                        motionBlur.setInput(effect.getValue());
                    }
                    content.setEffect(motionBlur);
                    prevEffects.add(new Pair<>(getId(), motionBlur));
                };
                effectName.setText("Блюр");
                break;

            case BLACKANDWHITE:
                effect = () -> {
                    ColorAdjust colorAdjust = new ColorAdjust();
                    colorAdjust.setSaturation(-1);
                    Graph.addPrevEffects(getId());
                    for (var effect : prevEffects) {
                        colorAdjust.setInput(effect.getValue());
                    }
                    content.setEffect(colorAdjust);
                    prevEffects.add(new Pair<>(getId(), colorAdjust));
                };
                effectName.setText("Черно-белое");
                break;

            case Reflection:
                effect = () -> {
                    Reflection reflection = new Reflection();
                    Graph.addPrevEffects(getId());
                    for (var effect : prevEffects) {
                        reflection.setInput(effect.getValue());
                    }
                    content.setEffect(reflection);
                    prevEffects.add(new Pair<>(getId(), reflection));
                };
                effectName.setText("Отражение");
                break;

            case ENDNODE:
                effectName.setText("Результат");
                rightLinkPane.setVisible(false);
                deleteButton.setVisible(false);
                effect = () -> {

                    // этот эффект не сработает, переменная нужна только в качестве контейнера
                    Bloom resEffect = new Bloom(1);
                    Graph.addPrevEffects(getId());
                    for (var effect : prevEffects) {
                        resEffect.setInput(effect.getValue());
                    }
                    content.setEffect(resEffect);
                };

                break;

            case STARTNODE:
                deleteButton.setVisible(true);
                leftLinkPane.setVisible(false);
                effectName.setText("Изображение");
                break;

        }
    }

    public void registerLink(String linkId) {
        linkIds.add(linkId);
    }

    void updatePoint(Point2D point) {
        var local = getParent().sceneToLocal(point);
        relocate((int) (local.getX() - offset.getX()),
                (int) (local.getY() - offset.getY())
        );
    }

    void nodeHandlers() {
        contextDragOver = dragEvent -> {
            dragEvent.acceptTransferModes(TransferMode.ANY);
            updatePoint(new Point2D(dragEvent.getSceneX(), dragEvent.getSceneY()));
            dragEvent.consume();
        };

        contextDragDropped = dragEvent -> {
            getParent().setOnDragDropped(null);
            getParent().setOnDragOver(null);
            dragEvent.setDropCompleted(true);
            dragEvent.consume();
        };

        rootPane.setOnDragDetected(event -> {
            getParent().setOnDragOver(contextDragOver);
            getParent().setOnDragDropped(contextDragDropped);

            offset = new Point2D(event.getX(), event.getY());
            updatePoint(new Point2D(event.getSceneX(), event.getSceneY()));

            ClipboardContent content = new ClipboardContent();
            DragContainer container = new DragContainer();

            container.addData("type", getId());
            content.put(DragContainer.AddNode, container);

            startDragAndDrop(TransferMode.ANY).setContent(content);
            event.consume();
        });
    }

    void linkHandlers() {
        parentProperty().addListener((observableValue, parent, t1) -> superParent = (AnchorPane) getParent());

        linkDragDetected = mouseEvent -> {
            if (dragType == DragType.ENDNODE)
                return;
            if (checkLine)
                return;

            getParent().setOnDragOver(contextLinkDragOver);
            getParent().setOnDragDropped(contextLinkDagDropped);

            superParent.getChildren().add(0, link);

            link.setVisible(true);

            var p = new Point2D(getLayoutX() + getWidth() / 2, getLayoutY() + getHeight() / 2);
            link.setStart(p);

            ClipboardContent content = new ClipboardContent();
            DragContainer container = new DragContainer();

            container.addData("source", getId());
            content.put(DragContainer.AddLink, container);
            startDragAndDrop(TransferMode.ANY).setContent(content);
            mouseEvent.consume();
        };

        linkDragDropped = event -> {
            getParent().setOnDragOver(null);
            getParent().setOnDragDropped(null);

            DragContainer container = (DragContainer) event.getDragboard().getContent(DragContainer.AddLink);
            if (container == null)
                return;

            link.setVisible(false);
            superParent.getChildren().remove(0);

            ClipboardContent content = new ClipboardContent();
            container.addData("target", getId());
            content.put(DragContainer.AddLink, container);
            event.getDragboard().setContent(content);

            String sourceId = container.getValue("source");
            String targetId = container.getValue("target");
            if (sourceId != null && targetId != null) {
                Graph.data.add(new Pair<>(sourceId, targetId));

                try {
                    var link = new NodeLink();
                    superParent.getChildren().add(0, link);

                    DraggableNode source = null;
                    DraggableNode target = null;

                    for (Node n : superParent.getChildren()) {

                        if (n.getId() == null)
                            continue;

                        if (n.getId().equals(sourceId))
                            source = (DraggableNode) n;

                        if (n.getId().equals(targetId))
                            target = (DraggableNode) n;

                    }

                    if (source != null && target != null && target.getId() != Graph.startNode) {
                        link.bindStartEnd(source, target);
                        source.checkLine = true;
                        source.registerLink(link.getId());
                        registerLink(link.getId());
                        if (effect != null) {
                            Graph.detoir();
                        }

                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

            event.setDropCompleted(true);
            event.consume();
        };

        contextLinkDragOver = event -> {
            event.acceptTransferModes(TransferMode.ANY);
            if (!link.isVisible())
                link.setVisible(true);
            link.setEnd(new Point2D(event.getX(), event.getY()));

            event.consume();
        };

        contextLinkDagDropped = event -> {
            getParent().setOnDragDropped(null);
            getParent().setOnDragOver(null);

            link.setVisible(false);
            superParent.getChildren().remove(0);

            event.setDropCompleted(true);
            event.consume();
        };
    }

}
