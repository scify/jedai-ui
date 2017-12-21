package wizard.steps;

import com.google.inject.Inject;
import javafx.beans.property.ListProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import utils.JPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.BlockCleaningCustomComparator;
import utils.JedaiOptions;
import utils.RowHidingChangeListener;
import wizard.Submit;
import wizard.WizardData;

import java.util.Arrays;

public class ConfirmController {
    public GridPane paramsGrid;
    private Logger log = LoggerFactory.getLogger(ConfirmController.class);
    private RowHidingChangeListener changeListener;

    // Vertical gap between rows (actual gap is double this number)
    private final int vGap = 5;

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

    /**
     * Add a new row to the grid, with a label and value. For each new row, an empty RowConstaints object is created.
     *
     * @param rowIndex Index to add row
     * @param label    Label node of row
     * @param value    Value node of row
     * @return RowConstraints object for the newly added row
     */
    private RowConstraints addRow(int rowIndex, Label label, Node value) {
        // Set padding on the label
        label.setPadding(new Insets(vGap, 0, vGap, 0));

        // Add the new row
        paramsGrid.addRow(rowIndex, label, value);

        // Create the row constraints for it
        RowConstraints rowConstraints = new RowConstraints();
        paramsGrid.getRowConstraints().add(rowConstraints);

        return rowConstraints;
    }

    /**
     * Create a node for displaying the advanced configuration parameters of a method.
     *
     * @param parametersProperty List property, that contains the values of the method's parameters
     * @return Node that displays the given parameters and values
     */
    private Node parametersNode(ListProperty<JPair<String, Object>> parametersProperty) {
        // Create the node to show the parameters
        ListView<JPair<String, Object>> paramsList = new ListView<>();
        paramsList.setMaxHeight(60);
        //todo: add vgap to the list

        // Bind the ListView's items to the given parameters property
        //todo: Improve .toString() method of MutablePairs
        paramsList.itemsProperty().bind(parametersProperty);

        return paramsList;
    }

    @FXML
    public void initialize() {
        int rows = 0;

        // Add ER type
        addRow(rows++, boldLabel("ER Type"), boundLabel(model.erTypeProperty()));

        // Add Dataset 1 type
        addRow(rows++, boldLabel("Dataset 1 Type"), boundLabel(model.entityProfilesD1TypeProperty()));

        // Add Dataset 1 parameters
        addRow(rows++, boldLabel("Dataset 1 Reader Parameters"), parametersNode(model.entityProfilesD1ParametersProperty()));

        // Add Dataset 2 type & parameters (only shown for Clean-Clean ER)
        Label d2TypeTitle = boldLabel("Dataset 2 Type");
        Label d2TypeValue = boundLabel(model.entityProfilesD2TypeProperty());
        Label d2ParamsTitle = boldLabel("Dataset 2 Reader Parameters");
        Node d2ParamsValue = parametersNode(model.entityProfilesD2ParametersProperty());

        // Add the new nodes to their rows, and keep the row constraints objects
        RowConstraints d2TypeConstraints = addRow(rows++, d2TypeTitle, d2TypeValue);
        RowConstraints d2ParamsConstraints = addRow(rows++, d2ParamsTitle, d2ParamsValue);

        // Hide the nodes of the two rows when ER type is not Clean-Clean
        d2TypeTitle.visibleProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.CLEAN_CLEAN_ER));
        d2TypeValue.visibleProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.CLEAN_CLEAN_ER));
        d2ParamsTitle.visibleProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.CLEAN_CLEAN_ER));
        d2ParamsValue.visibleProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.CLEAN_CLEAN_ER));

        // When ER type is Dirty, set the row heights to 0 to hide them
        changeListener = new RowHidingChangeListener(Arrays.asList(
                d2TypeConstraints,
                d2ParamsConstraints
        ));
        model.erTypeProperty().addListener(changeListener);

        // Run the changed() function once manually, to set the initial values correctly
        changeListener.changed(model.erTypeProperty(), null, model.getErType());

        // Add ground truth type
        addRow(rows++, boldLabel("Ground Truth Type"), boundLabel(model.groundTruthTypeProperty()));

        // Add Ground Truth parameters
        addRow(rows++, boldLabel("Ground Truth Reader Parameters"), parametersNode(model.groundTruthParametersProperty()));

        // Add Block Building method
        addRow(rows++, boldLabel("Block Building Method"), boundLabel(model.blockBuildingProperty()));

        // Add Block Building parameters
        addRow(rows++, boldLabel("Block Building Parameters"), parametersNode(model.blockBuildingParametersProperty()));

        // Add Block Cleaning methods (sorted automatically)
        ListView<String> blockCleaningList = new ListView<>(
                model.getBlockCleaningMethods()
                        .sorted(new BlockCleaningCustomComparator())
        );
        blockCleaningList.setMaxHeight(80);

        addRow(rows++, boldLabel("Block Cleaning Methods"), blockCleaningList);
        //todo: Add Block Cleaning parameters for each method

        // Add Comparison Cleaning method
        addRow(rows++, boldLabel("Comparison Cleaning Method"), boundLabel(model.comparisonCleaningProperty()));

        // Add Comparison Cleaning parameters
        addRow(rows++, boldLabel("Comparison Cleaning Parameters"), parametersNode(model.comparisonCleaningParametersProperty()));

        // Add Entity Matching method
        addRow(rows++, boldLabel("Entity Matching Method"), boundLabel(model.entityMatchingProperty()));

        // Add Entity Matching Representation Model & Similarity Metric
        addRow(rows++, boldLabel("Representation Model"), boundLabel(model.representationModelProperty()));
        addRow(rows++, boldLabel("Similarity Metric"), boundLabel(model.similarityMetricProperty()));

        // Add Entity Clustering algorithm
        addRow(rows++, boldLabel("Entity Clustering Algorithm"), boundLabel(model.entityClusteringProperty()));

        // Add Entity Clustering parameters
        addRow(rows, boldLabel("Entity Clustering Parameters"), parametersNode(model.entityClusteringParametersProperty()));
    }

    /**
     * Set a new model, and rerun the initialization. Useful when showing a detailed configuration in a popup.
     *
     * @param newModel New model to show
     */
    public void setModel(WizardData newModel) {
        // Remove change listener from old model
        model.erTypeProperty().removeListener(changeListener);

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
