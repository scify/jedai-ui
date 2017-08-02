package wizard.steps;

import BlockBuilding.IBlockBuilding;
import BlockProcessing.IBlockProcessing;
import DataModel.AbstractBlock;
import DataModel.EntityProfile;
import DataModel.EquivalenceCluster;
import DataModel.SimilarityPairs;
import EntityClustering.IEntityClustering;
import EntityMatching.GroupLinkage;
import EntityMatching.IEntityMatching;
import EntityMatching.ProfileMatcher;
import Utilities.BlocksPerformance;
import Utilities.ClustersPerformance;
import Utilities.DataStructures.AbstractDuplicatePropagation;
import Utilities.Enumerations.BlockBuildingMethod;
import Utilities.Enumerations.RepresentationModel;
import Utilities.Enumerations.SimilarityMetric;
import Utilities.PrintToFile;
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
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.BlockCleaningCustomComparator;
import utils.CustomMethodConfiguration;
import utils.DataReadingHelper;
import utils.JedaiOptions;
import utils.console_area.ConsoleArea;
import utils.console_area.MultiOutputStream;
import wizard.MethodMapping;
import wizard.WizardData;
import workbench.DetailsCell;
import workbench.WorkflowResult;

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

    private SingleSelectionModel<Tab> tabSelectionModel;
    private final ObservableList<WorkflowResult> tableData = FXCollections.observableArrayList();

    private Gauge f1Gauge;
    private Gauge recallGauge;
    private Gauge precisionGauge;
    private Logger log = LoggerFactory.getLogger(CompletedController.class);

    private List<WizardData> detailedRunData;
    private List<EquivalenceCluster> entityClusters;

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
     * Generates a new Medusa Gauge for showing a clustering accuracy metric
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
     * Updates the progress of the progressIndicator in the UI thread
     *
     * @param percentage Percentage to set indicator to (in range [0, 1])
     */
    private void updateProgress(double percentage) {
        Platform.runLater(() -> {
            progressIndicator.setProgress(percentage);
        });
    }

    @FXML
    private void runAlgorithmBtnHandler() {
        // Show the progress indicator
        progressIndicator.setVisible(true);

        // Runnable that will run algorithm in separate thread
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            double overheadStart;
            double overheadEnd;
            BlocksPerformance blp;

            try {
                // Get profiles and ground truth paths from model
                String erType = model.getErType();

                // Step 1: Data reading
                List<EntityProfile> profilesD1 = DataReadingHelper.getEntities(
                        model.getEntityProfilesD1Path(),
                        model.getEntityProfilesD1Type());
                System.out.println("Input Entity Profiles\t:\t" + profilesD1.size());

                // In case Clean-Clear ER was selected, also read 2nd profiles file
                List<EntityProfile> profilesD2 = null;
                if (erType.equals(JedaiOptions.CLEAN_CLEAN_ER)) {
                    profilesD2 = DataReadingHelper.getEntities(
                            model.getEntityProfilesD2Path(),
                            model.getEntityProfilesD2Type()
                    );
                }

                // Read ground truth file
                AbstractDuplicatePropagation duplicatePropagation = DataReadingHelper.getGroundTruth(
                        model.getGroundTruthPath(),
                        model.getGroundTruthType(),
                        erType,
                        profilesD1,
                        profilesD2
                );

                System.out.println("Existing Duplicates\t:\t" + duplicatePropagation.getDuplicates().size());

                // Set progress indicator to 20%
                updateProgress(0.2);

                // Step 2: Block Building
                IBlockBuilding blockBuildingMethod = null;
                ObservableList blBuParams = model.getBlockBuildingParameters();

                BlockBuildingMethod blockingWorkflow = MethodMapping.blockBuildingMethods.get(model.getBlockBuilding());
                overheadStart = System.currentTimeMillis();

                // Check if the user set any custom parameters for block building
                if (blBuParams.isEmpty()) {
                    // No parameters found, use default configuration
                    blockBuildingMethod = BlockBuildingMethod.getDefaultConfiguration(blockingWorkflow);
                } else {
                    // Create the method with the saved parameters
                    blockBuildingMethod = CustomMethodConfiguration.configureBlockBuildingMethod(blockingWorkflow, blBuParams);
                }

                List<AbstractBlock> blocks;
                if (erType.equals(JedaiOptions.DIRTY_ER)) {
                    blocks = blockBuildingMethod.getBlocks(profilesD1);
                } else {
                    blocks = blockBuildingMethod.getBlocks(profilesD1, profilesD2);
                }

                System.out.println("Original blocks\t:\t" + blocks.size());

                // Print blocks performance
                overheadEnd = System.currentTimeMillis();
                blp = new BlocksPerformance(blocks, duplicatePropagation);
                blp.setStatistics();
                blp.printStatistics(overheadEnd - overheadStart, blockBuildingMethod.getMethodConfiguration(), blockBuildingMethod.getMethodName());

                // Set progress indicator to 40%
                updateProgress(0.4);

                // Step 3: Block Cleaning
                List<String> blockCleaningMethods = model.getBlockCleaningMethods();

                if (blockCleaningMethods != null) {
                    // Sort the methods in order to execute them in correct order
                    blockCleaningMethods.sort(new BlockCleaningCustomComparator());

                    // Execute the methods
                    for (String currentMethod : blockCleaningMethods) {
                        overheadStart = System.currentTimeMillis();

                        // Process blocks with this method
                        IBlockProcessing blockCleaningMethod = MethodMapping.getMethodByName(currentMethod);
                        if (blockCleaningMethod != null) {
                            blocks = blockCleaningMethod.refineBlocks(blocks);

                            // Print blocks performance
                            overheadEnd = System.currentTimeMillis();
                            blp = new BlocksPerformance(blocks, duplicatePropagation);
                            blp.setStatistics();
                            blp.printStatistics(overheadEnd - overheadStart, blockCleaningMethod.getMethodConfiguration(), blockCleaningMethod.getMethodName());
                        }
                    }
                }

                // Step 4: Comparison Cleaning
                String compCleaningMethod = model.getComparisonCleaningMethod();
                if (compCleaningMethod != null && !compCleaningMethod.equals(JedaiOptions.NO_CLEANING)) {
                    overheadStart = System.currentTimeMillis();

                    IBlockProcessing comparisonCleaningMethod = MethodMapping.getMethodByName(compCleaningMethod);
                    blocks = comparisonCleaningMethod.refineBlocks(blocks);

                    // Print blocks performance
                    overheadEnd = System.currentTimeMillis();
                    blp = new BlocksPerformance(blocks, duplicatePropagation);
                    blp.setStatistics();
                    blp.printStatistics(overheadEnd - overheadStart, comparisonCleaningMethod.getMethodConfiguration(), comparisonCleaningMethod.getMethodName());
                }

                // Set progress indicator to 60%
                updateProgress(0.6);

                // Step 5: Entity Matching
                String entityMatchingMethodName = model.getEntityMatching();

                IEntityMatching em;
                RepresentationModel repModel = MethodMapping.getRepresentationModel(model.getRepresentationModel());
                SimilarityMetric similarityMetric = MethodMapping.getSimilarityMetric(model.getSimilarityMethod());

                if (entityMatchingMethodName.equals(JedaiOptions.GROUP_LINKAGE)) {
                    //todo: threshold should be specified by user...
                    em = new GroupLinkage(0.5, repModel, similarityMetric);
                } else {
                    // Profile Matcher
                    em = new ProfileMatcher(repModel, similarityMetric);
                }
                SimilarityPairs simPairs;

                if (erType.equals(JedaiOptions.DIRTY_ER)) {
                    simPairs = em.executeComparisons(blocks, profilesD1);
                } else {
                    simPairs = em.executeComparisons(blocks, profilesD1, profilesD2);
                }

                // Set progress indicator to 80%
                updateProgress(0.8);

                // Step 6: Entity Clustering
                overheadStart = System.currentTimeMillis();
                IEntityClustering ec = MethodMapping.getEntityClusteringMethod(model.getEntityClustering());
                ec.setSimilarityThreshold(0.1);
                entityClusters = ec.getDuplicates(simPairs);

                // Print clustering performance
                overheadEnd = System.currentTimeMillis();
                ClustersPerformance clp = new ClustersPerformance(entityClusters, duplicatePropagation);
                clp.setStatistics();
                clp.printStatistics(overheadEnd - overheadStart, ec.getMethodName(), ec.getMethodConfiguration());

                // Set gauge values
                f1Gauge.setValue(clp.getFMeasure());
                recallGauge.setValue(clp.getRecall());
                precisionGauge.setValue(clp.getPrecision());

                // Set progress indicator to 100%
                updateProgress(1.0);

                // Get final run values
                double totalTimeSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
                int inputInstances = profilesD1.size();
                int numOfClusters = entityClusters.size();
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

                    // Enable button for result export to CSV
                    exportBtn.setDisable(false);
                });
            } catch (Exception e) {
                // Exception occurred, show alert with information about it
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Exception");
                    alert.setHeaderText("An exception occurred while running the workflow!");
                    alert.setContentText("Details: " + e.toString() + " (" + e.getMessage() + ")");
                    alert.showAndWait();
                });

                // Print stack trace
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Asks the user for a filename with a save file dialog, and saves a CSV with the entity clusters
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
                PrintToFile.toCSV(entityClusters, file.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reset the view of the step
     */
    public void resetData() {
        // Hide time measurements
        numOfInstancesLabel.setVisible(false);
        totalTimeLabel.setVisible(false);
        numOfClustersLabel.setVisible(false);

        // Make export button disabled
        exportBtn.setDisable(true);

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
}
