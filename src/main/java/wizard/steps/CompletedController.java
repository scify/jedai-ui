package wizard.steps;

import Utilities.Enumerations.BlockBuildingMethod;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wizard.WizardData;

public class CompletedController {
    public PieChart f1MeasurePie;
    public PieChart recallPie;
    public PieChart precisionPie;
    public TreeView treeView;
    private Logger log = LoggerFactory.getLogger(CompletedController.class);

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

        // Run algorithm
        this.runAlgorithm();
    }

    private void runAlgorithm() {
        BlockBuildingMethod blockingWorkflow = BlockBuildingMethod.STANDARD_BLOCKING;
        /*

        String[] datasetProfiles = {
            "/home/ethanos/workspace/JedAIToolkitNew/datasets/restaurantProfiles",
//            "E:\\Data\\csvProfiles\\censusProfiles",
//            "E:\\Data\\csvProfiles\\coraProfiles",
//            "E:\\Data\\csvProfiles\\cddbProfiles",
//            "E:\\Data\\csvProfiles\\abt-buy\\dataset",
//            "E:\\Data\\csvProfiles\\amazon-gp\\dataset",
//            "E:\\Data\\csvProfiles\\dblp-acm\\dataset",
//            "E:\\Data\\csvProfiles\\dblp-scholar\\dataset",
//            "E:\\Data\\csvProfiles\\movies\\dataset"
        };
        String[] datasetGroundtruth = {
            "/home/ethanos/workspace/JedAIToolkitNew/datasets/restaurantIdDuplicates",
//            "E:\\Data\\csvProfiles\\censusIdDuplicates",
//            "E:\\Data\\csvProfiles\\coraIdDuplicates",
//            "E:\\Data\\csvProfiles\\cddbIdDuplicates",
//            "E:\\Data\\csvProfiles\\abt-buy\\groundtruth",
//            "E:\\Data\\csvProfiles\\amazon-gp\\groundtruth",
//            "E:\\Data\\csvProfiles\\dblp-acm\\groundtruth",
//            "E:\\Data\\csvProfiles\\dblp-scholar\\groundtruth",
//            "E:\\Data\\csvProfiles\\movies\\groundtruth"
        };

        for (int datasetId = 0; datasetId < datasetProfiles.length; datasetId++) {
            System.out.println("\n\n\n\n\nCurrent dataset id\t:\t" + datasetId);;

            IEntityReader eReader = new EntitySerializationReader(datasetProfiles[datasetId]);
            List<EntityProfile> profiles = eReader.getEntityProfiles();
            System.out.println("Input Entity Profiles\t:\t" + profiles.size());

            IGroundTruthReader gtReader = new GtSerializationReader(datasetGroundtruth[datasetId]);
            final AbstractDuplicatePropagation duplicatePropagation = new UnilateralDuplicatePropagation(gtReader.getDuplicatePairs(eReader.getEntityProfiles()));
            System.out.println("Existing Duplicates\t:\t" + duplicatePropagation.getDuplicates().size());

            IBlockBuilding blockBuildingMethod = BlockBuildingMethod.getDefaultConfiguration(blockingWorkflow);
            List<AbstractBlock> blocks = blockBuildingMethod.getBlocks(profiles, null);
            System.out.println("Original blocks\t:\t" + blocks.size());

            IBlockProcessing blockCleaningMethod = BlockBuildingMethod.getDefaultBlockCleaning(blockingWorkflow);
            if (blockCleaningMethod != null) {
                blocks = blockCleaningMethod.refineBlocks(blocks);
            }

            IBlockProcessing comparisonCleaningMethod = BlockBuildingMethod.getDefaultComparisonCleaning(blockingWorkflow);
            if (comparisonCleaningMethod != null) {
                blocks = comparisonCleaningMethod.refineBlocks(blocks);
            }

            BlocksPerformance blp = new BlocksPerformance(blocks, duplicatePropagation);
            blp.setStatistics();
            blp.printStatistics();

            RepresentationModel repModel = RepresentationModel.CHARACTER_BIGRAMS;
//            for (RepresentationModel repModel : RepresentationModel.values()) {
                System.out.println("\n\nCurrent model\t:\t" + repModel.toString() + "\t\t" +  SimilarityMetric.getModelDefaultSimMetric(repModel));
                IEntityMatching em = new ProfileMatcher(repModel, SimilarityMetric.JACCARD_SIMILARITY);
                SimilarityPairs simPairs = em.executeComparisons(blocks, profiles);

                IEntityClustering ec = new RicochetSRClustering();
                ec.setSimilarityThreshold(0.1);
                List<EquivalenceCluster> entityClusters = ec.getDuplicates(simPairs);

                try {
					PrintToFile.toCSV(entityClusters, "/home/ethanos/workspace/JedAIToolkitNew/rest.csv");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

                ClustersPerformance clp = new ClustersPerformance(entityClusters, duplicatePropagation);
                clp.setStatistics();
                clp.printStatistics();
            }
//        }
         */
    }
}
