package main.wizard;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WizardData {

    private final StringProperty dataset = new SimpleStringProperty();
    private final StringProperty blockBuilding = new SimpleStringProperty();
    private final StringProperty field5 = new SimpleStringProperty();
    private final StringProperty field6 = new SimpleStringProperty();
    private final StringProperty field7 = new SimpleStringProperty();

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

    public String getField5() {
        return field5.get();
    }

    public StringProperty field5Property() {
        return field5;
    }

    public void setField5(String field5) {
        this.field5.set(field5);
    }

    public String getField6() {
        return field6.get();
    }

    public StringProperty field6Property() {
        return field6;
    }

    public void setField6(String field6) {
        this.field6.set(field6);
    }

    public String getField7() {
        return field7.get();
    }

    public StringProperty field7Property() {
        return field7;
    }

    public void setField7(String field7) {
        this.field7.set(field7);
    }

    public void reset() {
        dataset.set("");
        blockBuilding.set("");
        field5.set("");
        field6.set("");
        field7.set("");
    }
}
