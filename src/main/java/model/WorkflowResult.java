package model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 * A workflow run result that can be shown in the Workbench
 */
public class WorkflowResult {
    private final SimpleIntegerProperty runNumber;
    private final SimpleDoubleProperty recall;
    private final SimpleDoubleProperty precision;
    private final SimpleDoubleProperty f1Measure;
    private final SimpleLongProperty totalTime;
    private final SimpleIntegerProperty inputInstances;
    private final SimpleIntegerProperty numOfClusters;

    public WorkflowResult(SimpleIntegerProperty runNumber, SimpleDoubleProperty recall, SimpleDoubleProperty precision, SimpleDoubleProperty f1Measure, SimpleLongProperty totalTime, SimpleIntegerProperty inputInstances, SimpleIntegerProperty numOfClusters) {
        this.runNumber = runNumber;
        this.recall = recall;
        this.precision = precision;
        this.f1Measure = f1Measure;
        this.totalTime = totalTime;
        this.inputInstances = inputInstances;
        this.numOfClusters = numOfClusters;
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

    public long getTotalTime() {
        return totalTime.get();
    }

    public SimpleLongProperty totalTimeProperty() {
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
