package sample;

import javafx.scene.input.DataFormat;
import javafx.util.Pair;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DragContainer implements Serializable {

    public static final DataFormat AddNode = new DataFormat("application.DraggableNode.drag");
    public static final DataFormat AddLink = new DataFormat("application.NodeLink.add");
    @Serial
    private static final long serialVersionUID = -1890998765646621338L;
    private final List<Pair<String, Object>> mDataPairs = new ArrayList<>();

    public void addData(String key, Object value) {
        mDataPairs.add(new Pair<>(key, value));
    }

    public <T> T getValue(String key) {

        for (Pair<String, Object> data : mDataPairs) {

            if (data.getKey().equals(key))
                return (T) data.getValue();

        }

        return null;
    }

    public List<Pair<String, Object>> getData() {
        return mDataPairs;
    }
}
