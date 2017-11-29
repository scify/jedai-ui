package utils.dynamic_configuration.input;

import javafx.scene.control.ComboBox;

import java.util.List;

/**
 * Enumeration input.
 */
public class EnumerationInput extends ComboBox {
    /**
     * Enumeration input constructor
     *
     * @param parameterValues List with all parameters
     * @param index           Index of the parameter that the input should use.
     * @param enumName        Full name of the enumeration to show options for.
     * @param defaultValue    Default value.
     */
    public EnumerationInput(List<Object> parameterValues, int index, String enumName, String defaultValue) {
        try {
            // Create an instance of the given class
            Class<?> cls = Class.forName(enumName);

            // Check that the class is an enumeration
            if (cls.isEnum()) {
                // Get the values of the enumeration
                Object[] enumValues = cls.getEnumConstants();

                // Create combobox with the enum's values
                this.getItems().addAll(enumValues);

                // Add change listener to save the value when the selection changes
                this.valueProperty().addListener((observable, oldValue, newValue) -> {
                    // Save the value
                    parameterValues.set(index, newValue);
                });

                // If there is a default value specified, use it
                if (defaultValue.equals("-")) {
                    // No default value, select the 1st option
                    this.setValue(enumValues[0]);
                } else {
                    // If the given String contains the full value path (determined by whether or not it contains "."),
                    // keep only the last part, as that is what is shown in the combobox...
                    if (defaultValue.contains(".")) {
                        String[] splitDefValue = defaultValue.split("\\.");
                        defaultValue = splitDefValue[splitDefValue.length - 1];
                    }

                    // Find and select the default value in the enumeration values array
                    for (Object val : enumValues) {
                        if (defaultValue.equals(val.toString())) {
                            // Value found, set it as selected
                            this.setValue(val);
                            break;
                        }
                    }
                }
            } else {
                throw new UnsupportedOperationException("Type " + enumName + " is unknown, and is not an enumeration!");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
