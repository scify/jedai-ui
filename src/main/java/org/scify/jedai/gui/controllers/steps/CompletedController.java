package org.scify.jedai.gui.controllers.steps;

import com.google.inject.Inject;
import com.google.inject.Injector;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.util.StringConverter;
import org.scify.jedai.datamodel.EquivalenceCluster;
import org.scify.jedai.gui.controllers.EntityClusterExplorationController;
import org.scify.jedai.gui.model.WorkflowResult;
import org.scify.jedai.gui.nodes.DetailsCell;
import org.scify.jedai.gui.utilities.DialogHelper;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.utilities.RadioButtonHelper;
import org.scify.jedai.gui.utilities.WorkflowManager;
import org.scify.jedai.gui.utilities.console_area.ConsoleArea;
import org.scify.jedai.gui.utilities.console_area.MultiOutputStream;
import org.scify.jedai.gui.wizard.WizardData;
import org.scify.jedai.utilities.ClustersPerformance;
import org.scify.jedai.utilities.PrintToFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.*;

public class CompletedController {
    public Button runBtn;
    public Button exportBtn;
    public VBox containerVBox;
    public Label numOfInstancesLabel;
    public Label numOfClustersLabel;
    public HBox gaugesHBox;
    public ProgressIndicator progressIndicator;
    public TextArea logTextArea;
    public Label totalTimeLabel;
    public TabPane resultsTabPane;
    public TableView workbenchTable;
    public Button exploreBtn;
    public VBox autoConfigContainer;
    public ComboBox outputFormatCombobox;

    private SingleSelectionModel<Tab> tabSelectionModel;
    private final ObservableList<WorkflowResult> tableData = FXCollections.observableArrayList();

    private Gauge f1Gauge;
    private Gauge recallGauge;
    private Gauge precisionGauge;

    private List<WizardData> detailedRunData;
    private EquivalenceCluster[] entityClusters;

    private WorkflowManager workflowMgr;

    @Inject
    private Injector injector;

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Initialize list of detailed run data
        detailedRunData = new ArrayList<>();

        // Create gauges
        recallGauge = newGauge("Recall");
        gaugesHBox.getChildren().add(recallGauge);

        precisionGauge = newGauge("Precision");
        gaugesHBox.getChildren().add(precisionGauge);

        f1Gauge = newGauge("F1-measure");
        gaugesHBox.getChildren().add(f1Gauge);

        // Setup text area as log
        ConsoleArea ca = new ConsoleArea(logTextArea);
        PrintStream ps = new PrintStream(ca, true);

        MultiOutputStream multiOut = new MultiOutputStream(System.out, ps);
        MultiOutputStream multiErr = new MultiOutputStream(System.err, ps);

        PrintStream out = new PrintStream(multiOut);
        PrintStream err = new PrintStream(multiErr);

        System.setOut(out);
        System.setErr(err);

        // Save the TabPane's selection model to use it for resetting the selected tab later
        tabSelectionModel = resultsTabPane.getSelectionModel();

        // Add radio buttons for configuring automatic configuration options
        // todo: Show in UI that holistic grid is not supported
        Label l = new Label("Automatic Configuration Type");
        l.setFont(Font.font("System", FontWeight.BOLD, 12));
        autoConfigContainer.getChildren().add(l);
        List<String> autoConfigTypes = Arrays.asList(
                JedaiOptions.AUTOCONFIG_HOLISTIC,
                JedaiOptions.AUTOCONFIG_STEPBYSTEP
        );
        RadioButtonHelper.createButtonGroup(autoConfigContainer, autoConfigTypes, model.autoConfigTypeProperty());

        autoConfigContainer.getChildren().add(new Separator());
        l = new Label("Search Type");
        l.setFont(Font.font("System", FontWeight.BOLD, 12));
        autoConfigContainer.getChildren().add(l);
        List<String> searchTypes = Arrays.asList(
                JedaiOptions.AUTOCONFIG_RANDOMSEARCH,
                JedaiOptions.AUTOCONFIG_GRIDSEARCH
        );
        RadioButtonHelper.createButtonGroup(autoConfigContainer, searchTypes, model.searchTypeProperty());

        // Add output options to the output format combobox
        ObservableList<String> outputFormats = FXCollections.observableArrayList(
                JedaiOptions.CSV,
                JedaiOptions.XML,
                JedaiOptions.RDF
        );
        outputFormatCombobox.setItems(outputFormats);

        // Disable the export button when the output format isn't selected or when the exploration button is disabled
        exportBtn.disableProperty().bind(
                outputFormatCombobox.valueProperty().isNull()
                        .or(exploreBtn.disabledProperty())
        );

        // Setup table for previous results (Workbench)
        initGrid();
    }

    /**
     * Initialize the workbench grid which shows the results of previous JedAI runs
     */
    private void initGrid() {
        // Set grid properties
        workbenchTable.setEditable(false);
        workbenchTable.setItems(tableData);

        // Specify columns for grid
        Map<String, String> tableCols = new LinkedHashMap<>();
        tableCols.put("Run #", "runNumber");
        tableCols.put("Recall", "recall");
        tableCols.put("Precision", "precision");
        tableCols.put("F1-measure", "f1Measure");
        tableCols.put("Total time (sec.)", "totalTime");
        tableCols.put("Input instances", "inputInstances");
        tableCols.put("Clusters #", "numOfClusters");

        List<String> colsToFormat = Arrays.asList("Recall", "Precision", "F1-measure");

        int colsNum = tableCols.size() + 1; // Add 1 because we add the details column later

        // Create column objects
        for (String colName : tableCols.keySet()) {
            TableColumn col = new TableColumn(colName);
            col.setCellValueFactory(new PropertyValueFactory<WorkflowResult, String>(tableCols.get(colName)));

            // Set the width to be the same for all columns (subtract not needed but prevents horizontal scrollbar...)
            col.prefWidthProperty().bind(workbenchTable.widthProperty().multiply(1.0 / colsNum).subtract(1));

            if (colsToFormat.contains(colName)) {
                // Add number formatter which shows maximum 3 fraction digits
                col.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
                    private final NumberFormat nf = NumberFormat.getNumberInstance();

                    {
                        nf.setMaximumFractionDigits(3);
                        nf.setMinimumFractionDigits(1);
                    }

                    @Override
                    public String toString(Double object) {
                        return nf.format(object);
                    }

                    @Override
                    public Double fromString(String string) {
                        // Not needed because we don't edit the table
                        return null;
                    }
                }));
            }

            // Add column to the table
            workbenchTable.getColumns().add(col);
        }

        // Add details button column
        TableColumn detailsBtnCol = new TableColumn("Details");
        detailsBtnCol.setCellFactory(param -> new DetailsCell(this.detailedRunData, this.injector));
        workbenchTable.getColumns().add(detailsBtnCol);
    }

    /**
     * Generate a new Medusa Gauge for showing a clustering accuracy metric
     *
     * @param title Title of gauge
     * @return Gauge
     */
    private Gauge newGauge(String title) {
        return GaugeBuilder.create()
                .skinType(SkinType.HORIZONTAL)
                .minValue(0.0)
                .maxValue(1.0)
                .tickLabelDecimals(1)
                .title(title)
                .decimals(3)
                .animated(true)
                .animationDuration(1500)
                .build();
    }

    /**
     * Update the progress of the progressIndicator in the UI thread
     *
     * @param percentage Percentage to set indicator to (in range [0, 1])
     */
    private void updateProgress(double percentage) {
        Platform.runLater(() -> progressIndicator.setProgress(percentage));
    }

    @FXML
    private void runAlgorithmBtnHandler() {
        // Show the progress indicator
        progressIndicator.setVisible(true);
        progressIndicator.setProgress(0.0);

        // Reset console area
        logTextArea.clear();

        workflowMgr = new WorkflowManager(model);

        // Runnable that will run algorithm in separate thread
        new Thread(() -> {
            // Disable the step control buttons
            model.setWorkflowRunning(true);

            // Disable exploration button
            exploreBtn.setDisable(true);

            // Create performance variables
            long startTime = System.currentTimeMillis();

            try {
                // Step 1: Data reading
                workflowMgr.readDatasets(true);

                // Set progress indicator to 20%
                updateProgress(0.2);

                // Prepare methods for rest of workflow
                workflowMgr.createMethodInstances();

                ClustersPerformance clp = workflowMgr.executeWorkflow();

                if (clp == null) {
                    Platform.runLater(() -> DialogHelper.showError("Workflow execution problem",
                            "A problem occurred while running the workflow!",
                            "ClustersPerformance while running the final workflow is null!"));
                    return;
                }

                entityClusters = workflowMgr.getEntityClusters();

                // Set gauge values
                f1Gauge.setValue(clp.getFMeasure());
                recallGauge.setValue(clp.getRecall());
                precisionGauge.setValue(clp.getPrecision());

                // Set progress indicator to 100%
                updateProgress(1.0);

                // Get final run values
                double totalTimeSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
                int inputInstances = workflowMgr.getProfilesD1().size();
                int numOfClusters = entityClusters.length;
                double recall = clp.getRecall();
                double precision = clp.getPrecision();
                double f1 = clp.getFMeasure();

                // Create entry for Workbench table
                tableData.add(new WorkflowResult(
                        new SimpleIntegerProperty(tableData.size() + 1),
                        new SimpleDoubleProperty(recall),
                        new SimpleDoubleProperty(precision),
                        new SimpleDoubleProperty(f1),
                        new SimpleDoubleProperty(totalTimeSeconds),
                        new SimpleIntegerProperty(inputInstances),
                        new SimpleIntegerProperty(numOfClusters),
                        new SimpleIntegerProperty(tableData.size())
                ));

                // Add a copy of current WizardData to the list
                detailedRunData.add(WizardData.cloneData(model));

                // Update labels and JavaFX UI components from UI thread
                Platform.runLater(() -> {
                    // Set label values and show them
                    numOfInstancesLabel.setText("Input instances: " + inputInstances);
                    numOfInstancesLabel.setVisible(true);

                    totalTimeLabel.setText("Total running time: " + String.format("%.1f", totalTimeSeconds) + " sec.");
                    totalTimeLabel.setVisible(true);

                    numOfClustersLabel.setText("Number of clusters: " + numOfClusters);
                    numOfClustersLabel.setVisible(true);

                    // Enable exploration button
                    exploreBtn.setDisable(false);
                });
            } catch (Exception e) {
                // Exception occurred, show alert with information about it
                Platform.runLater(() -> DialogHelper.showError("Exception",
                        "An exception occurred while running the workflow!",
                        "Details: " + e.toString() + " (" + e.getMessage() + ")"));

                // Print stack trace
                e.printStackTrace();

                // Set workflowRunning boolean to false
                model.setWorkflowRunning(false);
            }

            // Workflow ran, set workflowRunning boolean to false
            model.setWorkflowRunning(false);
        }).start();
    }

    /**
     * Ask the user for a filename with a save file dialog, and save a CSV with the entity clusters
     */
    public void exportBtnHandler() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV File", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(containerVBox.getScene().getWindow());

        if (file != null) {
            // Results export to CSV
            try {
                PrintToFile.toCSV(workflowMgr.getProfilesD1(), workflowMgr.getProfilesD2(), entityClusters,
                        file.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reset the view of the step
     */
    public void resetData() {
        // Disable exploration button
        exploreBtn.setDisable(true);

        // Hide time measurements
        numOfInstancesLabel.setVisible(false);
        totalTimeLabel.setVisible(false);
        numOfClustersLabel.setVisible(false);

        // Reset gauges
        f1Gauge.setValue(0.0);
        recallGauge.setValue(0.0);
        precisionGauge.setValue(0.0);

        // Reset Details tab
        logTextArea.setText("");

        // Reset progress indicator
        progressIndicator.setVisible(false);
        progressIndicator.setProgress(0.0);

        // Go to first tabset tab
        tabSelectionModel.selectFirst();
    }

    /**
     * Explore the results of the dataset. Assumes that it will not be called when this is not possible (because the
     * button is supposed to be disabled when that's the case...)
     *
     * @param actionEvent Button event
     */
    public void exploreResults(ActionEvent actionEvent) {
        // Get LIST of equivalence clusters (from array)
        List<EquivalenceCluster> duplicates = Arrays.asList(this.entityClusters);

        // Sort clusters from largest (in size) to smallest
        duplicates.sort((o1, o2) -> {
            Integer o1Size = o1.getEntityIdsD1().size() + o1.getEntityIdsD2().size();
            Integer o2Size = o2.getEntityIdsD1().size() + o2.getEntityIdsD2().size();
            return -o1Size.compareTo(o2Size);
        });

        // Load FXML for exploration window and get the controller
        Parent root = DialogHelper.loadFxml(this.getClass(), injector,
                "wizard-fxml/EntityClusterExploration.fxml");
        Object controller = null;
        if (root != null) {
            controller = root.getProperties().get("controller");
        }

        // Set properties of the controller & show window
        if (controller instanceof EntityClusterExplorationController) {
            // Cast the controller instance since we know it's safe here
            EntityClusterExplorationController popupController = (EntityClusterExplorationController) controller;
            popupController.setTitle("Results Exploration");

            // Give the configuration options to the controller
            if (model.getErType().equals(JedaiOptions.DIRTY_ER)) {
                popupController.setDuplicates(duplicates, workflowMgr.getProfilesD1());
            } else {
                popupController.setDuplicates(duplicates, workflowMgr.getProfilesD1(), workflowMgr.getProfilesD2());
            }

            // Create the popup
            DialogHelper.showScene(root, Modality.WINDOW_MODAL, false,
                    "JedAI - Results Exploration");
        } else {
            // This shouldn't ever happen.
            System.err.println("Error when showing the results exploration popup (Wrong controller instance?)");
        }
    }
}
