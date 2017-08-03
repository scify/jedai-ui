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

    private final StringProperty entityProfilesD1Path = new SimpleStringProperty();
    private final StringProperty entityProfilesD1Type = new SimpleStringProperty();
    private final ListProperty<Object> entityProfilesD1Parameters = new SimpleListProperty<>();

    private final StringProperty entityProfilesD2Path = new SimpleStringProperty();
    private final StringProperty entityProfilesD2Type = new SimpleStringProperty();
    private final ListProperty<Object> entityProfilesD2Parameters = new SimpleListProperty<>();

    private final StringProperty groundTruthPath = new SimpleStringProperty();
    private final StringProperty groundTruthType = new SimpleStringProperty();
    private final ListProperty<Object> groundTruthParameters = new SimpleListProperty<>();

    private final StringProperty blockBuilding = new SimpleStringProperty();
    private final ListProperty<Object> blockBuildingParameters = new SimpleListProperty<>();

    private final ListProperty<String> blockCleaningMethods = new SimpleListProperty<>();

    private final StringProperty comparisonCleaningMethod = new SimpleStringProperty();
    private final ListProperty<Object> comparisonCleaningParameters = new SimpleListProperty<>();

    private final StringProperty entityMatching = new SimpleStringProperty();
    private final StringProperty representationModel = new SimpleStringProperty();
    private final StringProperty similarityMethod = new SimpleStringProperty();

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
        clone.setEntityProfilesD1Path(data.getEntityProfilesD1Path());
        clone.setEntityProfilesD1Type(data.getEntityProfilesD1Type());
        clone.setEntityProfilesD2Path(data.getEntityProfilesD2Path());
        clone.setEntityProfilesD2Type(data.getEntityProfilesD2Type());
        clone.setGroundTruthPath(data.getGroundTruthPath());
        clone.setGroundTruthType(data.getGroundTruthType());
        clone.setBlockBuilding(data.getBlockBuilding());
        clone.setComparisonCleaningMethod(data.getComparisonCleaningMethod());
        clone.setBlockCleaningMethods(FXCollections.observableArrayList(data.getBlockCleaningMethods()));
        clone.setEntityMatching(data.getEntityMatching());
        clone.setRepresentationModel(data.getRepresentationModel());
        clone.setSimilarityMethod(data.getSimilarityMethod());
        clone.setEntityClustering(data.getEntityClustering());
        //todo: clone parameter lists too

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

    public String getSimilarityMethod() {
        return similarityMethod.get();
    }

    public StringProperty similarityMethodProperty() {
        return similarityMethod;
    }

    public void setSimilarityMethod(String similarityMethod) {
        this.similarityMethod.set(similarityMethod);
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

    public String getEntityProfilesD2Path() {
        return entityProfilesD2Path.get();
    }

    public StringProperty entityProfilesD2PathProperty() {
        return entityProfilesD2Path;
    }

    public void setEntityProfilesD2Path(String entityProfilesD2Path) {
        this.entityProfilesD2Path.set(entityProfilesD2Path);
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

    public String getGroundTruthPath() {
        return groundTruthPath.get();
    }

    public StringProperty groundTruthPathProperty() {
        return groundTruthPath;
    }

    public void setGroundTruthPath(String groundTruthPath) {
        this.groundTruthPath.set(groundTruthPath);
    }

    public String getEntityProfilesD1Path() {
        return entityProfilesD1Path.get();
    }

    public StringProperty entityProfilesD1PathProperty() {
        return entityProfilesD1Path;
    }

    public void setEntityProfilesD1Path(String entityProfilesD1Path) {
        this.entityProfilesD1Path.set(entityProfilesD1Path);
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

    public String getComparisonCleaningMethod() {
        return comparisonCleaningMethod.get();
    }

    public StringProperty comparisonCleaningMethodProperty() {
        return comparisonCleaningMethod;
    }

    public void setComparisonCleaningMethod(String comparisonCleaningMethod) {
        this.comparisonCleaningMethod.set(comparisonCleaningMethod);
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

        // Reset radio buttons
        blockBuildingProperty().setValue(JedaiOptions.STANDARD_TOKEN_BUILDING);
        comparisonCleaningMethodProperty().setValue(JedaiOptions.NO_CLEANING);
        entityMatchingProperty().setValue(JedaiOptions.GROUP_LINKAGE);
        entityClusteringProperty().setValue(JedaiOptions.CENTER_CLUSTERING);
        representationModelProperty().setValue(JedaiOptions.CHARACTER_BIGRAMS);
        similarityMethodProperty().setValue(JedaiOptions.ARCS_SIMILARITY);

        //todo: reset the parameter lists too
    }
}
