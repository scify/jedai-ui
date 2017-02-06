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
    private Logger log = LoggerFactory.getLogger(CompletedController.class);

    private List<EquivalenceCluster> entityClusters;

    @Inject
    WizardData model;

    @FXML
    public void initialize() {
        // Add fake pie data
        ObservableList<PieChart.Data> pieChartData1 =
                FXCollections.observableArrayList(
                        new PieChart.Data("Grapefruit", 13),
                        new PieChart.Data("Oranges", 25),
                        new PieChart.Data("Plums", 10));

        ObservableList<PieChart.Data> pieChartData2 =
                FXCollections.observableArrayList(
                        new PieChart.Data("Grapefruit", 13),
                        new PieChart.Data("Oranges", 25),
                        new PieChart.Data("Plums", 10),
                        new PieChart.Data("Pears", 22),
                        new PieChart.Data("Apples", 30));

        ObservableList<PieChart.Data> pieChartData3 =
                FXCollections.observableArrayList(
                        new PieChart.Data("Plums", 10),
                        new PieChart.Data("Pears", 22),
                        new PieChart.Data("Apples", 30));

        f1MeasurePie.setData(pieChartData1);
        recallPie.setData(pieChartData2);
        precisionPie.setData(pieChartData3);
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

            // Step 1: Data reading
            IEntityReader eReader = new EntitySerializationReader(datasetProfiles[datasetId]);
            List<EntityProfile> profiles = eReader.getEntityProfiles();
            System.out.println("Input Entity Profiles\t:\t" + profiles.size());

            AbstractDuplicatePropagation duplicatePropagation = null;
            if (hasGroundTruth) {
                IGroundTruthReader gtReader = new GtSerializationReader(datasetGroundtruth[datasetId]);
                duplicatePropagation = new UnilateralDuplicatePropagation(gtReader.getDuplicatePairs(eReader.getEntityProfiles()));
                System.out.println("Existing Duplicates\t:\t" + duplicatePropagation.getDuplicates().size());
            }

            // Step 2: Block Building
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

            // Step 5: Entity Clustering
            IEntityClustering ec = MethodMapping.getEntityClusteringMethod(model.getEntityClustering());
            ec.setSimilarityThreshold(0.1);
            entityClusters = ec.getDuplicates(simPairs);

            // Enable button for result export to CSV
            exportBtn.setDisable(false);

            // Print clustering performance
            if (hasGroundTruth) {
                ClustersPerformance clp = new ClustersPerformance(entityClusters, duplicatePropagation);
                clp.setStatistics();
                clp.printStatistics();
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
