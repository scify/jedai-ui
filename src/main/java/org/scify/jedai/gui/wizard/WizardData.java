package org.scify.jedai.gui.wizard;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.tuple.MutablePair;
import org.scify.jedai.gui.model.JedaiMethodConfiguration;

public class WizardData {
    // Boolean that indicates whether a workflow is currently running
    private final SimpleBooleanProperty workflowRunning = new SimpleBooleanProperty(false);

    // Workflow selection
    private final StringProperty workflow = new SimpleStringProperty();

    // Data Reading
    private final StringProperty erType = new SimpleStringProperty();

    private final StringProperty entityProfilesD1Type = new SimpleStringProperty();
    private final ListProperty<MutablePair<String, Object>> entityProfilesD1Parameters = new SimpleListProperty<>();

    private final StringProperty entityProfilesD2Type = new SimpleStringProperty();
    private final ListProperty<MutablePair<String, Object>> entityProfilesD2Parameters = new SimpleListProperty<>();

    private final StringProperty groundTruthType = new SimpleStringProperty();
    private final ListProperty<MutablePair<String, Object>> groundTruthParameters = new SimpleListProperty<>();

    // Schema Clustering
    private final StringProperty schemaClustering = new SimpleStringProperty();
    private final StringProperty schemaClusteringConfigType = new SimpleStringProperty();
    private final ListProperty<MutablePair<String, Object>> schemaClusteringParameters = new SimpleListProperty<>();

    // Block Building
    private final ListProperty<JedaiMethodConfiguration> blockBuildingMethods = new SimpleListProperty<>();
    private final IntegerProperty enabledBlockBuildingMethods = new SimpleIntegerProperty(-1);

    // Block Cleaning
    private final ListProperty<JedaiMethodConfiguration> blockCleaningMethods = new SimpleListProperty<>();

    // Comparison Cleaning
    private final StringProperty comparisonCleaning = new SimpleStringProperty();
    private final StringProperty comparisonCleaningConfigType = new SimpleStringProperty();
    private final ListProperty<MutablePair<String, Object>> comparisonCleaningParameters = new SimpleListProperty<>();

    // Entity Matching
    private final StringProperty entityMatching = new SimpleStringProperty();
    private final StringProperty entityMatchingConfigType = new SimpleStringProperty();
    private final ListProperty<MutablePair<String, Object>> entityMatchingParameters = new SimpleListProperty<>();

    // Entity Clustering
    private final StringProperty entityClustering = new SimpleStringProperty();
    private final StringProperty entityClusteringConfigType = new SimpleStringProperty();
    private final ListProperty<MutablePair<String, Object>> entityClusteringParameters = new SimpleListProperty<>();

    // Similarity Join (always manual config)
    private final StringProperty similarityJoin = new SimpleStringProperty();
    private final ListProperty<MutablePair<String, Object>> similarityJoinParameters = new SimpleListProperty<>();
    private final StringProperty dataset1Attribute = new SimpleStringProperty();
    private final StringProperty dataset2Attribute = new SimpleStringProperty();

    // Prioritization
    private final StringProperty prioritization = new SimpleStringProperty();
    private final StringProperty prioritizationConfigType = new SimpleStringProperty();
    private final ListProperty<MutablePair<String, Object>> prioritizationParameters = new SimpleListProperty<>();

    // Automatic Configuration
    private final StringProperty autoConfigType = new SimpleStringProperty();
    private final StringProperty searchType = new SimpleStringProperty();

    /**
     * Clone a WizardData object (return a new WizardData object, with the same properties of the given one)
     *
     * @param data WizardData object to clone
     * @return New cloned WizardData object
     */
    public static WizardData cloneData(WizardData data) {
        WizardData clone = new WizardData();

        // Workflow
        clone.setWorkflow(data.getWorkflow());

        // Data Reading
        clone.setErType(data.getErType());

        clone.setEntityProfilesD1Type(data.getEntityProfilesD1Type());
        clone.setEntityProfilesD1Parameters(FXCollections.observableArrayList(data.getEntityProfilesD1Parameters()));

        clone.setEntityProfilesD2Type(data.getEntityProfilesD2Type());
        if (data.getEntityProfilesD2Parameters() != null)
            clone.setEntityProfilesD2Parameters(
                    FXCollections.observableArrayList(data.getEntityProfilesD2Parameters()));

        clone.setGroundTruthType(data.getGroundTruthType());
        clone.setGroundTruthParameters(FXCollections.observableArrayList(data.getGroundTruthParameters()));

        // Schema Clustering
        clone.setSchemaClustering(data.getSchemaClustering());
        clone.setSchemaClusteringConfigType(data.getSchemaClusteringConfigType());
        if (data.getSchemaClusteringParameters() != null)
            clone.setSchemaClusteringParameters(
                    FXCollections.observableArrayList(data.getSchemaClusteringParameters()));

        // Similarity Join
        clone.setSimilarityJoin(data.getSimilarityJoin());
        if (data.getSimilarityJoinParameters() != null)
            clone.setSimilarityJoinParameters(
                    FXCollections.observableArrayList(data.getSimilarityJoinParameters()));
        clone.setDataset1Attribute(data.getDataset1Attribute());
        clone.setDataset2Attribute(data.getDataset2Attribute());

        // Prioritization
        clone.setPrioritization(data.getPrioritization());
        clone.setPrioritizationConfigType(data.getPrioritizationConfigType());
        if (data.getPrioritizationParameters() != null)
            clone.setPrioritizationParameters(
                    FXCollections.observableArrayList(data.getPrioritizationParameters()));

        // Block Building
        ObservableList<JedaiMethodConfiguration> newBlBuMethods = FXCollections.observableArrayList();
        if (data.getBlockBuildingMethods() != null) {
            for (JedaiMethodConfiguration method : data.getBlockBuildingMethods()) {
                // Create new object with the old one's properties to add to the new list
                newBlBuMethods.add(new JedaiMethodConfiguration(method));
            }
        }
        clone.setBlockBuildingMethods(newBlBuMethods);

        // Block Cleaning
        ObservableList<JedaiMethodConfiguration> newBlClMethods = FXCollections.observableArrayList();
        if (data.getBlockCleaningMethods() != null) {
            for (JedaiMethodConfiguration method : data.getBlockCleaningMethods()) {
                // Create new object with the old one's properties to add to the new list
                newBlClMethods.add(new JedaiMethodConfiguration(method));
            }
        }
        clone.setBlockCleaningMethods(newBlClMethods);

        // Comparison Cleaning
        clone.setComparisonCleaning(data.getComparisonCleaning());
        clone.setComparisonCleaningConfigType(data.getComparisonCleaningConfigType());
        if (data.getComparisonCleaningParameters() != null)
            clone.setComparisonCleaningParameters(
                    FXCollections.observableArrayList(data.getComparisonCleaningParameters()));

        // Entity Matching
        clone.setEntityMatching(data.getEntityMatching());
        clone.setEntityMatchingConfigType(data.getEntityMatchingConfigType());
        if (data.getEntityMatchingParameters() != null)
            clone.setEntityMatchingParameters(FXCollections.observableArrayList(data.getEntityMatchingParameters()));

        // Entity Clustering
        clone.setEntityClustering(data.getEntityClustering());
        clone.setEntityClusteringConfigType(data.getEntityClusteringConfigType());
        if (data.getEntityClusteringParameters() != null)
            clone.setEntityClusteringParameters(
                    FXCollections.observableArrayList(data.getEntityClusteringParameters()));

        return clone;
    }

    ///////////////////////////////////////////////////
    // Automatically generated getters/setters below //
    ///////////////////////////////////////////////////

    public boolean isWorkflowRunning() {
        return workflowRunning.get();
    }

    public SimpleBooleanProperty workflowRunningProperty() {
        return workflowRunning;
    }

    public void setWorkflowRunning(boolean workflowRunning) {
        this.workflowRunning.set(workflowRunning);
    }

    public String getWorkflow() {
        return workflow.get();
    }

    public StringProperty workflowProperty() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow.set(workflow);
    }

    public String getErType() {
        return erType.get();
    }

    public StringProperty erTypeProperty() {
        return erType;
    }

    public void setErType(String erType) {
        this.erType.set(erType);
    }

    public String getEntityProfilesD1Type() {
        return entityProfilesD1Type.get();
    }

    public StringProperty entityProfilesD1TypeProperty() {
        return entityProfilesD1Type;
    }

    public void setEntityProfilesD1Type(String entityProfilesD1Type) {
        this.entityProfilesD1Type.set(entityProfilesD1Type);
    }

    public ObservableList<MutablePair<String, Object>> getEntityProfilesD1Parameters() {
        return entityProfilesD1Parameters.get();
    }

    public ListProperty<MutablePair<String, Object>> entityProfilesD1ParametersProperty() {
        return entityProfilesD1Parameters;
    }

    public void setEntityProfilesD1Parameters(ObservableList<MutablePair<String, Object>> entityProfilesD1Parameters) {
        this.entityProfilesD1Parameters.set(entityProfilesD1Parameters);
    }

    public String getEntityProfilesD2Type() {
        return entityProfilesD2Type.get();
    }

    public StringProperty entityProfilesD2TypeProperty() {
        return entityProfilesD2Type;
    }

    public void setEntityProfilesD2Type(String entityProfilesD2Type) {
        this.entityProfilesD2Type.set(entityProfilesD2Type);
    }

    public ObservableList<MutablePair<String, Object>> getEntityProfilesD2Parameters() {
        return entityProfilesD2Parameters.get();
    }

    public ListProperty<MutablePair<String, Object>> entityProfilesD2ParametersProperty() {
        return entityProfilesD2Parameters;
    }

    public void setEntityProfilesD2Parameters(ObservableList<MutablePair<String, Object>> entityProfilesD2Parameters) {
        this.entityProfilesD2Parameters.set(entityProfilesD2Parameters);
    }

    public String getGroundTruthType() {
        return groundTruthType.get();
    }

    public StringProperty groundTruthTypeProperty() {
        return groundTruthType;
    }

    public void setGroundTruthType(String groundTruthType) {
        this.groundTruthType.set(groundTruthType);
    }

    public ObservableList<MutablePair<String, Object>> getGroundTruthParameters() {
        return groundTruthParameters.get();
    }

    public ListProperty<MutablePair<String, Object>> groundTruthParametersProperty() {
        return groundTruthParameters;
    }

    public void setGroundTruthParameters(ObservableList<MutablePair<String, Object>> groundTruthParameters) {
        this.groundTruthParameters.set(groundTruthParameters);
    }

    public String getSchemaClustering() {
        return schemaClustering.get();
    }

    public StringProperty schemaClusteringProperty() {
        return schemaClustering;
    }

    public void setSchemaClustering(String schemaClustering) {
        this.schemaClustering.set(schemaClustering);
    }

    public String getSchemaClusteringConfigType() {
        return schemaClusteringConfigType.get();
    }

    public StringProperty schemaClusteringConfigTypeProperty() {
        return schemaClusteringConfigType;
    }

    public void setSchemaClusteringConfigType(String schemaClusteringConfigType) {
        this.schemaClusteringConfigType.set(schemaClusteringConfigType);
    }

    public ObservableList<MutablePair<String, Object>> getSchemaClusteringParameters() {
        return schemaClusteringParameters.get();
    }

    public ListProperty<MutablePair<String, Object>> schemaClusteringParametersProperty() {
        return schemaClusteringParameters;
    }

    public void setSchemaClusteringParameters(ObservableList<MutablePair<String, Object>> schemaClusteringParameters) {
        this.schemaClusteringParameters.set(schemaClusteringParameters);
    }

    public ObservableList<JedaiMethodConfiguration> getBlockBuildingMethods() {
        return blockBuildingMethods.get();
    }

    public ListProperty<JedaiMethodConfiguration> blockBuildingMethodsProperty() {
        return blockBuildingMethods;
    }

    public void setBlockBuildingMethods(ObservableList<JedaiMethodConfiguration> blockBuildingMethods) {
        this.blockBuildingMethods.set(blockBuildingMethods);
    }

    public int getEnabledBlockBuildingMethods() {
        return enabledBlockBuildingMethods.get();
    }

    public IntegerProperty enabledBlockBuildingMethodsProperty() {
        return enabledBlockBuildingMethods;
    }

    public void setEnabledBlockBuildingMethods(int enabledBlockBuildingMethods) {
        this.enabledBlockBuildingMethods.set(enabledBlockBuildingMethods);
    }

    public ObservableList<JedaiMethodConfiguration> getBlockCleaningMethods() {
        return blockCleaningMethods.get();
    }

    public ListProperty<JedaiMethodConfiguration> blockCleaningMethodsProperty() {
        return blockCleaningMethods;
    }

    public void setBlockCleaningMethods(ObservableList<JedaiMethodConfiguration> blockCleaningMethods) {
        this.blockCleaningMethods.set(blockCleaningMethods);
    }

    public String getComparisonCleaning() {
        return comparisonCleaning.get();
    }

    public StringProperty comparisonCleaningProperty() {
        return comparisonCleaning;
    }

    public void setComparisonCleaning(String comparisonCleaning) {
        this.comparisonCleaning.set(comparisonCleaning);
    }

    public String getComparisonCleaningConfigType() {
        return comparisonCleaningConfigType.get();
    }

    public StringProperty comparisonCleaningConfigTypeProperty() {
        return comparisonCleaningConfigType;
    }

    public void setComparisonCleaningConfigType(String comparisonCleaningConfigType) {
        this.comparisonCleaningConfigType.set(comparisonCleaningConfigType);
    }

    public ObservableList<MutablePair<String, Object>> getComparisonCleaningParameters() {
        return comparisonCleaningParameters.get();
    }

    public ListProperty<MutablePair<String, Object>> comparisonCleaningParametersProperty() {
        return comparisonCleaningParameters;
    }

    public void setComparisonCleaningParameters(ObservableList<MutablePair<String, Object>> comparisonCleaningParameters) {
        this.comparisonCleaningParameters.set(comparisonCleaningParameters);
    }

    public String getEntityMatching() {
        return entityMatching.get();
    }

    public StringProperty entityMatchingProperty() {
        return entityMatching;
    }

    public void setEntityMatching(String entityMatching) {
        this.entityMatching.set(entityMatching);
    }

    public String getEntityMatchingConfigType() {
        return entityMatchingConfigType.get();
    }

    public StringProperty entityMatchingConfigTypeProperty() {
        return entityMatchingConfigType;
    }

    public void setEntityMatchingConfigType(String entityMatchingConfigType) {
        this.entityMatchingConfigType.set(entityMatchingConfigType);
    }

    public ObservableList<MutablePair<String, Object>> getEntityMatchingParameters() {
        return entityMatchingParameters.get();
    }

    public ListProperty<MutablePair<String, Object>> entityMatchingParametersProperty() {
        return entityMatchingParameters;
    }

    public void setEntityMatchingParameters(ObservableList<MutablePair<String, Object>> entityMatchingParameters) {
        this.entityMatchingParameters.set(entityMatchingParameters);
    }

    public String getEntityClustering() {
        return entityClustering.get();
    }

    public StringProperty entityClusteringProperty() {
        return entityClustering;
    }

    public void setEntityClustering(String entityClustering) {
        this.entityClustering.set(entityClustering);
    }

    public String getEntityClusteringConfigType() {
        return entityClusteringConfigType.get();
    }

    public StringProperty entityClusteringConfigTypeProperty() {
        return entityClusteringConfigType;
    }

    public void setEntityClusteringConfigType(String entityClusteringConfigType) {
        this.entityClusteringConfigType.set(entityClusteringConfigType);
    }

    public ObservableList<MutablePair<String, Object>> getEntityClusteringParameters() {
        return entityClusteringParameters.get();
    }

    public ListProperty<MutablePair<String, Object>> entityClusteringParametersProperty() {
        return entityClusteringParameters;
    }

    public void setEntityClusteringParameters(ObservableList<MutablePair<String, Object>> entityClusteringParameters) {
        this.entityClusteringParameters.set(entityClusteringParameters);
    }

    public String getSimilarityJoin() {
        return similarityJoin.get();
    }

    public StringProperty similarityJoinProperty() {
        return similarityJoin;
    }

    public void setSimilarityJoin(String similarityJoin) {
        this.similarityJoin.set(similarityJoin);
    }

    public ObservableList<MutablePair<String, Object>> getSimilarityJoinParameters() {
        return similarityJoinParameters.get();
    }

    public ListProperty<MutablePair<String, Object>> similarityJoinParametersProperty() {
        return similarityJoinParameters;
    }

    public void setSimilarityJoinParameters(ObservableList<MutablePair<String, Object>> similarityJoinParameters) {
        this.similarityJoinParameters.set(similarityJoinParameters);
    }

    public String getDataset1Attribute() {
        return dataset1Attribute.get();
    }

    public StringProperty dataset1AttributeProperty() {
        return dataset1Attribute;
    }

    public void setDataset1Attribute(String dataset1Attribute) {
        this.dataset1Attribute.set(dataset1Attribute);
    }

    public String getDataset2Attribute() {
        return dataset2Attribute.get();
    }

    public StringProperty dataset2AttributeProperty() {
        return dataset2Attribute;
    }

    public void setDataset2Attribute(String dataset2Attribute) {
        this.dataset2Attribute.set(dataset2Attribute);
    }

    public String getPrioritization() {
        return prioritization.get();
    }

    public StringProperty prioritizationProperty() {
        return prioritization;
    }

    public void setPrioritization(String prioritization) {
        this.prioritization.set(prioritization);
    }

    public String getPrioritizationConfigType() {
        return prioritizationConfigType.get();
    }

    public StringProperty prioritizationConfigTypeProperty() {
        return prioritizationConfigType;
    }

    public void setPrioritizationConfigType(String prioritizationConfigType) {
        this.prioritizationConfigType.set(prioritizationConfigType);
    }

    public ObservableList<MutablePair<String, Object>> getPrioritizationParameters() {
        return prioritizationParameters.get();
    }

    public ListProperty<MutablePair<String, Object>> prioritizationParametersProperty() {
        return prioritizationParameters;
    }

    public void setPrioritizationParameters(ObservableList<MutablePair<String, Object>> prioritizationParameters) {
        this.prioritizationParameters.set(prioritizationParameters);
    }

    public String getAutoConfigType() {
        return autoConfigType.get();
    }

    public StringProperty autoConfigTypeProperty() {
        return autoConfigType;
    }

    public void setAutoConfigType(String autoConfigType) {
        this.autoConfigType.set(autoConfigType);
    }

    public String getSearchType() {
        return searchType.get();
    }

    public StringProperty searchTypeProperty() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType.set(searchType);
    }
}
