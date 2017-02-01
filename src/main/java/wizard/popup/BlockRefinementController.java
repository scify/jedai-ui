package wizard.popup;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import wizard.WizardData;

public class BlockRefinementController {
    public ListView<String> list;
    public ListView<String> selectedList;
    public Button saveBtn;

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Create lists for selection
        list.setItems(FXCollections.observableArrayList(
                "Block Filtering",
                "Block Scheduling",
                "Size-based Block Purging",
                "Comparison-based Block Purging"
        ));

        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        list.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            selectedList.setItems(list.getSelectionModel().getSelectedItems());
        });

        // Set selected values as selected in the list, if they exist in model
        if (model.getBlockProcessingMethods() != null && !model.getBlockProcessingMethods().isEmpty()) {
            for (String selectedValue : model.getBlockProcessingMethods()) {
                list.getSelectionModel().select(selectedValue);
            }
        }
    }

    public void saveBtnHandler(ActionEvent actionEvent) {
        // Set the model's value to be the selected items from the list
        model.blockProcessingMethodsProperty().setValue(list.getSelectionModel().getSelectedItems());

        // Close the dialog window
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }
}
