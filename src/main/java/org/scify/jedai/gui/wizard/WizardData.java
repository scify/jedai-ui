package org.scify.jedai.gui.wizard;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.scify.jedai.gui.utilities.BlClMethodConfiguration;
import org.scify.jedai.gui.utilities.JPair;
import org.scify.jedai.gui.utilities.JedaiOptions;

public class WizardData {
    // Step 1: Data Reading
    private final StringProperty erType = new SimpleStringProperty();

    private final StringProperty entityProfilesD1Type = new SimpleStringProperty();
    private final ListProperty<JPair<String, Object>> entityProfilesD1Parameters = new SimpleListProperty<>();

    private final StringProperty entityProfilesD2Type = new SimpleStringProperty();
    private final ListProperty<JPair<String, Object>> entityProfilesD2Parameters = new SimpleListProperty<>();

    private final StringProperty groundTruthType = new SimpleStringProperty();
    private final ListProperty<JPair<String, Object>> groundTruthParameters = new SimpleListProperty<>();

    // Step 2: Block Building
    private final StringProperty blockBuilding = new SimpleStringProperty();
    private final StringProperty blockBuildingConfigType = new SimpleStringProperty();
    private final ListProperty<JPair<String, Object>> blockBuildingParameters = new SimpleListProperty<>();

    // Step 3: Block Cleaning
    private final ListProperty<BlClMethodConfiguration> blockCleaningMethods = new SimpleListProperty<>();

    // Step 4: Comparison Cleaning
    private final StringProperty comparisonCleaning = new SimpleStringProperty();
    private final StringProperty comparisonCleaningConfigType = new SimpleStringProperty();
    private final ListProperty<JPair<String, Object>> comparisonCleaningParameters = new SimpleListProperty<>();

    // Step 5: Entity Matching
    private final StringProperty entityMatching = new SimpleStringProperty();
    private final StringProperty entityMatchingConfigType = new SimpleStringProperty();
    private final ListProperty<JPair<String, Object>> entityMatchingParameters = new SimpleListProperty<>();

    // Step 6: Entity Clustering
    private final StringProperty entityClustering = new SimpleStringProperty();
    private final StringProperty entityClusteringConfigType = new SimpleStringProperty();
    private final ListProperty<JPair<String, Object>> entityClusteringParameters = new SimpleListProperty<>();

    /**
     * Clone a WizardData object (return a new WizardData object, with the same properties of the given one)
     *
     * @param data WizardData object to clone
     * @return New cloned WizardData object
     */
    public static WizardData cloneData(WizardData data) {
        WizardData clone = new WizardData();

        // Step 1: Data Reading
        clone.setErType(data.getErType());

        clone.setEntityProfilesD1Type(data.getEntityProfilesD1Type());
        clone.setEntityProfilesD1Parameters(FXCollections.observableList(data.getEntityProfilesD1Parameters()));

        clone.setEntityProfilesD2Type(data.getEntityProfilesD2Type());
        if (data.getEntityProfilesD2Parameters() != null)
            clone.setEntityProfilesD2Parameters(FXCollections.observableList(data.getEntityProfilesD2Parameters()));

        clone.setGroundTruthType(data.getGroundTruthType());
        clone.setGroundTruthParameters(FXCollections.observableList(data.getGroundTruthParameters()));

        // Step 2: Block Building
        clone.setBlockBuilding(data.getBlockBuilding());
        clone.setBlockBuildingConfigType(data.getBlockBuildingConfigType());
        if (data.getBlockBuildingParameters() != null)
            clone.setBlockBuildingParameters(FXCollections.observableList(data.getBlockBuildingParameters()));

        // Step 3: Block Cleaning
        clone.setBlockCleaningMethods(FXCollections.observableList(data.getBlockCleaningMethods()));

        // Step 4: Comparison Cleaning
        clone.setComparisonCleaning(data.getComparisonCleaning());
        clone.setComparisonCleaningConfigType(data.getComparisonCleaningConfigType());
        if (data.getComparisonCleaningParameters() != null)
            clone.setComparisonCleaningParameters(FXCollections.observableList(data.getComparisonCleaningParameters()));

        // Step 5: Entity Matching
        clone.setEntityMatching(data.getEntityMatching());
        clone.setEntityMatchingConfigType(data.getEntityMatchingConfigType());
        if (data.getEntityMatchingParameters() != null)
            clone.setEntityMatchingParameters(FXCollections.observableList(data.getEntityMatchingParameters()));

        // Step 6: Entity Clustering
        clone.setEntityClustering(data.getEntityClustering());
        clone.setEntityClusteringConfigType(data.getEntityClusteringConfigType());
        if (data.getEntityClusteringParameters() != null)
            clone.setEntityClusteringParameters(FXCollections.observableList(data.getEntityClusteringParameters()));

        return clone;
    }

    /**
     * Reset the model, setting all values to their defaults.
     */
    public void reset() {
        // Reset block cleaning list
        blockCleaningMethodsProperty().clear();

        // Reset advanced configuration parameters (except for Data Reading)
        blockBuildingParametersProperty().clear();
        comparisonCleaningParametersProperty().clear();
        entityClusteringParametersProperty().clear();
        entityMatchingParametersProperty().clear();

        // Reset radio buttons
        blockBuildingProperty().setValue(JedaiOptions.STANDARD_TOKEN_BUILDING);
        comparisonCleaningProperty().setValue(JedaiOptions.NO_CLEANING);
        entityMatchingProperty().setValue(JedaiOptions.GROUP_LINKAGE);
        entityClusteringProperty().setValue(JedaiOptions.CENTER_CLUSTERING);

        // Reset configuration type radio buttons
        blockBuildingConfigTypeProperty().setValue(JedaiOptions.DEFAULT_CONFIG);
        comparisonCleaningConfigTypeProperty().setValue(JedaiOptions.DEFAULT_CONFIG);
        entityMatchingConfigTypeProperty().setValue(JedaiOptions.DEFAULT_CONFIG);
        entityClusteringConfigTypeProperty().setValue(JedaiOptions.DEFAULT_CONFIG);
    }

    ///////////////////////////////////////////////////
    // Automatically generated getters/setters below //
    ///////////////////////////////////////////////////

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

    public ObservableList<JPair<String, Object>> getEntityProfilesD1Parameters() {
        return entityProfilesD1Parameters.get();
    }

    public ListProperty<JPair<String, Object>> entityProfilesD1ParametersProperty() {
        return entityProfilesD1Parameters;
    }

    public void setEntityProfilesD1Parameters(ObservableList<JPair<String, Object>> entityProfilesD1Parameters) {
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

    public ObservableList<JPair<String, Object>> getEntityProfilesD2Parameters() {
        return entityProfilesD2Parameters.get();
    }

    public ListProperty<JPair<String, Object>> entityProfilesD2ParametersProperty() {
        return entityProfilesD2Parameters;
    }

    public void setEntityProfilesD2Parameters(ObservableList<JPair<String, Object>> entityProfilesD2Parameters) {
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

    public ObservableList<JPair<String, Object>> getGroundTruthParameters() {
        return groundTruthParameters.get();
    }

    public ListProperty<JPair<String, Object>> groundTruthParametersProperty() {
        return groundTruthParameters;
    }

    public void setGroundTruthParameters(ObservableList<JPair<String, Object>> groundTruthParameters) {
        this.groundTruthParameters.set(groundTruthParameters);
    }

    public String getBlockBuilding() {
        return blockBuilding.get();
    }

    public StringProperty blockBuildingProperty() {
        return blockBuilding;
    }

    public void setBlockBuilding(String blockBuilding) {
        this.blockBuilding.set(blockBuilding);
    }

    public String getBlockBuildingConfigType() {
        return blockBuildingConfigType.get();
    }

    public StringProperty blockBuildingConfigTypeProperty() {
        return blockBuildingConfigType;
    }

    public void setBlockBuildingConfigType(String blockBuildingConfigType) {
        this.blockBuildingConfigType.set(blockBuildingConfigType);
    }

    public ObservableList<JPair<String, Object>> getBlockBuildingParameters() {
        return blockBuildingParameters.get();
    }

    public ListProperty<JPair<String, Object>> blockBuildingParametersProperty() {
        return blockBuildingParameters;
    }

    public void setBlockBuildingParameters(ObservableList<JPair<String, Object>> blockBuildingParameters) {
        this.blockBuildingParameters.set(blockBuildingParameters);
    }

    public ObservableList<BlClMethodConfiguration> getBlockCleaningMethods() {
        return blockCleaningMethods.get();
    }

    public ListProperty<BlClMethodConfiguration> blockCleaningMethodsProperty() {
        return blockCleaningMethods;
    }

    public void setBlockCleaningMethods(ObservableList<BlClMethodConfiguration> blockCleaningMethods) {
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

    public ObservableList<JPair<String, Object>> getComparisonCleaningParameters() {
        return comparisonCleaningParameters.get();
    }

    public ListProperty<JPair<String, Object>> comparisonCleaningParametersProperty() {
        return comparisonCleaningParameters;
    }

    public void setComparisonCleaningParameters(ObservableList<JPair<String, Object>> comparisonCleaningParameters) {
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

    public ObservableList<JPair<String, Object>> getEntityMatchingParameters() {
        return entityMatchingParameters.get();
    }

    public ListProperty<JPair<String, Object>> entityMatchingParametersProperty() {
        return entityMatchingParameters;
    }

    public void setEntityMatchingParameters(ObservableList<JPair<String, Object>> entityMatchingParameters) {
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

    public ObservableList<JPair<String, Object>> getEntityClusteringParameters() {
        return entityClusteringParameters.get();
    }

    public ListProperty<JPair<String, Object>> entityClusteringParametersProperty() {
        return entityClusteringParameters;
    }

    public void setEntityClusteringParameters(ObservableList<JPair<String, Object>> entityClusteringParameters) {
        this.entityClusteringParameters.set(entityClusteringParameters);
    }
}
