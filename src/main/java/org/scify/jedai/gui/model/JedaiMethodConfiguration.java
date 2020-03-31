package org.scify.jedai.gui.model;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.tuple.MutablePair;
import org.scify.jedai.gui.utilities.JedaiOptions;

/**
 * Block Cleaning Method configuration object.
 * Contains:
 * - Method name
 * - Whether the method is enabled or not
 * - The selected configuration type for this method
 * - For manual configuration, the selected parameters
 */
public class JedaiMethodConfiguration {
    private SimpleBooleanProperty enabled;
    private String name;
    private StringProperty configurationType;
    private ListProperty<MutablePair<String, Object>> manualParameters;

    public JedaiMethodConfiguration(String methodName) {
        this.name = methodName;

        // Initialize properties
        this.enabled = new SimpleBooleanProperty(false);
        this.configurationType = new SimpleStringProperty();
        this.manualParameters = new SimpleListProperty<>();
    }

    /**
     * Constructor that gets this item's properties from another JedaiMethodConfiguration object
     *
     * @param source Source JedaiMethodConfiguration object
     */
    public JedaiMethodConfiguration(JedaiMethodConfiguration source) {
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
        return "JedaiMethodConfiguration{" +
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

    public ObservableList<MutablePair<String, Object>> getManualParameters() {
        return manualParameters.get();
    }

    public ListProperty<MutablePair<String, Object>> manualParametersProperty() {
        return manualParameters;
    }

    public void setManualParameters(ObservableList<MutablePair<String, Object>> manualParameters) {
        this.manualParameters.set(manualParameters);
    }
}
