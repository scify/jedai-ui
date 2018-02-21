package utils;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

/**
 * Block Cleaning Method configuration object.
 * Contains:
 * - Method name
 * - Whether the method is enabled or not
 * - The selected configuration type for this method
 * - For manual configuration, the selected parameters
 */
public class BlClMethodConfiguration {
    private SimpleBooleanProperty enabled;
    private String name;
    private StringProperty configurationType;
    private List<JPair<String, Object>> manualParameters;

    public BlClMethodConfiguration(String methodName) {
        this.name = methodName;
        this.enabled = new SimpleBooleanProperty(false);
        this.configurationType = new SimpleStringProperty();
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public SimpleBooleanProperty enabledProperty() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public String getName() {
        return name;
    }

    public String getConfigurationType() {
        return configurationType.get();
    }

    public StringProperty configurationTypeProperty() {
        return configurationType;
    }

    public void setConfigurationType(String configurationType) {
        this.configurationType.set(configurationType);
    }

    public List<JPair<String, Object>> getManualParameters() {
        return manualParameters;
    }

    public void setManualParameters(List<JPair<String, Object>> manualParameters) {
        this.manualParameters = manualParameters;
    }
}
