package org.scify.jedai.gui.workbench;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * A workflow run result that can be shown in the Workbench
 */
public class WorkflowResult {
    private final SimpleIntegerProperty runNumber;
    private final SimpleDoubleProperty recall;
    private final SimpleDoubleProperty precision;
    private final SimpleDoubleProperty f1Measure;
    private final SimpleDoubleProperty totalTime;
    private final SimpleIntegerProperty inputInstances;
    private final SimpleIntegerProperty numOfClusters;
    private final SimpleIntegerProperty detailsId;

    public WorkflowResult(SimpleIntegerProperty runNumber, SimpleDoubleProperty recall, SimpleDoubleProperty precision,
                          SimpleDoubleProperty f1Measure, SimpleDoubleProperty totalTime,
                          SimpleIntegerProperty inputInstances, SimpleIntegerProperty numOfClusters,
                          SimpleIntegerProperty detailsId) {
        this.runNumber = runNumber;
        this.recall = recall;
        this.precision = precision;
        this.f1Measure = f1Measure;
        this.totalTime = totalTime;
        this.inputInstances = inputInstances;
        this.numOfClusters = numOfClusters;
        this.detailsId = detailsId;
    }

    public int getDetailsId() {
        return detailsId.get();
    }

    public SimpleIntegerProperty detailsIdProperty() {
        return detailsId;
    }

    public int getRunNumber() {
        return runNumber.get();
    }

    public SimpleIntegerProperty runNumberProperty() {
        return runNumber;
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
}
