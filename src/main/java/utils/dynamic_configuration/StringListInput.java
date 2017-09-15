package utils.dynamic_configuration;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringListInput extends VBox {
    private final Set<String> set;

    public StringListInput(List<Object> parameterValues, int index, String defaultValue) {
        // Create HashSet instance and add it to the parameters
        set = new HashSet<>();
        parameterValues.set(index, set);

        // Create the editable list
        ObservableList<String> stringList = FXCollections.observableArrayList();

        ListView<String> simpleList = new ListView<>(stringList);
        simpleList.setEditable(true);
        simpleList.setCellFactory(TextFieldListCell.forListView());

        simpleList.setOnEditCommit(t -> simpleList.getItems().set(t.getIndex(), t.getNewValue()));

        // Create the add/remove item buttons
        Button addBtn = new Button("Add");
        addBtn.onActionProperty().setValue(event -> stringList.add("(double click to edit)"));

        Button removeBtn = new Button("Remove last");
        removeBtn.onActionProperty().setValue(event -> {
            ObservableList<String> list = simpleList.getItems();

            // Only try to remove if list contains at least 1 item
            if (list.size() > 0) {
                list.remove(list.size() - 1);
            }
        });

        HBox addRemoveHBox = new HBox();
        addRemoveHBox.setSpacing(5);
        addRemoveHBox.setAlignment(Pos.CENTER);
        addRemoveHBox.getChildren().add(addBtn);
        addRemoveHBox.getChildren().add(removeBtn);

        // Add a listener to the string list, so when it changes we update the Set
        simpleList.getItems().addListener((ListChangeListener<String>) c -> {
            // Clear the current set's contents, and add the new ones
            set.clear();
            set.addAll(simpleList.getItems());
        });

        // Add buttons to the container
        this.setSpacing(5);
        this.getChildren().add(simpleList);
        this.getChildren().add(addRemoveHBox);
    }
}
