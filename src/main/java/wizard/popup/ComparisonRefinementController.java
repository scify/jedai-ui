package wizard.popup;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import wizard.WizardData;

public class ComparisonRefinementController {
    public ComboBox<String> methodCombobox;
    public Button saveBtn;

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Add options to combobox
        ObservableList<String> comboboxOptions =
                FXCollections.observableArrayList(
                        "Comparison Propagation",
                        "Cardinality Edge Pruning (CEP)",
                        "Cardinality Node Pruning (CNP)",
                        "Weighed Edge Pruning (WEP)",
                        "Weighed Node Pruning (WNP)",
                        "Reciprocal Cardinality Node Pruning (ReCNP)",
                        "Reciprocal Weighed Node Pruning (ReWNP)"
                );

        methodCombobox.setItems(comboboxOptions);

        // Restore selection if there is any saved in the model
        if (model.getBlockProcessingMethods() != null && !model.getBlockProcessingMethods().isEmpty()) {
            // There can only be 1 selection, so get item at index 0
            String previousSelection = model.getBlockProcessingMethods().get(0);

            // Set on combobox
            methodCombobox.setValue(previousSelection);
        }
    }

    public void saveBtnHandler() {
        // Save selection
        String selectedItem = methodCombobox.getValue();

        model.setBlockProcessingMethods(FXCollections.observableArrayList(
                selectedItem
        ));

        // Close the dialog window
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }
}
