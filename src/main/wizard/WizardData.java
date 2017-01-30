package main.wizard;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WizardData {
    private final StringProperty dataset = new SimpleStringProperty();
    private final StringProperty blockBuilding = new SimpleStringProperty();
    private final StringProperty blockProcessing = new SimpleStringProperty();
    private final StringProperty entityMatching = new SimpleStringProperty();
    private final StringProperty entityClustering = new SimpleStringProperty();

    public String getDataset() {
        return dataset.get();
    }

    public StringProperty datasetProperty() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset.set(dataset);
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

    public String getBlockProcessing() {
        return blockProcessing.get();
    }

    public StringProperty blockProcessingProperty() {
        return blockProcessing;
    }

    public void setBlockProcessing(String blockProcessing) {
        this.blockProcessing.set(blockProcessing);
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
        dataset.set("");
        blockBuilding.set("");
        blockProcessing.set("");
        entityMatching.set("");
        entityClustering.set("");
    }
}
