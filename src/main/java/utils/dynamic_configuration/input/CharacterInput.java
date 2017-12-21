package utils.dynamic_configuration.input;

import javafx.scene.control.TextField;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.List;

/**
 * Text Field for single-character input.
 */
public class CharacterInput extends TextField {
    /**
     * Character Input constructor.
     *
     * @param parameterValues List with all parameters
     * @param index           Index of the parameter that the input should use.
     * @param defaultValue    Default value to use.
     */
    public CharacterInput(List<MutablePair<String, Object>> parameterValues, int index, String defaultValue) {
        // Get default character
        char defaultChar;
        if (!defaultValue.equals("-") && defaultValue.length() == 1) {
            // Make the string a character
            defaultChar = defaultValue.charAt(0);
        } else {
            defaultChar = ',';
        }

        // Save the new default value to the list and display it in the text field
        parameterValues.get(index).setRight(defaultChar);
        this.setText(String.valueOf(defaultChar));

        // When the character field value changes, update the parameter in the list
        this.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 1) {
                // Use only the last String character
                char val = newValue.charAt(newValue.length() - 1);

                this.textProperty().setValue(String.valueOf(val));
                parameterValues.get(index).setRight(val);
            } else if (newValue.length() == 1) {
                // Set the single character as the parameter value
                parameterValues.get(index).setRight(newValue.charAt(0));
            } else {
                // Cannot be empty, set to the default value...
                this.textProperty().setValue(String.valueOf(defaultChar));
                parameterValues.get(index).setRight(defaultChar);
            }
        });
    }
}
