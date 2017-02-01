package main.wizard.steps;

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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.wizard.Submit;
import main.wizard.Validate;
import main.wizard.WizardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Step3Controller {
    public ComboBox<String> blockProcessingMethodCombobox;
    public VBox containerVBox;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    Injector injector;

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

        Stage primaryStage = (Stage) containerVBox.getScene().getWindow();

        if (type.equals("Block-refinement methods")) {
            Stage dialog = new Stage();
            Parent root;
            try {
                // Load the FXML file and inject model to its controller
                final JavaFXBuilderFactory bf = new JavaFXBuilderFactory();
                final Callback<Class<?>, Object> cb = (clazz) -> injector.getInstance(clazz);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/wizard-fxml/popup/BlockProcessingPopup.fxml"), null, bf, cb);
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
