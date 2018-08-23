package org.scify.jedai.gui.utilities;

import javafx.collections.ObservableList;
import org.scify.jedai.blockbuilding.IBlockBuilding;
import org.scify.jedai.blockprocessing.IBlockProcessing;
import org.scify.jedai.datamodel.AbstractBlock;
import org.scify.jedai.datamodel.EntityProfile;
import org.scify.jedai.datamodel.EquivalenceCluster;
import org.scify.jedai.datamodel.SimilarityPairs;
import org.scify.jedai.entityclustering.IEntityClustering;
import org.scify.jedai.entitymatching.IEntityMatching;
import org.scify.jedai.gui.model.BlClMethodConfiguration;
import org.scify.jedai.gui.wizard.MethodMapping;
import org.scify.jedai.gui.wizard.WizardData;
import org.scify.jedai.utilities.BlocksPerformance;
import org.scify.jedai.utilities.ClustersPerformance;
import org.scify.jedai.utilities.datastructures.AbstractDuplicatePropagation;
import org.scify.jedai.utilities.enumerations.BlockBuildingMethod;

import java.util.ArrayList;
import java.util.List;

public class WorkflowManager {
    private final static int NO_OF_TRIALS = 100;
    private final WizardData model;
    private final String erType;

    private EquivalenceCluster[] entityClusters;
    private List<EntityProfile> profilesD1;
    private List<EntityProfile> profilesD2;
    private AbstractDuplicatePropagation duplicatePropagation;

    private IBlockBuilding blockBuildingMethod;
    private List<IBlockProcessing> blClMethods;
    private IBlockProcessing comparisonCleaningMethod;
    private IEntityMatching entityMatchingMethod;
    private IEntityClustering ec;

    public WorkflowManager(WizardData model) {
        this.model = model;
        this.erType = model.getErType();
    }

    public List<EntityProfile> getProfilesD1() {
        return profilesD1;
    }

    public List<EntityProfile> getProfilesD2() {
        return profilesD2;
    }

    public EquivalenceCluster[] getEntityClusters() {
        return entityClusters;
    }

    /**
     * Create instances of the methods that will be used for running the workflow
     */
    public void createMethodInstances() {
        // Get block building method
        BlockBuildingMethod blockingWorkflow = MethodMapping.blockBuildingMethods.get(model.getBlockBuilding());

        // Check if the user set any custom parameters for block building
        if (!model.getBlockBuildingConfigType().equals(JedaiOptions.MANUAL_CONFIG)) {
            // Auto or default configuration selected: use default configuration
            blockBuildingMethod = BlockBuildingMethod.getDefaultConfiguration(blockingWorkflow);
        } else {
            // Manual configuration selected, create method with the saved parameters
            ObservableList<JPair<String, Object>> blBuParams = model.getBlockBuildingParameters();
            blockBuildingMethod = DynamicMethodConfiguration.configureBlockBuildingMethod(blockingWorkflow, blBuParams);
        }

        // Get list of enabled block cleaning method instances
        blClMethods = new ArrayList<>();
        for (BlClMethodConfiguration blClMethodConfig : model.getBlockCleaningMethods()) {
            // Ignore disabled methods
            if (!blClMethodConfig.isEnabled())
                continue;

            // Create instance of this method
            IBlockProcessing blockCleaningMethod;
            if (!blClMethodConfig.getConfigurationType().equals(JedaiOptions.MANUAL_CONFIG)) {
                // Auto or default configuration selected: use default configuration
                blockCleaningMethod = MethodMapping.getMethodByName(blClMethodConfig.getName());
            } else {
                // Manual configuration selected, create method with the saved parameters
                blockCleaningMethod = DynamicMethodConfiguration.configureBlockCleaningMethod(
                        blClMethodConfig.getName(), blClMethodConfig.getManualParameters());
            }

            blClMethods.add(blockCleaningMethod);
        }

        // Get comparison cleaning method
        String coClMethod = model.getComparisonCleaning();
        comparisonCleaningMethod = null;
        if (coClMethod != null && !coClMethod.equals(JedaiOptions.NO_CLEANING)) {
            // Create comparison cleaning method

            if (!model.getComparisonCleaningConfigType().equals(JedaiOptions.MANUAL_CONFIG)) {
                // Auto or default configuration selected: use default configuration
                comparisonCleaningMethod = MethodMapping.getMethodByName(coClMethod);
            } else {
                // Manual configuration selected, create method with the saved parameters
                ObservableList<JPair<String, Object>> coClParams = model.getComparisonCleaningParameters();
                comparisonCleaningMethod = DynamicMethodConfiguration.configureComparisonCleaningMethod(coClMethod, coClParams);
            }
        }

        // Get entity matching method
        String entityMatchingMethodStr = model.getEntityMatching();

        if (!model.getEntityMatchingConfigType().equals(JedaiOptions.MANUAL_CONFIG)) {
            // Default or automatic config, use default values
            entityMatchingMethod = DynamicMethodConfiguration
                    .configureEntityMatchingMethod(entityMatchingMethodStr, null);
        } else {
            // Manual configuration, use given parameters
            ObservableList<JPair<String, Object>> emParams = model.getEntityMatchingParameters();
            entityMatchingMethod = DynamicMethodConfiguration
                    .configureEntityMatchingMethod(entityMatchingMethodStr, emParams);
        }

        // Get entity clustering method
        String entityClusteringMethod = model.getEntityClustering();

        if (!model.getEntityClusteringConfigType().equals(JedaiOptions.MANUAL_CONFIG)) {
            // Auto or default configuration selected: use default configuration
            ec = MethodMapping.getEntityClusteringMethod(entityClusteringMethod);
        } else {
            // Manual configuration selected, create method with the saved parameters
            ObservableList<JPair<String, Object>> ecParams = model.getEntityClusteringParameters();
            ec = DynamicMethodConfiguration.configureEntityClusteringMethod(entityClusteringMethod, ecParams);
        }
    }

    /**
     * When bestIteration is null, set the next random configuraiton for each method in the workflow that should be
     * automatically configured. If it is set, set these methods to that configuration.
     *
     * @param bestIteration Best iteration (optional)
     */
    private void iterateHolisticRandom(Integer bestIteration) {
        // Check if block building parameters should be set automatically
        if (model.getBlockBuildingConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
            if (bestIteration == null) {
                blockBuildingMethod.setNextRandomConfiguration();
            } else {
                blockBuildingMethod.setNumberedRandomConfiguration(bestIteration);
            }
        }

        // Check if any block cleaning method parameters should be set automatically
        if (model.getBlockCleaningMethods() != null && !model.getBlockCleaningMethods().isEmpty()) {
            // Index of the methods in the blClMethods list
            int enabledMethodIndex = 0;

            // Check each block cleaning method config
            for (BlClMethodConfiguration blClConfig : model.getBlockCleaningMethods()) {
                if (blClConfig.isEnabled()) {
                    // Method is enabled, check if we should configure automatically
                    if (blClConfig.getConfigurationType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
                        // Get instance of the method and set next random configuration
                        if (bestIteration == null) {
                            blClMethods.get(enabledMethodIndex).setNextRandomConfiguration();
                        } else {
                            blClMethods.get(enabledMethodIndex).setNumberedRandomConfiguration(bestIteration);
                        }
                    }

                    // Increment index
                    enabledMethodIndex++;
                }
            }
        }

        // Check if comparison cleaning parameters should be set automatically
        if (model.getComparisonCleaningConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
            if (bestIteration == null) {
                comparisonCleaningMethod.setNextRandomConfiguration();
            } else {
                comparisonCleaningMethod.setNumberedRandomConfiguration(bestIteration);
            }
        }

        // Check if entity matching parameters should be set automatically
        if (model.getEntityMatchingConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
            if (bestIteration == null) {
                entityMatchingMethod.setNextRandomConfiguration();
            } else {
                entityMatchingMethod.setNumberedRandomConfiguration(bestIteration);
            }
        }

        // Check if entity clustering parameters should be set automatically
        if (model.getEntityClusteringConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
            if (bestIteration == null) {
                ec.setNextRandomConfiguration();
            } else {
                ec.setNumberedRandomConfiguration(bestIteration);
            }
        }
    }

    /**
     * Execute a full workflow. This includes automatically setting the parameters for any methods that should be
     * automatically configured.
     *
     * @return ClustersPerformance object for the final run of the workflow
     * @throws Exception If runWorkflow returns null...
     */
    public ClustersPerformance executeWorkflow() throws Exception {
        // todo: make difference between this method and runWorkflow() clearer
        if (anyAutomaticConfig()) {
            // Run the rest of the workflow with holistic, or step-by-step
            if (model.getAutoConfigType().equals(JedaiOptions.AUTOCONFIG_HOLISTIC)) {
                // Holistic random configuration (holistic grid is not supported at this time)
                int bestIteration = 0;
                double bestFMeasure = 0;

                for (int j = 0; j < NO_OF_TRIALS; j++) {
                    // Set the next automatic random configuration
                    iterateHolisticRandom(null);

                    // Run a workflow and check its F-measure
                    ClustersPerformance clp = this.runWorkflow(blockBuildingMethod,
                            blClMethods, comparisonCleaningMethod, entityMatchingMethod, ec, false);

                    // Keep this iteration if it has the best F-measure so far
                    double fMeasure = clp.getFMeasure();
                    if (bestFMeasure < fMeasure) {
                        bestIteration = j;
                        bestFMeasure = fMeasure;
                    }
                }

                System.out.println("Best Iteration\t:\t" + bestIteration);
                System.out.println("Best FMeasure\t:\t" + bestFMeasure);

                // Before running the workflow, we should configure the methods using the best iteration's parameters
                iterateHolisticRandom(bestIteration);

                // Run the final workflow (whether there was an automatic configuration or not)
                return this.runWorkflow(blockBuildingMethod, blClMethods, comparisonCleaningMethod,
                        entityMatchingMethod, ec, true);
            } else {
                // Step-by-step automatic configuration. Set random or grid depending on the selected search type.
                return runStepByStepWorkflow(
                        model.getSearchType().equals(JedaiOptions.AUTOCONFIG_RANDOMSEARCH)
                );
            }
        } else {
            // Run workflow without any automatic configuration
            return this.runWorkflow(blockBuildingMethod, blClMethods, comparisonCleaningMethod, entityMatchingMethod,
                    ec, true);
        }
    }

    /**
     * Return true if automatic configuration was chosen for any method
     *
     * @return True if automatic configuration was chosen for any method
     */
    private boolean anyAutomaticConfig() {
        // Check all steps except block cleaning methods
        if (model.getBlockBuildingConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)
                || model.getComparisonCleaningConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)
                || model.getEntityMatchingConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)
                || model.getEntityClusteringConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)
        ) {
            return true;
        }

        // Check block cleaning methods
        if (model.getBlockCleaningMethods() != null && !model.getBlockCleaningMethods().isEmpty()) {
            // Loop over the methods
            for (BlClMethodConfiguration config : model.getBlockCleaningMethods()) {
                // Check if the method is enabled and its config. type is automatic
                if (config.isEnabled() && config.getConfigurationType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Read the datasets
     *
     * @param output Enable/disable details output
     */
    public void readDatasets(boolean output) {
        // Read dataset 1
        profilesD1 = DataReader.getEntitiesD1(model);

        // In case Clean-Clear ER was selected, also read dataset 2
        profilesD2 = null;
        if (erType.equals(JedaiOptions.CLEAN_CLEAN_ER)) {
            profilesD2 = DataReader.getEntitiesD2(model);
        }

        // Read ground truth
        duplicatePropagation = DataReader.getGroundTruth(model, profilesD1, profilesD2);

        // Print details
        if (output) {
            System.out.println("Input Entity Profiles\t:\t" + profilesD1.size());
            System.out.println("Existing Duplicates\t:\t" + duplicatePropagation.getDuplicates().size());
        }
    }

    /**
     * Process blocks using a given block processing method
     *
     * @param duProp        Duplicate propagation (from ground-truth)
     * @param output        Set to true to print clusters performance
     * @param blocks        Blocks to process
     * @param currentMethod Method to process the blocks with
     * @return Processed list of blocks
     */
    private List<AbstractBlock> runBlockProcessing(AbstractDuplicatePropagation duProp, boolean output,
                                                   List<AbstractBlock> blocks, IBlockProcessing currentMethod) {
        double overheadStart;
        double overheadEnd;
        BlocksPerformance blp;
        overheadStart = System.currentTimeMillis();

        blocks = currentMethod.refineBlocks(blocks);

        // Print blocks performance
        overheadEnd = System.currentTimeMillis();
        blp = new BlocksPerformance(blocks, duProp);
        blp.setStatistics();
        if (output)
            blp.printStatistics(overheadEnd - overheadStart, currentMethod.getMethodConfiguration(),
                    currentMethod.getMethodName());
        return blocks;
    }

    /**
     * Run a workflow with the given methods and return its ClustersPerformance
     *
     * @param blBu        Block building method
     * @param blClMethods List of block cleaning methods
     * @param coCl        Comparison cleaning method
     * @param em          Entity matching method
     * @param ec          Entity clustering method
     * @param output      Set to true to print messages while running workflow
     * @return ClustersPerformance object of the executed workflow
     * @throws Exception In case the Entity Matching method is null (shouldn't happen though)
     */
    private ClustersPerformance runWorkflow(IBlockBuilding blBu, List<IBlockProcessing> blClMethods,
                                            IBlockProcessing coCl, IEntityMatching em, IEntityClustering ec,
                                            boolean output) throws Exception {
        // Initialize a few variables
        double overheadStart = System.currentTimeMillis();
        double overheadEnd;
        BlocksPerformance blp;

        // Run step 2
        List<AbstractBlock> blocks;
        if (blBu != null) {
            if (erType.equals(JedaiOptions.DIRTY_ER)) {
                blocks = blBu.getBlocks(profilesD1);
            } else {
                blocks = blBu.getBlocks(profilesD1, profilesD2);
            }
        } else {
            // Show error
            DialogHelper.showError("Block Building Method Error", "Block Building Method is null!",
                    "There was a problem running the selected block building method!");
            return null;
        }

        if (output)
            System.out.println("Original blocks\t:\t" + blocks.size());

        // Print blocks performance
        overheadEnd = System.currentTimeMillis();
        blp = new BlocksPerformance(blocks, duplicatePropagation);
        blp.setStatistics();
        if (output)
            blp.printStatistics(overheadEnd - overheadStart, blBu.getMethodConfiguration(),
                    blBu.getMethodName());

        // Step 3: Block Cleaning
        if (blClMethods != null && !blClMethods.isEmpty()) {
            // Execute the methods
            for (IBlockProcessing currentMethod : blClMethods) {
                blocks = runBlockProcessing(duplicatePropagation, output, blocks, currentMethod);
            }
        }

        // Step 4: Comparison Cleaning
        if (coCl != null) {
            blocks = runBlockProcessing(duplicatePropagation, output, blocks, coCl);
        }

        // Step 5: Entity Matching
        SimilarityPairs simPairs;

        if (em == null)
            throw new Exception("Entity Matching method is null!");

        if (erType.equals(JedaiOptions.DIRTY_ER)) {
            simPairs = em.executeComparisons(blocks, profilesD1);
        } else {
            simPairs = em.executeComparisons(blocks, profilesD1, profilesD2);
        }

        // Step 6: Entity Clustering
        overheadStart = System.currentTimeMillis();

        entityClusters = ec.getDuplicates(simPairs);

        // Print clustering performance
        overheadEnd = System.currentTimeMillis();
        ClustersPerformance clp = new ClustersPerformance(entityClusters, duplicatePropagation);
        clp.setStatistics();
        if (output)
            clp.printStatistics(overheadEnd - overheadStart, ec.getMethodName(),
                    ec.getMethodConfiguration());

        return clp;
    }

    /**
     * Get total comparisons that will be made for a list of blocks
     *
     * @param blocks List of blocks
     * @return Number of comparisons
     */
    private double getTotalComparisons(List<AbstractBlock> blocks) {
        double originalComparisons = 0;
        originalComparisons = blocks.stream()
                .map(AbstractBlock::getNoOfComparisons)
                .reduce(originalComparisons, (accumulator, _item) -> accumulator + _item);
        System.out.println("Original comparisons\t:\t" + originalComparisons);
        return originalComparisons;
    }

    /**
     * Optimize a given block processing method randomly using the given list of blocks.
     * Modifies the original block processing object and sets it to use the best found
     * random configuration.
     *
     * @param bp     Block processing method object
     * @param blocks Blocks to optimize with
     * @param random If true will use random search, otherwise grid
     */
    private void optimizeBlockProcessing(IBlockProcessing bp, List<AbstractBlock> blocks, boolean random) {
        List<AbstractBlock> cleanedBlocks;
        double bestA = 0;
        int bestIteration = 0;
        double originalComparisons = getTotalComparisons(blocks);

        int iterationsNum = random ? NO_OF_TRIALS : bp.getNumberOfGridConfigurations();
        for (int j = 0; j < iterationsNum; j++) {
            if (random) {
                bp.setNextRandomConfiguration();
            } else {
                bp.setNumberedGridConfiguration(j);
            }
            cleanedBlocks = bp.refineBlocks(blocks);
            if (cleanedBlocks.isEmpty()) {
                continue;
            }

            BlocksPerformance blp = new BlocksPerformance(cleanedBlocks, duplicatePropagation);
            blp.setStatistics();
            double recall = blp.getPc();
            double rr = 1 - blp.getAggregateCardinality() / originalComparisons;
            double a = rr * recall;
            if (bestA < a) {
                bestIteration = j;
                bestA = a;
            }
        }
        System.out.println("\n\nBest iteration\t:\t" + bestIteration);
        System.out.println("Best performance\t:\t" + bestA);

        if (random) {
            bp.setNumberedRandomConfiguration(bestIteration);
        } else {
            bp.setNumberedGridConfiguration(bestIteration);
        }
    }

    /**
     * Run a step by step workflow, using random or grid search based on the given parameter.
     *
     * @param random If true, will use random search. Otherwise, grid.
     * @return ClustersPerformance of the workflow result
     */
    private ClustersPerformance runStepByStepWorkflow(boolean random) {
        double bestA = 0;
        int bestIteration = 0;
        double originalComparisons;
        int iterationsNum;

        // Local optimization of Block Building
        if (model.getBlockBuildingConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
            if (erType.equals(JedaiOptions.DIRTY_ER)) {
                originalComparisons = profilesD1.size() * profilesD1.size();
            } else {
                originalComparisons = ((double) profilesD1.size()) * profilesD2.size();
            }

            iterationsNum = random ? NO_OF_TRIALS : blockBuildingMethod.getNumberOfGridConfigurations();

            for (int j = 0; j < iterationsNum; j++) {
                // Set next configuration
                if (random) {
                    blockBuildingMethod.setNextRandomConfiguration();
                } else {
                    blockBuildingMethod.setNumberedGridConfiguration(j);
                }

                // Process the blocks (call appropriate method depending on ER type)
                final List<AbstractBlock> originalBlocks;
                if (erType.equals(JedaiOptions.DIRTY_ER)) {
                    originalBlocks = blockBuildingMethod.getBlocks(profilesD1);
                } else {
                    originalBlocks = blockBuildingMethod.getBlocks(profilesD1, profilesD2);
                }

                if (originalBlocks.isEmpty()) {
                    continue;
                }

                final BlocksPerformance blp = new BlocksPerformance(originalBlocks, duplicatePropagation);
                blp.setStatistics();
                double recall = blp.getPc();
                double rr = 1 - blp.getAggregateCardinality() / originalComparisons;
                double a = rr * recall;
                if (bestA < a) {
                    bestIteration = j;
                    bestA = a;
                }
            }
            System.out.println("\n\nBest iteration\t:\t" + bestIteration);
            System.out.println("Best performance\t:\t" + bestA);

            // Set final block building parameters
            if (random) {
                blockBuildingMethod.setNumberedRandomConfiguration(bestIteration);
            } else {
                blockBuildingMethod.setNumberedGridConfiguration(bestIteration);
            }
        }

        // Process the blocks with block building
        final List<AbstractBlock> blocks;
        if (erType.equals(JedaiOptions.DIRTY_ER)) {
            blocks = blockBuildingMethod.getBlocks(profilesD1);
        } else {
            blocks = blockBuildingMethod.getBlocks(profilesD1, profilesD2);
        }

        BlocksPerformance blp = new BlocksPerformance(blocks, duplicatePropagation);
        blp.setStatistics();
        blp.printStatistics(0, blockBuildingMethod.getMethodConfiguration(),
                blockBuildingMethod.getMethodName());

        // Local optimization of Block Cleaning methods
        List<AbstractBlock> cleanedBlocks = blocks;
        if (model.getBlockCleaningMethods() != null && !model.getBlockCleaningMethods().isEmpty()) {
            // Index of the methods in the blClMethods list
            int enabledMethodIndex = 0;

            // Check each block cleaning method config
            for (BlClMethodConfiguration blClConfig : model.getBlockCleaningMethods()) {
                // Skip disabled methods
                if (!blClConfig.isEnabled())
                    continue;

                // Get instance of the method
                IBlockProcessing bp = blClMethods.get(enabledMethodIndex);

                // Check if we should configure this method automatically
                if (blClConfig.getConfigurationType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
                    // Optimize the method
                    optimizeBlockProcessing(bp, blocks, random);
                }

                // Process blocks with this method
                cleanedBlocks = bp.refineBlocks(blocks);

                blp = new BlocksPerformance(cleanedBlocks, duplicatePropagation);
                blp.setStatistics();
                blp.printStatistics(0, bp.getMethodConfiguration(), bp.getMethodName());

                // Increment index
                enabledMethodIndex++;
            }
        }

        // Local optimization of Comparison Cleaning
        List<AbstractBlock> finalBlocks;
        if (model.getComparisonCleaningConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
            // Optimize the comparison cleaning method
            optimizeBlockProcessing(comparisonCleaningMethod, cleanedBlocks, random);
        }

        finalBlocks = comparisonCleaningMethod.refineBlocks(cleanedBlocks);
        blp = new BlocksPerformance(finalBlocks, duplicatePropagation);
        blp.setStatistics();
        blp.printStatistics(0, comparisonCleaningMethod.getMethodConfiguration(),
                comparisonCleaningMethod.getMethodName());

        // Local optimization of Matching & Clustering
        double time1 = System.currentTimeMillis();
        if (model.getEntityMatchingConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)
                || model.getEntityClusteringConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
            bestIteration = 0;
            double bestFMeasure = 0;
            // todo: handle grid search...
            for (int j = 0; j < NO_OF_TRIALS; j++) {
                // Set entity matching parameters automatically if needed
                if (model.getEntityMatchingConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
                    entityMatchingMethod.setNextRandomConfiguration();
                }
                final SimilarityPairs sims =
                        entityMatchingMethod.executeComparisons(finalBlocks, profilesD1, profilesD2);

                // Set entity clustering parameters automatically if needed
                if (model.getEntityClusteringConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
                    ec.setNextRandomConfiguration();
                }
                final EquivalenceCluster[] clusters = ec.getDuplicates(sims);

                final ClustersPerformance clp = new ClustersPerformance(clusters, duplicatePropagation);
                clp.setStatistics();
                double fMeasure = clp.getFMeasure();
                if (bestFMeasure < fMeasure) {
                    bestIteration = j;
                    bestFMeasure = fMeasure;
                }
            }
            System.out.println("\nBest Iteration\t:\t" + bestIteration);
            System.out.println("Best FMeasure\t:\t" + bestFMeasure);

            time1 = System.currentTimeMillis();

            entityMatchingMethod.setNumberedRandomConfiguration(bestIteration);
            ec.setNumberedRandomConfiguration(bestIteration);
        }

        final SimilarityPairs sims = entityMatchingMethod.executeComparisons(finalBlocks, profilesD1, profilesD2);
        entityClusters = ec.getDuplicates(sims);

        double time2 = System.currentTimeMillis();

        final ClustersPerformance clp = new ClustersPerformance(entityClusters, duplicatePropagation);
        clp.setStatistics();
        // todo: Could set the entire configuration details instead of entity clustering method name & config.
        clp.printStatistics(time2 - time1, ec.getMethodName(), ec.getMethodConfiguration());

        return clp;
    }
}
