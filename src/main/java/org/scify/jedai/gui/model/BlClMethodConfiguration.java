package org.scify.jedai.gui.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import org.scify.jedai.gui.utilities.JPair;
import org.scify.jedai.gui.utilities.JedaiOptions;

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

    /**
     * Constructor that gets this item's properties from another BlClMethodConfiguration object
     *
     * @param source Source BlClMethodConfiguration object
     */
    public BlClMethodConfiguration(BlClMethodConfiguration source) {
        this.name = source.getName();
        this.enabled = new SimpleBooleanProperty(source.isEnabled());
        this.configurationType = new SimpleStringProperty(source.getConfigurationType());
        this.manualParameters = new SimpleListProperty<>(source.getManualParameters());
    }

    /**
     * Reset the values of this method configuration
     */
    public void reset() {
        this.setEnabled(false);
        this.setManualParameters(new SimpleListProperty<>());
        this.setConfigurationType(JedaiOptions.DEFAULT_CONFIG);
    }

    @Override
    public String toString() {
        return "BlClMethodConfiguration{" +
                "enabled=" + enabled +
                ", name='" + name + '\'' +
                ", configurationType=" + configurationType +
                ", manualParameters=" + manualParameters +
                '}';
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
