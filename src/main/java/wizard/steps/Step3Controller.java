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
import javafx.scene.control.Button;
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
    public Button selectMethodBtn;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    private Injector injector;

    @Inject
    private WizardData model;

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

    public void methodSelectionBtnHandler() {
        // Get block processing type that was selected
        String type = model.getBlockProcessingType();

        if (type == null)
            return;

        if (type.equals("Block-refinement methods")) {
            // Show block refinement modal
            showModal("wizard-fxml/popup/BlockRefinementPopup.fxml",
                    "Block-refinement Method Selection");
        } else if (type.equals("Comparison-refinement methods")) {
            // Show comparison refinement modal
            showModal("wizard-fxml/popup/ComparisonRefinementPopup.fxml",
                    "Comparison-refinement Method Selection");
        }
    }

    /**
     * Create and show a modal from the FXML at the given path.
     *
     * @param fxmlPath Path to the FXML
     * @param title    Title to put on modal dialog
     */
    private void showModal(String fxmlPath, String title) {
        // Declare some variables
        Stage primaryStage = (Stage) containerVBox.getScene().getWindow();
        Parent root;

        // Load the FXML file and inject model to its controller
        final JavaFXBuilderFactory bf = new JavaFXBuilderFactory();
        final Callback<Class<?>, Object> cb = (clazz) -> injector.getInstance(clazz);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath), null, bf, cb);

        try {
            root = loader.load();
            root.getProperties().put("controller", loader.getController());

            Stage dialog = new Stage();
            dialog.setScene(new Scene(root));
            dialog.setTitle(title);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);

            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void methodTypeChangeHandler(ActionEvent actionEvent) {
        //todo: clear selection from model

        // Make method selection button disabled if the method type selected is "No block processing"
        selectMethodBtn.setDisable(blockProcessingMethodCombobox.getValue().equals("No block processing"));
    }
}
