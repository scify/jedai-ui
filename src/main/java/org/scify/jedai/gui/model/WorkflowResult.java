package org.scify.jedai.gui.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * A workflow run result that can be shown in the Workbench
 */
public class WorkflowResult {
    private final SimpleStringProperty resultName;
    private final SimpleDoubleProperty recall;
    private final SimpleDoubleProperty precision;
    private final SimpleDoubleProperty f1Measure;
    private final SimpleDoubleProperty totalTime;
    private final SimpleIntegerProperty inputInstances;
    private final SimpleIntegerProperty numOfClusters;
    private final SimpleIntegerProperty detailsId;

    public WorkflowResult(SimpleStringProperty resultName, SimpleDoubleProperty recall, SimpleDoubleProperty precision,
                          SimpleDoubleProperty f1Measure, SimpleDoubleProperty totalTime,
                          SimpleIntegerProperty inputInstances, SimpleIntegerProperty numOfClusters,
                          SimpleIntegerProperty detailsId) {
        this.resultName = resultName;
        this.recall = recall;
        this.precision = precision;
        this.f1Measure = f1Measure;
        this.totalTime = totalTime;
        this.inputInstances = inputInstances;
        this.numOfClusters = numOfClusters;
        this.detailsId = detailsId;
    }

    public String getResultName() {
        return resultName.get();
    }

    public SimpleStringProperty resultNameProperty() {
        return resultName;
    }

    public double getRecall() {
        return recall.get();
    }

    public SimpleDoubleProperty recallProperty() {
        return recall;
    }

    public double getPrecision() {
        return precision.get();
    }

    public SimpleDoubleProperty precisionProperty() {
        return precision;
    }

    public double getF1Measure() {
        return f1Measure.get();
    }

    public SimpleDoubleProperty f1MeasureProperty() {
        return f1Measure;
    }

    public double getTotalTime() {
        return totalTime.get();
    }

    public SimpleDoubleProperty totalTimeProperty() {
        return totalTime;
    }

    public int getInputInstances() {
        return inputInstances.get();
    }

    public SimpleIntegerProperty inputInstancesProperty() {
        return inputInstances;
    }

    public int getNumOfClusters() {
        return numOfClusters.get();
    }

    public SimpleIntegerProperty numOfClustersProperty() {
        return numOfClusters;
    }

    public int getDetailsId() {
        return detailsId.get();
    }

    public SimpleIntegerProperty detailsIdProperty() {
        return detailsId;
    }
}
