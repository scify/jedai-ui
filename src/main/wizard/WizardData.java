package main.wizard;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class WizardData {

    private final StringProperty dataset = new SimpleStringProperty();
    private final StringProperty field4 = new SimpleStringProperty();
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

    public String getField4() {
        return field4.get();
    }

    public StringProperty field4Property() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4.set(field4);
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
        field4.set("");
        field5.set("");
        field6.set("");
        field7.set("");
    }
}
