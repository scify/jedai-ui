package org.scify.jedai.gui.utilities;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import org.scify.jedai.blockbuilding.IBlockBuilding;
import org.scify.jedai.blockprocessing.IBlockProcessing;
import org.scify.jedai.datamodel.*;
import org.scify.jedai.entityclustering.IEntityClustering;
import org.scify.jedai.entitymatching.IEntityMatching;
import org.scify.jedai.gui.model.JedaiMethodConfiguration;
import org.scify.jedai.gui.model.WorkflowResult;
import org.scify.jedai.gui.wizard.MethodMapping;
import org.scify.jedai.gui.wizard.WizardData;
import org.scify.jedai.schemaclustering.ISchemaClustering;
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
    private final List<WorkflowResult> performancePerStep;

    private EquivalenceCluster[] entityClusters;
    private List<EntityProfile> profilesD1;
    private List<EntityProfile> profilesD2;
    private AbstractDuplicatePropagation duplicatePropagation;

    private ISchemaClustering schemaClusteringMethod;
    private List<IBlockBuilding> blBuMethods;
    private List<IBlockProcessing> blClMethods;
    private IBlockProcessing comparisonCleaningMethod;
    private IEntityMatching entityMatchingMethod;
    private IEntityClustering ec;

    public WorkflowManager(WizardData model) {
        this.model = model;
        this.erType = model.getErType();

        // Initialize performance per step list
        this.performancePerStep = new ArrayList<>();
    }

    public List<WorkflowResult> getPerformancePerStep() {
        return performancePerStep;
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

    public AbstractDuplicatePropagation getDuplicatePropagation() {
        return duplicatePropagation;
    }

    /**
     * Create instances of the methods that will be used for running the workflow
     *
     * @param isCleanCleanEr If we are using clean-clean ER or not. Some block cleaning methods require this.
     */
    public void createMethodInstances(boolean isCleanCleanEr) {
        // Get schema clustering method (will become null if no schema clustering method was selected)
        if (!model.getSchemaClusteringConfigType().equals(JedaiOptions.MANUAL_CONFIG)) {
            // Default (or automatic, later) configuration of schema clustering
            schemaClusteringMethod = MethodMapping.getSchemaClusteringMethodByName(model.getSchemaClustering());
        } else {
            // Manual configuration of schema clustering
            schemaClusteringMethod = DynamicMethodConfiguration.configureSchemaClusteringMethod(
                    model.getSchemaClustering(),
                    model.getSchemaClusteringParameters()
            );
        }

        // Get list of enabled block cleaning method instances
        blBuMethods = new ArrayList<>();
        for (JedaiMethodConfiguration methodConfig : model.getBlockBuildingMethods()) {
            // Ignore disabled methods
            if (!methodConfig.isEnabled())
                continue;

            // Create instance of this method
            BlockBuildingMethod blockingWorkflow = MethodMapping.blockBuildingMethods.get(methodConfig.getName());

            // Check if the user set any custom parameters for block building
            IBlockBuilding blockBuildingMethod;
            if (!methodConfig.getConfigurationType().equals(JedaiOptions.MANUAL_CONFIG)) {
                // Auto or default configuration selected: use default configuration
                blockBuildingMethod = BlockBuildingMethod.getDefaultConfiguration(blockingWorkflow);
            } else {
                // Manual configuration selected, create method with the saved parameters
                ObservableList<JPair<String, Object>> blBuParams = methodConfig.getManualParameters();
                blockBuildingMethod = DynamicMethodConfiguration.configureBlockBuildingMethod(blockingWorkflow, blBuParams);
            }

            blBuMethods.add(blockBuildingMethod);
        }

        // Get list of enabled block cleaning method instances
        blClMethods = new ArrayList<>();
        for (JedaiMethodConfiguration blClMethodConfig : model.getBlockCleaningMethods()) {
            // Ignore disabled methods
            if (!blClMethodConfig.isEnabled())
                continue;

            // Create instance of this method
            IBlockProcessing blockCleaningMethod;
            if (!blClMethodConfig.getConfigurationType().equals(JedaiOptions.MANUAL_CONFIG)) {
                // Auto or default configuration selected: use default configuration
                blockCleaningMethod = MethodMapping.getMethodByName(blClMethodConfig.getName(), isCleanCleanEr);
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
                comparisonCleaningMethod = MethodMapping.getMethodByName(coClMethod, isCleanCleanEr);
            } else {
                // Manual configuration selected, create method with the saved parameters
                ObservableList<JPair<String, Object>> coClParams = model.getComparisonCleaningParameters();
                comparisonCleaningMethod = DynamicMethodConfiguration.configureComparisonCleaningMethod(coClMethod, coClParams);
            }
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
     * Get an instance of the currently selected entity matching method, configured as required by the selected options
     * in the Wizard.
     *
     * @param profilesD1 Entity profiles for 1st dataset
     * @param profilesD2 Entity profiles for 2nd dataset (used for Clean-Clean ER, can be null)
     * @return Configured entity matching method
     */
    private IEntityMatching getEntityMatchingMethodInstance(List<EntityProfile> profilesD1,
                                                            List<EntityProfile> profilesD2) {
        // Get entity matching method
        String entityMatchingMethodStr = model.getEntityMatching();

        if (!model.getEntityMatchingConfigType().equals(JedaiOptions.MANUAL_CONFIG)) {
            // Default or automatic config, use default values
            return DynamicMethodConfiguration
                    .configureEntityMatchingMethod(entityMatchingMethodStr, profilesD1, profilesD2, null);
        } else {
            // Manual configuration, use given parameters
            ObservableList<JPair<String, Object>> emParams = model.getEntityMatchingParameters();

            return DynamicMethodConfiguration
                    .configureEntityMatchingMethod(entityMatchingMethodStr, profilesD1, profilesD2, emParams);
        }
    }

    /**
     * When bestIteration is null, set the next random configuraiton for each method in the workflow that should be
     * automatically configured. If it is set, set these methods to that configuration.
     *
     * @param bestIteration Best iteration (optional)
     */
    private void iterateHolisticRandom(Integer bestIteration) {
        // Check if schema clustering parameters should be set automatically
        if (model.getSchemaClusteringConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
            if (bestIteration == null) {
                schemaClusteringMethod.setNextRandomConfiguration();
            } else {
                schemaClusteringMethod.setNumberedRandomConfiguration(bestIteration);
            }
        }

        // Check if any block building method parameters should be set automatically
        if (model.getBlockBuildingMethods() != null && !model.getBlockBuildingMethods().isEmpty()) {
            // Index of the methods in the blClMethods list
            int enabledMethodIndex = 0;

            // Check each block cleaning method config
            for (JedaiMethodConfiguration methodConfig : model.getBlockBuildingMethods()) {
                if (methodConfig.isEnabled()) {
                    // Method is enabled, check if we should configure automatically
                    if (methodConfig.getConfigurationType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
                        // Get instance of the method and set next random configuration
                        if (bestIteration == null) {
                            blBuMethods.get(enabledMethodIndex).setNextRandomConfiguration();
                        } else {
                            blBuMethods.get(enabledMethodIndex).setNumberedRandomConfiguration(bestIteration);
                        }
                    }

                    // Increment index
                    enabledMethodIndex++;
                }
            }
        }

        // Check if any block cleaning method parameters should be set automatically
        if (model.getBlockCleaningMethods() != null && !model.getBlockCleaningMethods().isEmpty()) {
            // Index of the methods in the blClMethods list
            int enabledMethodIndex = 0;

            // Check each block cleaning method config
            for (JedaiMethodConfiguration blClConfig : model.getBlockCleaningMethods()) {
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
     * Add a blocks performance result to the performance per step list
     *
     * @param name Name of step
     * @param time Time it took to run the step (in milliseconds)
     * @param blp  BlocksPerformance object (to get values)
     */
    private void addBlocksPerformance(String name, double time, BlocksPerformance blp) {
        performancePerStep.add(
                new WorkflowResult(name, blp.getPc(), blp.getPq(), blp.getFMeasure(), time / 1000.0, -1, -1, -1)
        );
    }

    /**
     * Execute a full workflow. This includes automatically setting the parameters for any methods that should be
     * automatically configured.
     *
     * @return ClustersPerformance object for the final run of the workflow
     * @throws Exception If runWorkflow returns null...
     */
    public ClustersPerformance executeWorkflow(Label statusLabel) throws Exception {
        // todo: make difference between this method and runWorkflow() clearer
        if (anyAutomaticConfig()) {
            // Run the rest of the workflow with holistic, or step-by-step
            if (model.getAutoConfigType().equals(JedaiOptions.AUTOCONFIG_HOLISTIC)) {
                // Holistic random configuration (holistic grid is not supported at this time)
                int bestIteration = 0;
                double bestFMeasure = 0;

                for (int j = 0; j < NO_OF_TRIALS; j++) {
                    int finalJ = j;
                    Platform.runLater(() -> statusLabel.setText("Auto-configuration " + finalJ + "/" + NO_OF_TRIALS));

                    // Set the next automatic random configuration
                    iterateHolisticRandom(null);

                    // Run a workflow and check its F-measure
                    ClustersPerformance clp = this.runWorkflow(statusLabel, schemaClusteringMethod, blBuMethods,
                            blClMethods, comparisonCleaningMethod, ec, false);

                    // If there was a problem with this random workflow, skip this iteration
                    if (clp == null) {
                        continue;
                    }

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
                return this.runWorkflow(statusLabel, schemaClusteringMethod, blBuMethods, blClMethods,
                        comparisonCleaningMethod, ec, true);
            } else {
                // Step-by-step automatic configuration. Set random or grid depending on the selected search type.
                return runStepByStepWorkflow(
                        statusLabel, model.getSearchType().equals(JedaiOptions.AUTOCONFIG_RANDOMSEARCH)
                );
            }
        } else {
            // Run workflow without any automatic configuration
            return this.runWorkflow(statusLabel, schemaClusteringMethod, blBuMethods, blClMethods,
                    comparisonCleaningMethod, ec, true);
        }
    }

    /**
     * Return true if automatic configuration was chosen for any method
     *
     * @return True if automatic configuration was chosen for any method
     */
    private boolean anyAutomaticConfig() {
        // Check all steps except block cleaning methods
        if (model.getComparisonCleaningConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)
                || model.getEntityMatchingConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)
                || model.getEntityClusteringConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)
        ) {
            return true;
        }

        // Create list of all block building and cleaning method configurations
        List<JedaiMethodConfiguration> allConfigs = new ArrayList<>();

        // Add block building method configurations
        if (model.getBlockBuildingMethods() != null && !model.getBlockBuildingMethods().isEmpty()) {
            allConfigs.addAll(model.getBlockBuildingMethods());
        }

        // Add block cleaning method configurations
        if (model.getBlockCleaningMethods() != null && !model.getBlockCleaningMethods().isEmpty()) {
            allConfigs.addAll(model.getBlockCleaningMethods());
        }

        // Loop over the method configurations
        for (JedaiMethodConfiguration config : allConfigs) {
            // Check if the method is enabled and its config. type is automatic
            if (config.isEnabled() && config.getConfigurationType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
                return true;
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
     * @param finalRun      Set to true to print clusters performance
     * @param blocks        Blocks to process
     * @param currentMethod Method to process the blocks with
     * @return Processed list of blocks
     */
    private List<AbstractBlock> runBlockProcessing(AbstractDuplicatePropagation duProp, boolean finalRun,
                                                   List<AbstractBlock> blocks, IBlockProcessing currentMethod) {
        double overheadStart;
        double overheadEnd;
        BlocksPerformance blp;
        overheadStart = System.currentTimeMillis();

        if (!blocks.isEmpty()) {
            blocks = currentMethod.refineBlocks(blocks);
            overheadEnd = System.currentTimeMillis();

            if (finalRun) {
                // Print blocks performance
                blp = new BlocksPerformance(blocks, duProp);
                blp.setStatistics();

                double totalTime = overheadEnd - overheadStart;
                blp.printStatistics(totalTime, currentMethod.getMethodConfiguration(),
                        currentMethod.getMethodName());

                // Save the performance of block processing
                this.addBlocksPerformance(currentMethod.getMethodName(), totalTime, blp);
            }
        }

        return blocks;
    }

    /**
     * Run a block building method and return its blocks
     *
     * @param erType     Entity Resolution type
     * @param clusters   Clusters from schema clustering, if applicable (can be null)
     * @param profilesD1 List of profiles from the 1st dataset
     * @param profilesD2 List of profiles from the 2nd dataset
     * @param bb         Block building method instance to get blocks with
     * @return List of blocks generated by block building method
     */
    private List<AbstractBlock> runBlockBuilding(String erType, AttributeClusters[] clusters,
                                                 List<EntityProfile> profilesD1, List<EntityProfile> profilesD2,
                                                 IBlockBuilding bb) {
        if (erType.equals(JedaiOptions.DIRTY_ER)) {
            if (clusters == null) {
                // Dirty ER without schema clustering
                return bb.getBlocks(profilesD1);
            } else {
                // Dirty ER with schema clustering
                return bb.getBlocks(profilesD1, null, clusters);
            }
        } else {
            if (clusters == null) {
                // Clean-clean ER without schema clustering
                return bb.getBlocks(profilesD1, profilesD2);
            } else {
                // Clean-clean ER with schema clustering
                return bb.getBlocks(profilesD1, profilesD2, clusters);
            }
        }
    }

    /**
     * Run a workflow with the given methods and return its ClustersPerformance
     *
     * @param statusLabel Label to set status on
     * @param sc          Schema clustering method
     * @param blBuMethods List of block building methods
     * @param blClMethods List of block cleaning methods
     * @param coCl        Comparison cleaning method
     * @param ec          Entity clustering method
     * @param finalRun    Set to true to print messages while running workflow & save performance of each step
     * @return ClustersPerformance object of the executed workflow
     * @throws Exception In case the Entity Matching method is null (shouldn't happen though)
     */
    private ClustersPerformance runWorkflow(Label statusLabel, ISchemaClustering sc, List<IBlockBuilding> blBuMethods,
                                            List<IBlockProcessing> blClMethods, IBlockProcessing coCl,
                                            IEntityClustering ec, boolean finalRun)
            throws Exception {
        // Run schema clustering if it's not null (can't measure its performance)
        if (finalRun)
            Platform.runLater(() -> statusLabel.setText("Running schema clustering..."));

        boolean isDirtyEr = erType.equals(JedaiOptions.DIRTY_ER);

        AttributeClusters[] clusters = null;
        if (sc != null) {
            // Run schema clustering
            if (isDirtyEr) {
                clusters = sc.getClusters(profilesD1);
            } else {
                clusters = sc.getClusters(profilesD1, profilesD2);
            }
        }

        // Initialize a few variables
        double overheadStart;
        double overheadEnd;
        BlocksPerformance blp;

        // Run block building methods
        if (finalRun)
            Platform.runLater(() -> statusLabel.setText("Running block building..."));

        List<AbstractBlock> blocks = new ArrayList<>();
        for (IBlockBuilding bb : blBuMethods) {
            // Start time measurement
            overheadStart = System.currentTimeMillis();

            // Run the method
            blocks.addAll(this.runBlockBuilding(erType, clusters, profilesD1, profilesD2, bb));

            // Get blocks performance to print
            overheadEnd = System.currentTimeMillis();
            blp = new BlocksPerformance(blocks, duplicatePropagation);
            blp.setStatistics();
            if (finalRun) {
                double totalTime = overheadEnd - overheadStart;

                // Print performance
                blp.printStatistics(totalTime, bb.getMethodConfiguration(), bb.getMethodName());

                // Save the performance of block building
                this.addBlocksPerformance(bb.getMethodName(), totalTime, blp);
            }
        }

        if (finalRun)
            System.out.println("Original blocks\t:\t" + blocks.size());

        // Run Block Cleaning
        if (finalRun)
            Platform.runLater(() -> statusLabel.setText("Running block cleaning..."));

        if (blClMethods != null && !blClMethods.isEmpty()) {
            // Execute the methods
            for (IBlockProcessing currentMethod : blClMethods) {
                blocks = runBlockProcessing(duplicatePropagation, finalRun, blocks, currentMethod);

                if (blocks.isEmpty()) {
                    return null;
                }
            }
        }

        // Run Comparison Cleaning
        if (finalRun)
            Platform.runLater(() -> statusLabel.setText("Running comparison cleaning..."));
        if (coCl != null) {
            blocks = runBlockProcessing(duplicatePropagation, finalRun, blocks, coCl);

            if (blocks.isEmpty()) {
                return null;
            }
        }

        // Run Entity Matching
        if (finalRun)
            Platform.runLater(() -> statusLabel.setText("Running entity matching..."));
        SimilarityPairs simPairs;

        // Create the entity matching method here because it requires the entity profiles
        IEntityMatching entityMatching = getEntityMatchingMethodInstance(profilesD1, profilesD2);
        if (entityMatching == null)
            throw new Exception("Entity Matching method is null!");

        simPairs = entityMatching.executeComparisons(blocks);

        // Run Entity Clustering
        if (finalRun)
            Platform.runLater(() -> statusLabel.setText("Running entity clustering..."));
        overheadStart = System.currentTimeMillis();

        entityClusters = ec.getDuplicates(simPairs);

        // Print clustering performance
        overheadEnd = System.currentTimeMillis();
        ClustersPerformance clp = new ClustersPerformance(entityClusters, duplicatePropagation);
        clp.setStatistics();
        if (finalRun)
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
     * @param statusLabel Label to show status
     * @param random      If true, will use random search. Otherwise, grid.
     * @return ClustersPerformance of the workflow result
     */
    private ClustersPerformance runStepByStepWorkflow(Label statusLabel, boolean random) {
        double bestA = 0;
        int bestIteration = 0;

        double originalComparisons;
        int iterationsNum;
        double time1, time2, totalTimeMillis;
        BlocksPerformance blp;

        // Schema Clustering local optimization
        AttributeClusters[] scClusters = null;
        if (!model.getSchemaClustering().equals(JedaiOptions.NO_SCHEMA_CLUSTERING)) {
            Platform.runLater(() -> statusLabel.setText("Schema Clustering optimization..."));

            // Optimize schema clustering
//          if (model.getSchemaClusteringConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) { }
            // todo: optimize together with block building (and enable the option in the GUI)

            // Execute schema clustering method
            ISchemaClustering sc = MethodMapping.getSchemaClusteringMethodByName(model.getSchemaClustering());

            if (erType.equals(JedaiOptions.DIRTY_ER)) {
                scClusters = sc.getClusters(profilesD1);
            } else {
                scClusters = sc.getClusters(profilesD1, profilesD2);
            }
        }

        // Block Building local optimization
        Platform.runLater(() -> statusLabel.setText("Block Building optimization..."));
        time1 = System.currentTimeMillis();
        final List<AbstractBlock> blocks = new ArrayList<>();

        if (model.getBlockBuildingMethods() != null && !model.getBlockBuildingMethods().isEmpty()) {
            // Index of the methods in the blBuMethods list
            int enabledMethodIndex = 0;

            for (JedaiMethodConfiguration mc : model.getBlockBuildingMethods()) {
                // Skip disabled methods
                if (!mc.isEnabled())
                    continue;

                // Get the block building method instance
                IBlockBuilding bb = blBuMethods.get(enabledMethodIndex);

                if (mc.getConfigurationType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
                    if (erType.equals(JedaiOptions.DIRTY_ER)) {
                        originalComparisons = profilesD1.size() * profilesD1.size();
                    } else {
                        originalComparisons = ((double) profilesD1.size()) * profilesD2.size();
                    }

                    iterationsNum = random ? NO_OF_TRIALS : bb.getNumberOfGridConfigurations();

                    for (int j = 0; j < iterationsNum; j++) {
                        // Set next configuration
                        if (random) {
                            bb.setNextRandomConfiguration();
                        } else {
                            bb.setNumberedGridConfiguration(j);
                        }

                        // Process the blocks
                        final List<AbstractBlock> originalBlocks = new ArrayList<>(blocks);
                        originalBlocks.addAll(runBlockBuilding(erType, scClusters, profilesD1, profilesD2, bb));

                        if (originalBlocks.isEmpty()) {
                            continue;
                        }

                        final BlocksPerformance methodBlp = new BlocksPerformance(originalBlocks, duplicatePropagation);
                        methodBlp.setStatistics();
                        double recall = methodBlp.getPc();
                        double rr = 1 - methodBlp.getAggregateCardinality() / originalComparisons;
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
                        bb.setNumberedRandomConfiguration(bestIteration);
                    } else {
                        bb.setNumberedGridConfiguration(bestIteration);
                    }
                }

                // Process the blocks with block building
                Platform.runLater(() -> statusLabel.setText("Running block building..."));

                if (erType.equals(JedaiOptions.DIRTY_ER)) {
                    blocks.addAll(bb.getBlocks(profilesD1));
                } else {
                    blocks.addAll(bb.getBlocks(profilesD1, profilesD2));
                }

                time2 = System.currentTimeMillis();
                totalTimeMillis = time2 - time1;

                blp = new BlocksPerformance(blocks, duplicatePropagation);
                blp.setStatistics();
                blp.printStatistics(totalTimeMillis, bb.getMethodConfiguration(),
                        bb.getMethodName());
                this.addBlocksPerformance(bb.getMethodName(), totalTimeMillis, blp);

                // Increment index
                enabledMethodIndex++;
            }
        }

        // Block Cleaning methods local optimization
        Platform.runLater(() -> statusLabel.setText("Running block cleaning..."));

        List<AbstractBlock> cleanedBlocks = blocks;
        if (model.getBlockCleaningMethods() != null && !model.getBlockCleaningMethods().isEmpty()) {
            // Index of the methods in the blClMethods list
            int enabledMethodIndex = 0;

            // Check each block cleaning method config
            for (JedaiMethodConfiguration blClConfig : model.getBlockCleaningMethods()) {
                // Skip disabled methods
                if (!blClConfig.isEnabled())
                    continue;

                // Start time measurement
                time1 = System.currentTimeMillis();

                // Get instance of the method
                IBlockProcessing bp = blClMethods.get(enabledMethodIndex);

                // Check if we should configure this method automatically
                if (blClConfig.getConfigurationType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
                    // Optimize the method
                    optimizeBlockProcessing(bp, blocks, random);
                }

                // Process blocks with this method
                cleanedBlocks = bp.refineBlocks(blocks);

                // Measure milliseconds it took to optimize & run method
                time2 = System.currentTimeMillis();
                totalTimeMillis = time2 - time1;

                blp = new BlocksPerformance(cleanedBlocks, duplicatePropagation);
                blp.setStatistics();
                blp.printStatistics(totalTimeMillis, bp.getMethodConfiguration(), bp.getMethodName());
                this.addBlocksPerformance(bp.getMethodName(), totalTimeMillis, blp);

                // Increment index
                enabledMethodIndex++;
            }
        }

        // Comparison Cleaning local optimization
        Platform.runLater(() -> statusLabel.setText("Running comparison cleaning..."));
        time1 = System.currentTimeMillis();

        List<AbstractBlock> finalBlocks;
        if (model.getComparisonCleaningConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
            // Optimize the comparison cleaning method
            optimizeBlockProcessing(comparisonCleaningMethod, cleanedBlocks, random);
        }

        finalBlocks = comparisonCleaningMethod.refineBlocks(cleanedBlocks);
        time2 = System.currentTimeMillis();
        totalTimeMillis = time2 - time1;

        blp = new BlocksPerformance(finalBlocks, duplicatePropagation);
        blp.setStatistics();
        blp.printStatistics(totalTimeMillis, comparisonCleaningMethod.getMethodConfiguration(),
                comparisonCleaningMethod.getMethodName());
        this.addBlocksPerformance(comparisonCleaningMethod.getMethodName(), totalTimeMillis, blp);

        // Entity Matching & Clustering local optimization
        time1 = System.currentTimeMillis();
        boolean matchingAutomatic = model.getEntityMatchingConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG);
        boolean clusteringAutomatic = model.getEntityClusteringConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG);

        if (matchingAutomatic || clusteringAutomatic) {
            // Show message that we are doing optimization based on the selected options
            String optimizationMsg = (matchingAutomatic ? "matching" : "") +
                    (matchingAutomatic && clusteringAutomatic ? " & " : "") +
                    (clusteringAutomatic ? "clustering" : "");
            Platform.runLater(() -> statusLabel.setText("Entity " + optimizationMsg + " optimization..."));

            double bestFMeasure = 0;

            // Check if we are using random search or grid search
            if (random) {
                bestIteration = 0;

                // Optimize entity matching and clustering with random search
                for (int j = 0; j < NO_OF_TRIALS; j++) {
                    // Set entity matching parameters automatically if needed
                    if (model.getEntityMatchingConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
                        entityMatchingMethod.setNextRandomConfiguration();
                    }
                    final SimilarityPairs sims =
                            entityMatchingMethod.executeComparisons(finalBlocks);

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

                // Set the best iteration's parameters to the methods that should be automatically configured
                if (model.getEntityMatchingConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
                    entityMatchingMethod.setNumberedRandomConfiguration(bestIteration);
                }
                if (model.getEntityClusteringConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
                    ec.setNumberedRandomConfiguration(bestIteration);
                }
            } else {
                // Optimize entity matching and clustering with grid search
                boolean emAutoConfig = model.getEntityMatchingConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG);
                boolean ecAutoConfig = model.getEntityClusteringConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG);

                int bestInnerIteration = 0;
                int bestOuterIteration = 0;

                // Get number of loops for each
                int outerLoops = (emAutoConfig) ? entityMatchingMethod.getNumberOfGridConfigurations() : 1;
                int innerLoops = (ecAutoConfig) ? ec.getNumberOfGridConfigurations() : 1;

                // Iterate all entity matching configurations
                for (int j = 0; j < outerLoops; j++) {
                    if (emAutoConfig) {
                        entityMatchingMethod.setNumberedGridConfiguration(j);
                    }
                    final SimilarityPairs sims = entityMatchingMethod.executeComparisons(finalBlocks);

                    // Iterate all entity clustering configurations
                    for (int k = 0; k < innerLoops; k++) {
                        if (ecAutoConfig) {
                            ec.setNumberedGridConfiguration(k);
                        }
                        final EquivalenceCluster[] clusters = ec.getDuplicates(sims);

                        final ClustersPerformance clp = new ClustersPerformance(clusters, duplicatePropagation);
                        clp.setStatistics();
                        double fMeasure = clp.getFMeasure();
                        if (bestFMeasure < fMeasure) {
                            bestInnerIteration = k;
                            bestOuterIteration = j;
                            bestFMeasure = fMeasure;
                        }
                    }
                }
                System.out.println("\nBest Inner Iteration\t:\t" + bestInnerIteration);
                System.out.println("\nBest Outer Iteration\t:\t" + bestOuterIteration);
                System.out.println("Best FMeasure\t:\t" + bestFMeasure);

                // Set the best iteration's parameters to the methods that should be automatically configured
                if (model.getEntityMatchingConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
                    entityMatchingMethod.setNumberedGridConfiguration(bestOuterIteration);
                }
                if (model.getEntityClusteringConfigType().equals(JedaiOptions.AUTOMATIC_CONFIG)) {
                    ec.setNumberedGridConfiguration(bestInnerIteration);
                }
            }
        }

        // Run entity matching with final configuration
        Platform.runLater(() -> statusLabel.setText("Running entity matching..."));
        final SimilarityPairs sims = entityMatchingMethod.executeComparisons(finalBlocks);

        // Run entity clustering with final configuration
        Platform.runLater(() -> statusLabel.setText("Running entity clustering..."));
        entityClusters = ec.getDuplicates(sims);

        time2 = System.currentTimeMillis();
        totalTimeMillis = time2 - time1;

        final ClustersPerformance clp = new ClustersPerformance(entityClusters, duplicatePropagation);
        clp.setStatistics();
        // todo: Could set the entire configuration details instead of entity clustering method name & config.
        clp.printStatistics(totalTimeMillis, ec.getMethodName(), ec.getMethodConfiguration());

        return clp;
    }
}
