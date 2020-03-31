package org.scify.jedai.gui.nodes.dynamic_configuration.input;

import javafx.scene.control.TextField;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.List;

/**
 * Text Field for Double input.
 */
public class DoubleInput extends TextField {
    /**
     * Double input constructor.
     *
     * @param parameterValues List with all parameters
     * @param index           Index of the parameter that the input should use.
     * @param defaultValue    Default value to use. Expected to be properly formatted double.
     * @param minValue        Minimum allowed value. Expected to be properly formatted double.
     * @param maxValue        Maximum allowed value. Expected to be properly formatted double.
     */
    public DoubleInput(List<MutablePair<String, Object>> parameterValues, int index, String defaultValue, String minValue, String maxValue) {
        // Parse the min/max values
        double min;
        if (!minValue.equals("-")) {
            min = Double.parseDouble(minValue);
        } else {
            min = Double.MIN_VALUE;
        }

        double max;
        if (!maxValue.equals("-")) {
            max = Double.parseDouble(maxValue);
        } else {
            max = Double.MAX_VALUE;
        }

        // Set default value
        if (!defaultValue.equals("-")) {
            this.textProperty().setValue(defaultValue);
            parameterValues.get(index).setRight(Double.parseDouble(defaultValue));
        }

        // Add change listener
        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // Act only when focus is lost
            if (!newValue) {
                String inputValue = this.textProperty().getValue();

                // Check the input
                try {
                    double value = Double.parseDouble(inputValue);

                    // Make sure the number is within the min/max values
                    if (value < min) {
                        value = min;
                        this.textProperty().setValue("" + min);
                    } else if (value > max) {
                        value = max;
                        this.textProperty().setValue("" + max);
                    }

                    // Save the value to the parameters list
                    parameterValues.get(index).setRight(value);
                } catch (NumberFormatException e) {
                    // Problem parsing, so not a double. Set previous value.
                    this.setText(defaultValue);
                    parameterValues.get(index).setRight(Double.parseDouble(defaultValue));
                }
            }
        });
    }
}
