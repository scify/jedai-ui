package utils.dynamic_configuration;

import javafx.scene.control.TextField;

import java.util.List;

/**
 * Text Field for Integer input.
 */
public class IntegerInput extends TextField {
    public IntegerInput(List<Object> parameterValues, int index, String defaultValue, String minValue, String maxValue) {
        // Get minimum and maximum Integer values
        int min;
        if (!minValue.equals("-")) {
            // Use minimum from parameters
            min = Integer.parseInt(minValue);
        } else {
            min = Integer.MIN_VALUE;
        }

        int max;
        if (!maxValue.equals("-")) {
            // Use maximum from parameters
            max = Integer.parseInt(maxValue);
        } else {
            max = Integer.MAX_VALUE;
        }


        // Set default value
        if (!defaultValue.equals("-")) {
            this.textProperty().setValue(defaultValue);
            parameterValues.set(index, Integer.parseInt(defaultValue));
        }

        // Add change listener to restrict to numbers input only
        this.textProperty().addListener(
                new IntegerInputChangeListener(this, min, max, index, parameterValues)
        );
    }
}
