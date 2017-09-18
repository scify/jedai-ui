package utils.dynamic_configuration;

import javafx.scene.control.TextField;

import java.util.List;

/**
 * Text Field for Integer input.
 */
public class IntegerInput extends TextField {
    /**
     * Integer input constructor
     *
     * @param parameterValues List with all parameters
     * @param index           Index of the parameter that the input should use
     * @param defaultValue    Default value to use. Expected to be properly formatted integer.
     * @param minValue        Minimum allowed value. Expected to be properly formatted integer.
     * @param maxValue        Maximum allowed value. Expected to be properly formatted integer.
     */
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

        // Add listener for checking the input when focus is lost
        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // Get the text field's text
            String currStrValue = this.getText();

            // Try parsing the String
            try {
                int intValue = Integer.parseInt(currStrValue);

                // Check if the number is out of the minimum/maximum bounds
                if (intValue < min) {
                    // Less than minimum, set to the minimum
                    currStrValue = "" + min;
                    this.setText(currStrValue);

                    parameterValues.set(index, min);
                } else if (intValue > max) {
                    // Exceeds the max, set it to the max
                    currStrValue = "" + max;
                    this.setText(currStrValue);

                    parameterValues.set(index, max);
                }

                // Save the value
                parameterValues.set(index, currStrValue);
            } catch (NumberFormatException e) {
                if (currStrValue.length() == 0) {
                    this.setText("0");
                    parameterValues.set(index, 0);
                } else {
                    // Set to maximum value
                    this.setText("" + max);
                    parameterValues.set(index, max);
                }
            }
        });
    }
}
