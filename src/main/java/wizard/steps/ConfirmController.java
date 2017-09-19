package wizard.steps;

import com.google.inject.Inject;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wizard.Submit;
import wizard.WizardData;

public class ConfirmController {
    public GridPane paramsGrid;
    private Logger log = LoggerFactory.getLogger(ConfirmController.class);

    @Inject
    private WizardData model;

    /**
     * Create a bold label with a specific text
     *
     * @param text Text to use for value
     * @return Label with bold text
     */
    private Label boldLabel(String text) {
        // Create label with the given text and set its font to bold
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 12));

        return l;
    }

    /**
     * Create a Label whose value is bound to an observable value.
     *
     * @param observable Value to bind to label text
     * @return Label with bound value
     */
    private Label boundLabel(ObservableValue<? extends String> observable) {
        // Create label
        Label l = new Label();

        // Bind its value to the observable
        l.textProperty().bind(observable);

        return l;
    }

    @FXML
    public void initialize() {
        int rows = 0;

        // Add ER type
        paramsGrid.addRow(rows++, boldLabel("ER Type"), boundLabel(model.erTypeProperty()));

        // Add Dataset 1 type
        paramsGrid.addRow(rows++, boldLabel("Dataset 1 Type"), boundLabel(model.entityProfilesD1TypeProperty()));

        //todo: Add Dataset 1 parameters

        //todo: Add Dataset 2 type + parameters, only when ER type is Clean-Clean

        // Add ground truth type
        paramsGrid.addRow(rows++, boldLabel("Ground Truth Type"), boundLabel(model.groundTruthTypeProperty()));

        //todo: Add Ground Truth parameters

        // Add Block Building method
        paramsGrid.addRow(rows++, boldLabel("Block Building Method"), boundLabel(model.blockBuildingProperty()));

        //todo: Add Block Building parameters

        //todo: Add Block Cleaning methods (+parameters?)
        paramsGrid.addRow(rows++, boldLabel("Block Cleaning Methods"), boldLabel("[...]"));

        // Add Comparison Cleaning method
        paramsGrid.addRow(rows++, boldLabel("Comparison Cleaning Method"), boundLabel(model.comparisonCleaningProperty()));

        //todo: Add Comparison Cleaning parameters

        // Add Entity Matching method
        paramsGrid.addRow(rows++, boldLabel("Entity Matching Method"), boundLabel(model.entityMatchingProperty()));

        // Add Entity Matching Representation Model & Similarity Metric
        paramsGrid.addRow(rows++, boldLabel("Representation Model"), boundLabel(model.representationModelProperty()));
        paramsGrid.addRow(rows++, boldLabel("Similarity Metric"), boundLabel(model.similarityMetricProperty()));

        // Add Entity Clustering algorithm
        paramsGrid.addRow(rows++, boldLabel("Entity Clustering Algorithm"), boundLabel(model.entityClusteringProperty()));

        //todo: Add Entity Clustering parameters
    }

    /**
     * Set a new model, and rerun the initialization. Useful when showing a detailed configuration in a popup.
     *
     * @param newModel New model to show
     */
    public void setModel(WizardData newModel) {
        // Set the new model
        this.model = newModel;

        // Rerun the initialization to bind UI items to the new model
        paramsGrid.getChildren().clear();
        initialize();
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] Confirmation step completed");
        }
    }
}
