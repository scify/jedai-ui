package utils;

import javafx.beans.property.SimpleBooleanProperty;

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
    private SimpleBooleanProperty methodEnabled;
    private String methodName;
    private String configurationType;
    private List<JPair<String, Object>> manualParameters;

    public BlClMethodConfiguration(String methodName) {
        this.methodName = methodName;
        this.methodEnabled = new SimpleBooleanProperty(false);
    }

    public boolean isMethodEnabled() {
        return methodEnabled.get();
    }

    public SimpleBooleanProperty methodEnabledProperty() {
        return methodEnabled;
    }

    public void setMethodEnabled(boolean methodEnabled) {
        this.methodEnabled.set(methodEnabled);
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getConfigurationType() {
        return configurationType;
    }

    public void setConfigurationType(String configurationType) {
        this.configurationType = configurationType;
    }

    public List<JPair<String, Object>> getManualParameters() {
        return manualParameters;
    }

    public void setManualParameters(List<JPair<String, Object>> manualParameters) {
        this.manualParameters = manualParameters;
    }
}
