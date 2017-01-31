package main.wizard.steps;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.wizard.Submit;
import main.wizard.Validate;
import main.wizard.WizardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Step3Controller {
    public ComboBox<String> blockProcessingMethodCombobox;
    public VBox containerVBox;
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
                        "Comparison-refinement methods"
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
        // Get block processing type that was selected
        String type = model.getBlockProcessingType();

        // Get primary stage
        Stage primaryStage = (Stage) containerVBox.getScene().getWindow();

        if (type.equals("Block-refinement methods")) {
            // Create lists for selection
            ListView<String> list = new ListView<>(FXCollections.observableArrayList(
                    "Block Filtering",
                    "Block Scheduling",
                    "Size-based Block Purging",
                    "Comparison-based Block Purging"
            ));
            ListView<String> selectedList = new ListView<>();
            HBox listHBox = new HBox(list, selectedList);

            list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            list.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
                selectedList.setItems(list.getSelectionModel().getSelectedItems());
            });

            // todo: Bind selection list data to the model

            // Create modal dialog
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            VBox dialogVbox = new VBox(20);
            dialogVbox.getChildren().add(new Text("Select Block-refinement method:"));
            dialogVbox.getChildren().add(new Text("(Hold CTRL to select multiple)"));
            dialogVbox.getChildren().add(new Text("Selected items are shown on the right."));
            dialogVbox.getChildren().add(listHBox);
            Scene dialogScene = new Scene(dialogVbox, 350, 300);
            dialog.setScene(dialogScene);
            dialog.show();
        } else if (type.equals("Comparison-refinement methods")) {
            // todo
            /*
                Options for dropdown:
                "Comparison Propagation",
                "Cardinality Edge Pruning (CEP)",
                "Cardinality Node Pruning (CNP)",
                "Weighed Edge Pruning (WEP)",
                "Weighed Node Pruning (WNP)",
                "Reciprocal Cardinality Node Pruning (ReCNP)",
                "Reciprocal Weighed Node Pruning (ReWNP)"
             */
            System.out.println("Comparison refinement methods popup");
        }
    }
}
