package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.CubicCurve;

import java.io.IOException;
import java.util.UUID;

class NodeLink extends AnchorPane {
    @FXML
    CubicCurve nodeLink;

    SimpleDoubleProperty offsetX = new SimpleDoubleProperty();
    SimpleDoubleProperty offsetY = new SimpleDoubleProperty();
    SimpleDoubleProperty offsetDirX1 = new SimpleDoubleProperty();
    SimpleDoubleProperty offsetDirX2 = new SimpleDoubleProperty();
    SimpleDoubleProperty offsetDirY1 = new SimpleDoubleProperty();
    SimpleDoubleProperty offsetDirY2 = new SimpleDoubleProperty();

    public NodeLink() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../res/NodeLink.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        setId(UUID.randomUUID().toString());
    }

    @FXML
    void initialize() {
        offsetX.set(50.0);
        offsetY.set(50.0);

        offsetDirX1.bind(new When(nodeLink.startXProperty().greaterThan(nodeLink.endXProperty()))
                .then(-1.0).otherwise(1.0));

        offsetDirX2.bind(new When(nodeLink.startXProperty().greaterThan(nodeLink.endXProperty()))
                .then(1.0).otherwise(-1.0));

        nodeLink.controlX1Property().bind(Bindings.add(nodeLink.startXProperty(), offsetX.multiply(offsetDirX1)));
        nodeLink.controlX2Property().bind(Bindings.add(nodeLink.endXProperty(), offsetX.multiply(offsetDirX2)));
        nodeLink.controlY1Property().bind(Bindings.add(nodeLink.startYProperty(), offsetY.multiply(offsetDirY1)));
        nodeLink.controlY2Property().bind(Bindings.add(nodeLink.endYProperty(), offsetY.multiply(offsetDirY2)));

        nodeLink.setOnMouseMoved(mouseEvent -> nodeLink.setStyle("-fx-stroke: #ff0000"));
        nodeLink.setOnMouseExited(mouseEvent -> nodeLink.setStyle("-fx-stroke: BLUE"));

    }

    void setStart(Point2D point) {
        nodeLink.setStartX(point.getX());
        nodeLink.setStartY(point.getY());
    }

    void setEnd(Point2D point) {
        nodeLink.setEndX(point.getX());
        nodeLink.setEndY(point.getY());
    }

    void bindStartEnd(DraggableNode firstSource, DraggableNode secondSource) {
        nodeLink.startXProperty().bind(Bindings.add(firstSource.layoutXProperty(), firstSource.getWidth() / 2.0));
        nodeLink.startYProperty().bind(Bindings.add(firstSource.layoutYProperty(), firstSource.getWidth() / 2.0));
        nodeLink.endXProperty().bind(Bindings.add(secondSource.layoutXProperty(), secondSource.getWidth() / 2.0));
        nodeLink.endYProperty().bind(Bindings.add(secondSource.layoutYProperty(), secondSource.getWidth() / 2.0));
    }

}