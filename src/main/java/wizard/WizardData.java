package wizard;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.JedaiOptions;

public class WizardData {
    private final StringProperty erType = new SimpleStringProperty();

    private final StringProperty entityProfilesD1Type = new SimpleStringProperty();
    private final ListProperty<Object> entityProfilesD1Parameters = new SimpleListProperty<>();

    private final StringProperty entityProfilesD2Type = new SimpleStringProperty();
    private final ListProperty<Object> entityProfilesD2Parameters = new SimpleListProperty<>();

    private final StringProperty groundTruthType = new SimpleStringProperty();
    private final ListProperty<Object> groundTruthParameters = new SimpleListProperty<>();

    private final StringProperty blockBuilding = new SimpleStringProperty();
    private final ListProperty<Object> blockBuildingParameters = new SimpleListProperty<>();

    private final ListProperty<String> blockCleaningMethods = new SimpleListProperty<>();

    private final StringProperty comparisonCleaning = new SimpleStringProperty();
    private final ListProperty<Object> comparisonCleaningParameters = new SimpleListProperty<>();

    private final StringProperty entityMatching = new SimpleStringProperty();
    private final StringProperty representationModel = new SimpleStringProperty();
    private final StringProperty similarityMetric = new SimpleStringProperty();

    private final StringProperty entityClustering = new SimpleStringProperty();
    private final ListProperty<Object> entityClusteringParameters = new SimpleListProperty<>();

    /**
     * Clone a WizardData object (return a new WizardData object, with the same properties of the given one)
     *
     * @param data WizardData object to clone
     * @return New cloned WizardData object
     */
    public static WizardData cloneData(WizardData data) {
        WizardData clone = new WizardData();

        clone.setErType(data.getErType());

        clone.setEntityProfilesD1Type(data.getEntityProfilesD1Type());
        clone.setEntityProfilesD1Parameters(FXCollections.observableList(data.getEntityProfilesD1Parameters()));

        clone.setEntityProfilesD2Type(data.getEntityProfilesD2Type());
        if (data.getEntityProfilesD2Parameters() != null)
            clone.setEntityProfilesD2Parameters(FXCollections.observableList(data.getEntityProfilesD2Parameters()));

        clone.setGroundTruthType(data.getGroundTruthType());
        clone.setGroundTruthParameters(FXCollections.observableList(data.getGroundTruthParameters()));

        clone.setBlockBuilding(data.getBlockBuilding());
        if (data.getBlockBuildingParameters() != null)
            clone.setBlockBuildingParameters(FXCollections.observableList(data.getBlockBuildingParameters()));

        clone.setComparisonCleaning(data.getComparisonCleaning());
        if (data.getComparisonCleaningParameters() != null)
            clone.setComparisonCleaningParameters(FXCollections.observableList(data.getComparisonCleaningParameters()));

        clone.setBlockCleaningMethods(FXCollections.observableList(data.getBlockCleaningMethods()));

        clone.setEntityMatching(data.getEntityMatching());
        clone.setRepresentationModel(data.getRepresentationModel());
        clone.setSimilarityMetric(data.getSimilarityMetric());

        clone.setEntityClustering(data.getEntityClustering());
        if (data.getEntityClusteringParameters() != null)
            clone.setEntityClusteringParameters(FXCollections.observableList(data.getEntityClusteringParameters()));

        return clone;
    }

    public ObservableList<Object> getEntityProfilesD1Parameters() {
        return entityProfilesD1Parameters.get();
    }

    public ListProperty<Object> entityProfilesD1ParametersProperty() {
        return entityProfilesD1Parameters;
    }

    public void setEntityProfilesD1Parameters(ObservableList<Object> entityProfilesD1Parameters) {
        this.entityProfilesD1Parameters.set(entityProfilesD1Parameters);
    }

    public ObservableList<Object> getEntityProfilesD2Parameters() {
        return entityProfilesD2Parameters.get();
    }

    public ListProperty<Object> entityProfilesD2ParametersProperty() {
        return entityProfilesD2Parameters;
    }

    public void setEntityProfilesD2Parameters(ObservableList<Object> entityProfilesD2Parameters) {
        this.entityProfilesD2Parameters.set(entityProfilesD2Parameters);
    }

    public ObservableList<Object> getGroundTruthParameters() {
        return groundTruthParameters.get();
    }

    public ListProperty<Object> groundTruthParametersProperty() {
        return groundTruthParameters;
    }

    public void setGroundTruthParameters(ObservableList<Object> groundTruthParameters) {
        this.groundTruthParameters.set(groundTruthParameters);
    }

    public ObservableList<Object> getEntityClusteringParameters() {
        return entityClusteringParameters.get();
    }

    public ListProperty<Object> entityClusteringParametersProperty() {
        return entityClusteringParameters;
    }

    public void setEntityClusteringParameters(ObservableList<Object> entityClusteringParameters) {
        this.entityClusteringParameters.set(entityClusteringParameters);
    }

    public ObservableList<Object> getComparisonCleaningParameters() {
        return comparisonCleaningParameters.get();
    }

    public ListProperty<Object> comparisonCleaningParametersProperty() {
        return comparisonCleaningParameters;
    }

    public void setComparisonCleaningParameters(ObservableList<Object> comparisonCleaningParameters) {
        this.comparisonCleaningParameters.set(comparisonCleaningParameters);
    }

    public ObservableList<Object> getBlockBuildingParameters() {
        return blockBuildingParameters.get();
    }

    public ListProperty<Object> blockBuildingParametersProperty() {
        return blockBuildingParameters;
    }

    public void setBlockBuildingParameters(ObservableList<Object> blockBuildingParameters) {
        this.blockBuildingParameters.set(blockBuildingParameters);
    }

    public String getSimilarityMetric() {
        return similarityMetric.get();
    }

    public StringProperty similarityMetricProperty() {
        return similarityMetric;
    }

    public void setSimilarityMetric(String similarityMetric) {
        this.similarityMetric.set(similarityMetric);
    }

    public String getRepresentationModel() {
        return representationModel.get();
    }

    public StringProperty representationModelProperty() {
        return representationModel;
    }

    public void setRepresentationModel(String representationModel) {
        this.representationModel.set(representationModel);
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

    public String getEntityProfilesD2Type() {
        return entityProfilesD2Type.get();
    }

    public StringProperty entityProfilesD2TypeProperty() {
        return entityProfilesD2Type;
    }

    public void setEntityProfilesD2Type(String entityProfilesD2Type) {
        this.entityProfilesD2Type.set(entityProfilesD2Type);
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

    public String getErType() {
        return erType.get();
    }

    public StringProperty erTypeProperty() {
        return erType;
    }

    public void setErType(String erType) {
        this.erType.set(erType);
    }

    public ObservableList<String> getBlockCleaningMethods() {
        return blockCleaningMethods.get();
    }

    public ListProperty<String> blockCleaningMethodsProperty() {
        return blockCleaningMethods;
    }

    public void setBlockCleaningMethods(ObservableList<String> blockCleaningMethods) {
        this.blockCleaningMethods.set(blockCleaningMethods);
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

    public String getComparisonCleaning() {
        return comparisonCleaning.get();
    }

    public StringProperty comparisonCleaningProperty() {
        return comparisonCleaning;
    }

    public void setComparisonCleaning(String comparisonCleaning) {
        this.comparisonCleaning.set(comparisonCleaning);
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

    public String getEntityClustering() {
        return entityClustering.get();
    }

    public StringProperty entityClusteringProperty() {
        return entityClustering;
    }

    public void setEntityClustering(String entityClustering) {
        this.entityClustering.set(entityClustering);
    }

    public void reset() {
        // Reset block cleaning list
        blockCleaningMethodsProperty().clear();

        // Reset advanced configuration parameters (except for Data Reading)
        blockBuildingParametersProperty().clear();
        comparisonCleaningParametersProperty().clear();
        entityClusteringParametersProperty().clear();

        // Reset radio buttons
        blockBuildingProperty().setValue(JedaiOptions.STANDARD_TOKEN_BUILDING);
        comparisonCleaningProperty().setValue(JedaiOptions.NO_CLEANING);
        entityMatchingProperty().setValue(JedaiOptions.GROUP_LINKAGE);
        entityClusteringProperty().setValue(JedaiOptions.CENTER_CLUSTERING);
        representationModelProperty().setValue(JedaiOptions.CHARACTER_BIGRAMS);
    }
}
