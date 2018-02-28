package org.scify.jedai.gui.utilities;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

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
    private ListProperty<JPair<String, Object>> manualParameters;

    public BlClMethodConfiguration(String methodName) {
        this.name = methodName;

        // Initialize properties
        this.enabled = new SimpleBooleanProperty(false);
        this.configurationType = new SimpleStringProperty();
        this.manualParameters = new SimpleListProperty<>();
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

    public ObservableList<JPair<String, Object>> getManualParameters() {
        return manualParameters.get();
    }

    public ListProperty<JPair<String, Object>> manualParametersProperty() {
        return manualParameters;
    }

    public void setManualParameters(ObservableList<JPair<String, Object>> manualParameters) {
        this.manualParameters.set(manualParameters);
    }
}
