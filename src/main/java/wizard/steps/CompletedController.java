package wizard.steps;

import BlockBuilding.IBlockBuilding;
import DataModel.AbstractBlock;
import DataModel.EntityProfile;
import DataModel.EquivalenceCluster;
import DataModel.SimilarityPairs;
import DataReader.EntityReader.EntitySerializationReader;
import DataReader.EntityReader.IEntityReader;
import DataReader.GroundTruthReader.GtSerializationReader;
import DataReader.GroundTruthReader.IGroundTruthReader;
import EntityClustering.IEntityClustering;
import EntityMatching.GroupLinkage;
import EntityMatching.IEntityMatching;
import EntityMatching.ProfileMatcher;
import Utilities.BlocksPerformance;
import Utilities.ClustersPerformance;
import Utilities.DataStructures.AbstractDuplicatePropagation;
import Utilities.DataStructures.UnilateralDuplicatePropagation;
import Utilities.Enumerations.BlockBuildingMethod;
import Utilities.Enumerations.RepresentationModel;
import Utilities.Enumerations.SimilarityMetric;
import Utilities.PrintToFile;
import com.google.inject.Inject;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.SkinType;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConsoleArea;
import utils.MultiOutputStream;
import wizard.MethodMapping;
import wizard.WizardData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

public class CompletedController {
    public Button runBtn;
    public Button exportBtn;
    public VBox containerVBox;
    public Label numOfInstancesLabel;
    public Label numOfClustersLabel;
    public HBox gaugesHBox;
    public ProgressIndicator progressIndicator;
    public TextArea logTextArea;
    private Logger log = LoggerFactory.getLogger(CompletedController.class);

    private Gauge f1Gauge;
    private Gauge recallGauge;
    private Gauge precisionGauge;

    private List<EquivalenceCluster> entityClusters;

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Create gauges
        f1Gauge = newGauge("F1-measure");
        gaugesHBox.getChildren().add(f1Gauge);

        recallGauge = newGauge("Recall");
        gaugesHBox.getChildren().add(recallGauge);

        precisionGauge = newGauge("Precision");
        gaugesHBox.getChildren().add(precisionGauge);

        // Setup text area as log
        ConsoleArea ca = new ConsoleArea(logTextArea);
        PrintStream ps = new PrintStream(ca, true);

        MultiOutputStream multiOut = new MultiOutputStream(System.out, ps);
        MultiOutputStream multiErr = new MultiOutputStream(System.err, ps);

        PrintStream out = new PrintStream(multiOut);
        PrintStream err = new PrintStream(multiErr);

        System.setOut(out);
        System.setErr(err);
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
            try {
                // Get profiles and ground truth paths from model
                String datasetProfiles = model.getEntityProfilesPath();
                String datasetGroundTruth = model.getGroundTruthPath();

                boolean hasGroundTruth = (datasetGroundTruth != null && !datasetGroundTruth.isEmpty());

                // Step 1: Data reading
                IEntityReader eReader = new EntitySerializationReader(datasetProfiles);
                List<EntityProfile> profiles = eReader.getEntityProfiles();
                System.out.println("Input Entity Profiles\t:\t" + profiles.size());

                AbstractDuplicatePropagation duplicatePropagation = null;
                if (hasGroundTruth) {
                    IGroundTruthReader gtReader = new GtSerializationReader(datasetGroundTruth);
                    duplicatePropagation = new UnilateralDuplicatePropagation(gtReader.getDuplicatePairs(eReader.getEntityProfiles()));
                    System.out.println("Existing Duplicates\t:\t" + duplicatePropagation.getDuplicates().size());
                }

                // Set progress indicator to 20%
                updateProgress(0.2);

                // Step 2: Block Building
                BlockBuildingMethod blockingWorkflow = MethodMapping.blockBuildingMethods.get(model.getBlockBuilding());

                IBlockBuilding blockBuildingMethod = BlockBuildingMethod.getDefaultConfiguration(blockingWorkflow);
                List<AbstractBlock> blocks = blockBuildingMethod.getBlocks(profiles, null);
                System.out.println("Original blocks\t:\t" + blocks.size());

                // Set progress indicator to 40%
                updateProgress(0.4);

                // Step 3: Block Processing
                List<String> processingMethods = model.getBlockProcessingMethods();
                for (String currentMethod : processingMethods) {
                    // Process blocks with this method
                    blocks = MethodMapping.processBlocks(blocks, currentMethod);
                }

                // Step 4: Comparison Refinement method
                String compRefMethod = model.getComparisonRefinementMethod();
                if (compRefMethod != null && !compRefMethod.equals("No refinement")) {
                    blocks = MethodMapping.processBlocks(blocks, compRefMethod);
                }

                if (hasGroundTruth) {
                    BlocksPerformance blp = new BlocksPerformance(blocks, duplicatePropagation);
                    blp.setStatistics();
                    blp.printStatistics();
                }

                // Set progress indicator to 60%
                updateProgress(0.6);

                // Step 5: Entity Matching
                String entityMatchingMethodName = model.getEntityMatching();

                IEntityMatching em;
                RepresentationModel repModel = RepresentationModel.TOKEN_UNIGRAMS;
                if (entityMatchingMethodName.equals("Group Linkage")) {
                    em = new GroupLinkage(repModel, SimilarityMetric.getModelDefaultSimMetric(repModel));
                } else {
                    // Profile Matcher
                    em = new ProfileMatcher(repModel, SimilarityMetric.getModelDefaultSimMetric(repModel));
                }
                SimilarityPairs simPairs = em.executeComparisons(blocks, profiles);

                // Set progress indicator to 80%
                updateProgress(0.8);

                // Step 6: Entity Clustering
                IEntityClustering ec = MethodMapping.getEntityClusteringMethod(model.getEntityClustering());
                ec.setSimilarityThreshold(0.1);
                entityClusters = ec.getDuplicates(simPairs);

                // Print clustering performance
                if (hasGroundTruth) {
                    ClustersPerformance clp = new ClustersPerformance(entityClusters, duplicatePropagation);
                    clp.setStatistics();
                    clp.printStatistics();

                    // Set gauge values
                    f1Gauge.setValue(clp.getFMeasure());
                    recallGauge.setValue(clp.getRecall());
                    precisionGauge.setValue(clp.getPrecision());
                }

                // Set progress indicator to 100%
                updateProgress(1.0);

                // Update labels and JavaFX UI components from UI thread
                Platform.runLater(() -> {
                    // Set label values and show them
                    numOfClustersLabel.setText("Number of clusters: " + entityClusters.size());
                    numOfClustersLabel.setVisible(true);

                    numOfInstancesLabel.setText("Number of input instances: " + profiles.size());
                    numOfInstancesLabel.setVisible(true);

                    // Enable button for result export to CSV
                    exportBtn.setDisable(false);
                });
            } catch (Exception e) {
                // Exception occurred, show alert with information about it
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Exception");
                    alert.setHeaderText("An exception occured while running the workflow!");
                    alert.setContentText("Details: " + e.toString() + " (" + e.getMessage() + ")");
                    alert.showAndWait();
                });
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
}
