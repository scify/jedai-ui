package wizard.steps;

import BlockBuilding.IBlockBuilding;
import BlockProcessing.IBlockProcessing;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    public PieChart f1MeasurePie;
    public PieChart recallPie;
    public PieChart precisionPie;
    public Button runBtn;
    public Button exportBtn;
    public VBox containerVBox;
    public Label numOfInstancesLabel;
    public Label numOfClustersLabel;
    private Logger log = LoggerFactory.getLogger(CompletedController.class);

    private List<EquivalenceCluster> entityClusters;

    @Inject
    WizardData model;

    @FXML
    public void initialize() {
    }

    @FXML
    private void runAlgorithm() {
        // Get profiles and ground truth paths from model
        String[] datasetProfiles = {
                model.getEntityProfilesPath()
        };
        String[] datasetGroundtruth = {
                model.getGroundTruthPath()
        };

        for (int datasetId = 0; datasetId < datasetProfiles.length; datasetId++) {
            System.out.println("\n\n\n\n\nCurrent dataset id\t:\t" + datasetId);

            boolean hasGroundTruth = false;
            if (datasetGroundtruth[datasetId] != null && !datasetGroundtruth[datasetId].isEmpty()) {
                hasGroundTruth = true;
            }

            // Step 1: Data reading (complete)
            IEntityReader eReader = new EntitySerializationReader(datasetProfiles[datasetId]);
            List<EntityProfile> profiles = eReader.getEntityProfiles();
            System.out.println("Input Entity Profiles\t:\t" + profiles.size());

            AbstractDuplicatePropagation duplicatePropagation = null;
            if (hasGroundTruth) {
                IGroundTruthReader gtReader = new GtSerializationReader(datasetGroundtruth[datasetId]);
                duplicatePropagation = new UnilateralDuplicatePropagation(gtReader.getDuplicatePairs(eReader.getEntityProfiles()));
                System.out.println("Existing Duplicates\t:\t" + duplicatePropagation.getDuplicates().size());
            }

            // Step 2: Block Building (complete)
            BlockBuildingMethod blockingWorkflow = MethodMapping.blockBuildingMethods.get(model.getBlockBuilding());

            IBlockBuilding blockBuildingMethod = BlockBuildingMethod.getDefaultConfiguration(blockingWorkflow);
            List<AbstractBlock> blocks = blockBuildingMethod.getBlocks(profiles, null);
            System.out.println("Original blocks\t:\t" + blocks.size());

            // Step 3: Block Processing
            IBlockProcessing blockCleaningMethod = BlockBuildingMethod.getDefaultBlockCleaning(blockingWorkflow);
            if (blockCleaningMethod != null) {
                blocks = blockCleaningMethod.refineBlocks(blocks);
            }

            IBlockProcessing comparisonCleaningMethod = BlockBuildingMethod.getDefaultComparisonCleaning(blockingWorkflow);
            if (comparisonCleaningMethod != null) {
                blocks = comparisonCleaningMethod.refineBlocks(blocks);
            }

            if (hasGroundTruth) {
                BlocksPerformance blp = new BlocksPerformance(blocks, duplicatePropagation);
                blp.setStatistics();
                blp.printStatistics();
            }

            // Step 4: Entity Matching
            RepresentationModel repModel = RepresentationModel.CHARACTER_BIGRAMS;
//            for (RepresentationModel repModel : RepresentationModel.values()) {
            System.out.println("\n\nCurrent model\t:\t" + repModel.toString() + "\t\t" + SimilarityMetric.getModelDefaultSimMetric(repModel));
            IEntityMatching em = new ProfileMatcher(repModel, SimilarityMetric.JACCARD_SIMILARITY);
            SimilarityPairs simPairs = em.executeComparisons(blocks, profiles);

            // Step 5: Entity Clustering (complete)
            IEntityClustering ec = MethodMapping.getEntityClusteringMethod(model.getEntityClustering());
            ec.setSimilarityThreshold(0.1);
            entityClusters = ec.getDuplicates(simPairs);

            // Set label values and show them
            numOfClustersLabel.setText("Number of clusters: " + entityClusters.size());
            numOfClustersLabel.setVisible(true);

//            numOfInstancesLabel.setText("Number of instances: " + entityClusters.size());
            numOfInstancesLabel.setVisible(true);

            // Enable button for result export to CSV
            exportBtn.setDisable(false);

            // Print clustering performance
            if (hasGroundTruth) {
                ClustersPerformance clp = new ClustersPerformance(entityClusters, duplicatePropagation);
                clp.setStatistics();
                clp.printStatistics();

                // Get clustering accuracy measures data
                double fMeasure = clp.getFMeasure();
                double recall = clp.getRecall();
                double precision = clp.getPrecision();

                // Create observable arraylists to give data to pies
                ObservableList<PieChart.Data> f1MeasureData =
                        FXCollections.observableArrayList(
                                new PieChart.Data(String.format("F1-measure: %1$.3f", fMeasure), fMeasure),
                                new PieChart.Data("", (1.0 - fMeasure))
                        );

                ObservableList<PieChart.Data> recallData =
                        FXCollections.observableArrayList(
                                new PieChart.Data(String.format("Recall: %1$.3f", recall), recall),
                                new PieChart.Data("", (1.0 - recall))
                        );

                ObservableList<PieChart.Data> precisionData =
                        FXCollections.observableArrayList(
                                new PieChart.Data(String.format("Precision: %1$.3f", precision), precision),
                                new PieChart.Data("", (1.0 - precision))
                        );

                // Give data to pies
                f1MeasurePie.setData(f1MeasureData);
                recallPie.setData(recallData);
                precisionPie.setData(precisionData);

                // Show pies
                f1MeasurePie.setVisible(true);
                recallPie.setVisible(true);
                precisionPie.setVisible(true);
            }
        }
    }

    public void exportBtnHandler() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV File", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
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
