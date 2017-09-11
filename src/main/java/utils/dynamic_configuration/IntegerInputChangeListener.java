package utils.dynamic_configuration;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * Change Listener for the Dynamic Configuration fields of Integer type
 */
public class IntegerInputChangeListener implements ChangeListener<String> {
    private final TextField integerField;
    private final int min;
    private final int max;
    private final int index;
    private final List<Object> parameterValues;

    public IntegerInputChangeListener(TextField integerField, int min, int max, int index, List<Object> parameterValues) {
        this.integerField = integerField;
        this.min = min;
        this.max = max;
        this.index = index;
        this.parameterValues = parameterValues;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        // Check that only numbers have been entered
        if (!newValue.matches("\\d*")) {
            integerField.setText(newValue.replaceAll("[^\\d]", ""));
        } else {
            // Check if the number is out of the minimum/maximum bounds
            try {
                int intValue = Integer.parseInt(newValue);
                String newIntValue = newValue;

                if (intValue < min) {
                    // Less than minimum, set to the minimum
                    newIntValue = "" + min;
                    integerField.setText(newIntValue);
                } else if (intValue > max) {
                    // Exceeds the max, set it to the max
                    newIntValue = "" + max;
                    integerField.setText(newIntValue);
                }

                // Save the value
                parameterValues.set(index, newIntValue);
            } catch (NumberFormatException e) {
                if (newValue.length() == 0) {
                    integerField.setText("");
                } else {
                    // Set to maximum value
                    integerField.setText("" + max);
                }
            }
        }
    }
}
