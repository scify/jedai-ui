package org.scify.jedai.gui.nodes.dynamic_configuration.input;

import javafx.scene.control.CheckBox;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.List;

public class BooleanInput extends CheckBox {
    /**
     * Boolean input constructor
     *
     * @param parameterValues List of all parameter values
     * @param index           Index of the parameter that the input should use.
     * @param defaultValue    Default value
     */
    public BooleanInput(List<MutablePair<String, Object>> parameterValues, int index, String defaultValue) {
        // Use the default value
        boolean defaultBool;
        try {
            defaultBool = Boolean.parseBoolean(defaultValue);
        } catch (Exception e) {
            defaultBool = false;
        }

        this.setSelected(defaultBool);
        parameterValues.get(index).setRight(defaultBool);

        // When the checkbox is toggled, update the parameter in the list
        this.selectedProperty().addListener((observable, oldValue, newValue)
                -> parameterValues.get(index).setRight(newValue));
    }
}
