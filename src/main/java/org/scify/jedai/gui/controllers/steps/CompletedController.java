package org.scify.jedai.gui.controllers.steps;

import com.google.inject.Inject;
import com.google.inject.Injector;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.scify.jedai.datamodel.EquivalenceCluster;
import org.scify.jedai.datawriter.ClustersPerformanceWriter;
import org.scify.jedai.gui.controllers.EntityClusterExplorationController;
import org.scify.jedai.gui.model.WorkflowResult;
import org.scify.jedai.gui.nodes.DetailsTreeCell;
import org.scify.jedai.gui.nodes.NonNegativeTreeTableCell;
import org.scify.jedai.gui.utilities.DialogHelper;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.utilities.RadioButtonHelper;
import org.scify.jedai.gui.utilities.WorkflowManager;
import org.scify.jedai.gui.utilities.console_area.ConsoleArea;
import org.scify.jedai.gui.utilities.console_area.MultiOutputStream;
import org.scify.jedai.gui.wizard.WizardData;
import org.scify.jedai.utilities.ClustersPerformance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompletedController {
    public Button runBtn;
    public Button exportBtn;
    public VBox containerVBox;
    public Label numOfInstancesLabel;
    public Label numOfClustersLabel;
    public HBox gaugesHBox;
    public TextArea logTextArea;
    public Label totalTimeLabel;
    public TabPane resultsTabPane;
    public Button exploreBtn;
    public Button showPlotBtn;
    public VBox autoConfigContainer;
    public ComboBox<String> outputFormatCombobox;
    public Label statusLabel;
    public TreeTableView<WorkflowResult> resultsTable;  // Tree table with results

    private SingleSelectionModel<Tab> tabSelectionModel;
    private final ObservableList<WorkflowResult> tableData = FXCollections.observableArrayList();

    private Gauge f1Gauge;
    private Gauge recallGauge;
    private Gauge precisionGauge;

    private List<WizardData> previousRunConfigs;
    private EquivalenceCluster[] entityClusters;

    private WorkflowManager workflowMgr;

    @Inject
    private Injector injector;

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Initialize list of detailed run data
        previousRunConfigs = new ArrayList<>();

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

        // Get the last 2 radio buttons of the container, which are the search types, in order to be able to disable
        // them later
        List<Node> searchRadioBtns = new ArrayList<>();
        List<Node> autoConfigNodes = autoConfigContainer.getChildren();
        for (Node n : autoConfigNodes) {
            if (n instanceof RadioButton) {
                if (autoConfigNodes.indexOf(n) > autoConfigNodes.size() - 3) {
                    searchRadioBtns.add(n);
                }
            }
        }

        // Add listener to disable search type selection for holistic auto configuration
        model.autoConfigTypeProperty().addListener((observable, oldValue, newValue) -> {
            // If the new selection is holistic search, select random search
            if (newValue.equals(JedaiOptions.AUTOCONFIG_HOLISTIC)) {
                model.setSearchType(JedaiOptions.AUTOCONFIG_RANDOMSEARCH);
            }

            // Enable or disable the radio buttons
            toggleNodes(searchRadioBtns, newValue.equals(JedaiOptions.AUTOCONFIG_HOLISTIC));
        });

        // Enable or disable the radio buttons depending on the initially selected auto-config. type
        toggleNodes(searchRadioBtns, model.getAutoConfigType().equals(JedaiOptions.AUTOCONFIG_HOLISTIC));

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
        initResultsGrid();
    }

    /**
     * Enable or disable a list of nodes (used for the automatic configuration radio buttons)
     *
     * @param nodes   Nodes to enable or disable
     * @param disable Whether to disable the nodes or enable them
     */
    private void toggleNodes(List<Node> nodes, boolean disable) {
        for (Node n : nodes) {
            n.setDisable(disable);
        }
    }

    /**
     * Initialize the workbench treeview grid which shows the results of previous JedAI runs.
     */
    private void initResultsGrid() {
        resultsTable.setEditable(false);
        resultsTable.setShowRoot(false);

        // Create root node
        TreeItem<WorkflowResult> root = new TreeItem<>(new WorkflowResult("root", -1, -1,
                -1, -1, -1, -1, -1));
        resultsTable.setRoot(root);

        // Generate grid columns (with label, property name and column width relative to other columns...)
        List<ImmutableTriple<String, String, Integer>> tableCols = Arrays.asList(
                new ImmutableTriple<>("Run #", "resultName", 3),
                new ImmutableTriple<>("Recall", "recallRounded", 1),
                new ImmutableTriple<>("Precision", "precisionRounded", 1),
                new ImmutableTriple<>("F1-measure", "fMeasureRounded", 1),
                new ImmutableTriple<>("Total time (sec.)", "totalTime", 1),
                new ImmutableTriple<>("Input instances", "inputInstances", 1),
                new ImmutableTriple<>("Clusters #", "numOfClusters", 1)
        );

        // Sum the column widths (+1 because we add Details column later)
        int totalWidth = 1 + tableCols.stream().map(ImmutableTriple::getRight).mapToInt(Integer::intValue).sum();

        // Create column objects
        for (ImmutableTriple<String, String, Integer> p : tableCols) {
            String colName = p.getLeft();
            String propertyName = p.getMiddle();
            int colWidth = p.getRight();

            // Create column
            TreeTableColumn<WorkflowResult, Object> col = new TreeTableColumn<>(colName);
            col.setCellValueFactory(new TreeItemPropertyValueFactory<>(propertyName));
            col.setCellFactory(param -> new NonNegativeTreeTableCell());

            // Set width of the column (subtract not needed but prevents horizontal scrollbar...)
            col.prefWidthProperty().bind(
                    resultsTable.widthProperty().multiply(1.0 / totalWidth).multiply(colWidth).subtract(colWidth));

            // Add column to table
            resultsTable.getColumns().add(col);
        }

        // Add details button column
        TreeTableColumn<WorkflowResult, Object> detailsBtnCol = new TreeTableColumn<>("Details");
        detailsBtnCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("detailsId"));
        detailsBtnCol.setCellFactory(
                param -> new DetailsTreeCell(root.getChildren(), this.previousRunConfigs, this.injector));
        resultsTable.getColumns().add(detailsBtnCol);
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
     * Add a workflow result to the tree table view. Requires the total performance of the workflow, and the manager
     * that ran it in order to get the detailed performance per step.
     *
     * @param total Total workflow performance result
     * @param mgr   WorkflowManager to get detailed performance per step
     */
    private void addResultToTable(WorkflowResult total, WorkflowManager mgr) {
        // Create root node for the total workflow result
        TreeItem<WorkflowResult> totalResult = new TreeItem<>(total);

        //  Get performance for each step from the workflow manager and add them as children of totalResult
        for (WorkflowResult wr : mgr.getPerformancePerStep()) {
            totalResult.getChildren().add(
                    new TreeItem<>(wr)
            );
        }

        // Add node for this workflow to the table's root node
        resultsTable.getRoot().getChildren().add(totalResult);
    }

    @FXML
    private void runAlgorithmBtnHandler() {
        // Reset console area
        logTextArea.clear();

        workflowMgr = new WorkflowManager(model);

        // Runnable that will run algorithm in separate thread
        new Thread(() -> {
            // Disable the step control buttons & exploration button
            model.setWorkflowRunning(true);
            exploreBtn.setDisable(true);
            showPlotBtn.setDisable(true);

            // Set the starting time
            long startTime = System.currentTimeMillis();

            try {
                // Data reading
                Platform.runLater(() -> statusLabel.setText("Reading datasets..."));
                workflowMgr.readDatasets(true);

                // Prepare methods for rest of workflow
                boolean isCleanCleanEr = (model.getErType().equals(JedaiOptions.CLEAN_CLEAN_ER));
                workflowMgr.createMethodInstances(isCleanCleanEr);

                // Execute the workflow
                ClustersPerformance clp = workflowMgr.executeFullWorkflow(statusLabel);

                if (clp == null) {
                    DialogHelper.showError("Workflow execution problem",
                            "A problem occurred while running the workflow!",
                            "ClustersPerformance while running the final workflow is null!");
                    return;
                }

                // Get the generated clusters
                entityClusters = workflowMgr.getEntityClusters();

                // Set gauge values & status label
                f1Gauge.setValue(clp.getFMeasure());
                recallGauge.setValue(clp.getRecall());
                precisionGauge.setValue(clp.getPrecision());
                Platform.runLater(() -> statusLabel.setText("Completed!"));

                // Get final run values
                double totalTimeSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
                int inputInstances = workflowMgr.getProfilesD1().size();
                int numOfClusters = entityClusters.length;
                double recall = clp.getRecall();
                double precision = clp.getPrecision();
                double f1 = clp.getFMeasure();

                // Add entry to the workbench
                WorkflowResult total = new WorkflowResult("Run " + (tableData.size() + 1), recall, precision,
                        f1, totalTimeSeconds, inputInstances, numOfClusters, tableData.size());
                addResultToTable(total, workflowMgr);
                tableData.add(total);

                // Add a copy of current WizardData to the list
                previousRunConfigs.add(WizardData.cloneData(model));

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
                    showPlotBtn.setDisable(false);
                });
            } catch (Exception e) {
                // Exception occurred, show alert with information about it
                DialogHelper.showError("Exception",
                        "An exception occurred while running the workflow!",
                        "Details: " + e.toString() + " (" + e.getMessage() + ")");

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
     * Ask the user for a filename with a save file dialog, and save a file with the entity clusters
     */
    public void exportBtnHandler() {
        FileChooser fileChooser = new FileChooser();

        // Get selected output format
        String outputFormat = outputFormatCombobox.getValue().toString();

        // Set extension
        String extension = null;
        switch (outputFormat) {
            case JedaiOptions.CSV:
                extension = "*.csv";
                break;
            case JedaiOptions.XML:
            case JedaiOptions.RDF:
                extension = "*.xml";
                break;
        }

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(outputFormat + " File", extension);
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(containerVBox.getScene().getWindow());

        if (file != null) {
            // Create performance writer object
            ClustersPerformanceWriter cpw = new ClustersPerformanceWriter(
                    workflowMgr.getEntityClusters(),
                    workflowMgr.getDuplicatePropagation()
            );

            // Export the results to file
            try {
                switch (outputFormat) {
                    case JedaiOptions.CSV:
                        // Output CSV
                        cpw.printDetailedResultsToCSV(workflowMgr.getProfilesD1(), workflowMgr.getProfilesD2(),
                                file.getAbsolutePath());
                        break;
                    case JedaiOptions.XML:
                        cpw.printDetailedResultsToXML(workflowMgr.getProfilesD1(), workflowMgr.getProfilesD2(),
                                file.getAbsolutePath());
                        break;
                    case JedaiOptions.RDF:
                        cpw.printDetailedResultsToRDF(workflowMgr.getProfilesD1(), workflowMgr.getProfilesD2(),
                                file.getAbsolutePath());
                        break;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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

    /**
     * Shows a Line chart with the recall and the [Normalized number of emitted records] ?
     *
     * @param actionEvent Click event of the button
     */
    public void showPlot(ActionEvent actionEvent) {
        // Create stage that will show the plot
        Stage stage = new Stage();
        stage.setTitle("Progressive Workflow ROC Curve");

        // Create X axis
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(true);
//        xAxis.setLabel("Normalized number of emitted records");
        xAxis.setLabel("Iterations");

        // Create Y axis
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(true);
        yAxis.setLabel("Recall %");

        // Create the chart
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Progressive Workflow ROC Curve");

        // Define series
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        lineChart.setLegendVisible(false);

        // Get the data from the workflow manager and add it to the series
        List<Double> data = this.workflowMgr.getRecallCurve();
        for (int i = 0; i < data.size(); i++) {
            series.getData().add(new XYChart.Data<>(i + 1, data.get(i)));
        }

        // Add the series to the chart
        lineChart.getData().add(series);

        // Show the stage with the plot
        stage.setScene(new Scene(lineChart, 800, 600));
        stage.show();
    }
}
