package org.scify.jedai.gui.utilities;

import org.scify.jedai.blockbuilding.IBlockBuilding;
import org.scify.jedai.blockprocessing.IBlockProcessing;
import org.scify.jedai.datamodel.AbstractBlock;
import org.scify.jedai.datamodel.EntityProfile;
import org.scify.jedai.datamodel.EquivalenceCluster;
import org.scify.jedai.datamodel.SimilarityPairs;
import org.scify.jedai.entityclustering.IEntityClustering;
import org.scify.jedai.entitymatching.IEntityMatching;
import org.scify.jedai.gui.wizard.WizardData;
import org.scify.jedai.utilities.BlocksPerformance;
import org.scify.jedai.utilities.ClustersPerformance;
import org.scify.jedai.utilities.datastructures.AbstractDuplicatePropagation;

import java.util.List;

public class WorkflowManager {
    private final static int NO_OF_TRIALS = 100;
    private final WizardData model;

    private EquivalenceCluster[] entityClusters;
    private List<EntityProfile> profilesD1;
    private List<EntityProfile> profilesD2;

    public WorkflowManager(WizardData model) {
        this.model = model;
    }

    public List<EntityProfile> getProfilesD1() {
        return profilesD1;
    }

    public void setProfilesD1(List<EntityProfile> profilesD1) {
        this.profilesD1 = profilesD1;
    }

    public List<EntityProfile> getProfilesD2() {
        return profilesD2;
    }

    public void setProfilesD2(List<EntityProfile> profilesD2) {
        this.profilesD2 = profilesD2;
    }

    public EquivalenceCluster[] getEntityClusters() {
        return entityClusters;
    }

    public void setEntityClusters(EquivalenceCluster[] entityClusters) {
        this.entityClusters = entityClusters;
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
     * @param erType      Entity Resolution type (Dirty/Clean-Clean)
     * @param duProp      Duplicate propagation (from ground truth)
     * @param blBu        Block building method
     * @param blClMethods List of block cleaning methods
     * @param coCl        Comparison cleaning method
     * @param em          Entity matching method
     * @param ec          Entity clustering method
     * @param output      Set to true to print messages while running workflow
     * @return ClustersPerformance object of the executed workflow
     * @throws Exception In case the Entity Matching method is null (shouldn't happen though)
     */
    public ClustersPerformance runWorkflow(String erType, AbstractDuplicatePropagation duProp, IBlockBuilding blBu,
                                           List<IBlockProcessing> blClMethods, IBlockProcessing coCl,
                                           IEntityMatching em, IEntityClustering ec, boolean output) throws Exception {
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
        blp = new BlocksPerformance(blocks, duProp);
        blp.setStatistics();
        if (output)
            blp.printStatistics(overheadEnd - overheadStart, blBu.getMethodConfiguration(), blBu.getMethodName());

        // Set progress indicator to 40%
//        updateProgress(0.4);

        // Step 3: Block Cleaning
        if (blClMethods != null && !blClMethods.isEmpty()) {
            // Execute the methods
            for (IBlockProcessing currentMethod : blClMethods) {
                blocks = runBlockProcessing(duProp, output, blocks, currentMethod);
            }
        }

        // Step 4: Comparison Cleaning
        if (coCl != null) {
            blocks = runBlockProcessing(duProp, output, blocks, coCl);
        }

        // Set progress indicator to 60%
//        updateProgress(0.6);

        // Step 5: Entity Matching
        SimilarityPairs simPairs;

        if (em == null)
            throw new Exception("Entity Matching method is null!");

        if (erType.equals(JedaiOptions.DIRTY_ER)) {
            simPairs = em.executeComparisons(blocks, profilesD1);
        } else {
            simPairs = em.executeComparisons(blocks, profilesD1, profilesD2);
        }

        // Set progress indicator to 80%
//        updateProgress(0.8);

        // Step 6: Entity Clustering
        overheadStart = System.currentTimeMillis();

        entityClusters = ec.getDuplicates(simPairs);

        // Print clustering performance
        overheadEnd = System.currentTimeMillis();
        ClustersPerformance clp = new ClustersPerformance(entityClusters, duProp);
        clp.setStatistics();
        if (output)
            clp.printStatistics(overheadEnd - overheadStart, ec.getMethodName(),
                    ec.getMethodConfiguration());

        return clp;
    }
}
