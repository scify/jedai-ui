package main.wizard.steps;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import main.wizard.Submit;
import main.wizard.Validate;
import main.wizard.WizardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Step3Controller {
    public ComboBox<String> blockProcessingMethodCombobox;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        // Add options to combobox
        ObservableList<String> comboboxOptions =
                FXCollections.observableArrayList(
                        "Block-refinement methods",
//                        "Block Filtering",
//                        "Block Scheduling",
//                        "Size-based Block Purging",
//                        "Comparison-based Block Purging",
                        "Comparison-refinement methods"
//                        "Comparison Propagation",
//                        "Cardinality Edge Pruning (CEP)",
//                        "Cardinality Node Pruning (CNP)",
//                        "Weighed Edge Pruning (WEP)",
//                        "Weighed Node Pruning (WNP)",
//                        "Reciprocal Cardinality Node Pruning (ReCNP)",
//                        "Reciprocal Weighed Node Pruning (ReWNP)"
                );
        blockProcessingMethodCombobox.setItems(comboboxOptions);

        // Bind combobox selection to model
        blockProcessingMethodCombobox.valueProperty().bindBidirectional(model.blockProcessingTypeProperty());
    }

    @Validate
    public boolean validate() throws Exception {
        if (blockProcessingMethodCombobox.getValue() == null || blockProcessingMethodCombobox.getValue().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Block Processing Method");
            alert.setHeaderText("Missing Field");
            alert.setContentText("Selecting a Block Processing Method is required.");
            alert.showAndWait();
            return false;
        }

        //todo: also check the actual method selection, not just type like right now

        return true;
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 3");
        }
    }

    public void methodTypeSelectionHandler(ActionEvent actionEvent) {
        String type = model.getBlockProcessingType();

        if (type.equals("Block-refinement methods")) {
            // todo
            System.out.println("Block refinement has been chosen");
        } else if (type.equals("Comparison-refinement methods")) {
            // todo
            System.out.println("Comparison refinement methods popup");
        }
    }
}


