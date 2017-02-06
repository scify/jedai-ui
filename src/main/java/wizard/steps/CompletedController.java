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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wizard.MethodMapping;
import wizard.WizardData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class CompletedController {
    public Button runBtn;
    public Button exportBtn;
    public VBox containerVBox;
    public Label numOfInstancesLabel;
    public Label numOfClustersLabel;
    public HBox gaugesHBox;
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
    }

    private Gauge newGauge(String title) {
        return GaugeBuilder.create()
                .skinType(SkinType.HORIZONTAL)
                .minValue(0.0)
                .maxValue(1.0)
                .tickLabelDecimals(1)
                .title(title)
                .decimals(3)
                .build();
    }

    @FXML
    private void runAlgorithm() {
        // Get profiles and ground truth paths from model
        String datasetProfiles = model.getEntityProfilesPath();
        String datasetGroundTruth = model.getGroundTruthPath();

        boolean hasGroundTruth = false;
        if (datasetGroundTruth != null && !datasetGroundTruth.isEmpty()) {
            hasGroundTruth = true;
        }

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

        // Step 2: Block Building
        BlockBuildingMethod blockingWorkflow = MethodMapping.blockBuildingMethods.get(model.getBlockBuilding());

        IBlockBuilding blockBuildingMethod = BlockBuildingMethod.getDefaultConfiguration(blockingWorkflow);
        List<AbstractBlock> blocks = blockBuildingMethod.getBlocks(profiles, null);
        System.out.println("Original blocks\t:\t" + blocks.size());

        // Step 3: Block Processing
        String processingType = model.getBlockProcessingType();
        if (!processingType.equals("No block processing")) {
            List<String> processingMethods = model.getBlockProcessingMethods();

            for (String currentMethod : processingMethods) {
                blocks = MethodMapping.processBlocks(blocks, processingType, currentMethod);
            }
        }

        if (hasGroundTruth) {
            BlocksPerformance blp = new BlocksPerformance(blocks, duplicatePropagation);
            blp.setStatistics();
            blp.printStatistics();
        }

        // Step 4: Entity Matching
        //todo
        RepresentationModel repModel = RepresentationModel.CHARACTER_BIGRAMS;
        System.out.println("\n\nCurrent model\t:\t" + repModel.toString() + "\t\t" + SimilarityMetric.getModelDefaultSimMetric(repModel));
        IEntityMatching em = new ProfileMatcher(repModel, SimilarityMetric.JACCARD_SIMILARITY);
        SimilarityPairs simPairs = em.executeComparisons(blocks, profiles);

        // Step 5: Entity Clustering
        IEntityClustering ec = MethodMapping.getEntityClusteringMethod(model.getEntityClustering());
        ec.setSimilarityThreshold(0.1);
        entityClusters = ec.getDuplicates(simPairs);

        // Set label values and show them
        numOfClustersLabel.setText("Number of clusters: " + entityClusters.size());
        numOfClustersLabel.setVisible(true);

        numOfInstancesLabel.setText("Number of input instances: " + profiles.size());
        numOfInstancesLabel.setVisible(true);

        // Enable button for result export to CSV
        exportBtn.setDisable(false);

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
