package wizard.steps;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

import java.io.IOException;

public class Step3Controller {
    public ComboBox<String> blockProcessingMethodCombobox;
    public VBox containerVBox;
    public ListView selectionList;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    private
    Injector injector;

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        // Add options to combobox
        ObservableList<String> comboboxOptions =
                FXCollections.observableArrayList(
                        "No block processing",
                        "Block-refinement methods",
                        "Comparison-refinement methods"
                );
        blockProcessingMethodCombobox.setItems(comboboxOptions);

        // Bind combobox selection to model
        blockProcessingMethodCombobox.valueProperty().bindBidirectional(model.blockProcessingTypeProperty());

        // todo: bind selectionList to the selected method parameters
    }

    @Validate
    public boolean validate() throws Exception {
        if (blockProcessingMethodCombobox.getValue() == null || blockProcessingMethodCombobox.getValue().isEmpty()) {
            showError("Block Processing Method", "Missing Field",
                    "Selecting a Block Processing Method is required.");
            return false;
        }

        // If there is a selection other than "No block processing", check that parameters were also selected
        if (blockProcessingMethodCombobox.getValue().equals("Block-refinement methods") &&
                (model.getBlockProcessingMethods() == null || model.getBlockProcessingMethods().isEmpty())) {
            // No block refinement methods
            showError("Block-refinement Methods", "Missing Field",
                    "Selecting a Block-refinement method for Block Processing is required.");
            return false;
        } else if (blockProcessingMethodCombobox.getValue().equals("Comparison-refinement methods") &&
                (model.getBlockProcessingMethods() == null || model.getBlockProcessingMethods().isEmpty())) {
            // No comparison refinement methods
            showError("Comparison-refinement Methods", "Missing Field",
                    "Selecting a Comparison-refinement method for Block Processing is required.");
            return false;
        }

        return true;
    }

    /**
     * Show an error with the specified title, header and content
     *
     * @param title   Title of the alert window
     * @param header  Header of alert
     * @param content Message with more detail about the problem
     */
    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 3");
        }
    }

    public void methodSelectionBtnHandler(ActionEvent actionEvent) {
        // Get selected method
        // Get block processing type that was selected
        String type = model.getBlockProcessingType();

        Stage primaryStage = (Stage) containerVBox.getScene().getWindow();
        if (type != null && type.equals("Block-refinement methods")) {
            Stage dialog = new Stage();
            Parent root;
            try {
                // Load the FXML file and inject model to its controller
                final JavaFXBuilderFactory bf = new JavaFXBuilderFactory();
                final Callback<Class<?>, Object> cb = (clazz) -> injector.getInstance(clazz);

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("wizard-fxml/popup/BlockRefinementPopup.fxml"), null, bf, cb);
                root = loader.load();
                root.getProperties().put("controller", loader.getController());

                dialog.setScene(new Scene(root));
                dialog.setTitle("Block-refinement Method Selection");
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.initOwner(primaryStage);

                dialog.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type != null && type.equals("Comparison-refinement methods")) {
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
        } else {
            // Alert that says parameters can only be selected after you have selected a processing method
            showError("Block Processing Parameter Seletion", "No Block Matching method selected.",
                    "When you haven't selected a Block Processing method, you can't select parameters for it.");
        }
    }
}
