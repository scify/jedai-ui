package org.scify.jedai.gui.nodes.dynamic_configuration.input;

import javafx.scene.control.TextField;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.List;

/**
 * Text Field for String input, which accepts a default value, and adds the current value to a list.
 */
public class StringInput extends TextField {
    /**
     * String input constructor.
     *
     * @param parameterValues List with all parameters
     * @param index           Index of the parameter that the input should use.
     * @param defaultValue    Default value to use.
     */
    public StringInput(List<MutablePair<String, Object>> parameterValues, int index, String defaultValue) {
        // Set default value
        if (!defaultValue.equals("-")) {
            this.setText(defaultValue);

            parameterValues.get(index).setRight(defaultValue);
        }

        // When the text field value changes, update the parameter in the list
        this.textProperty().addListener((observable, oldValue, newValue)
                -> parameterValues.get(index).setRight(defaultValue));
    }
}
