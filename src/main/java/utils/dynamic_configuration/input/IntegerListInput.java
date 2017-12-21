package utils.dynamic_configuration.input;

import com.google.common.base.Joiner;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Integer list input
 */
public class IntegerListInput extends TextField {
    private final Set<Integer> set;

    /**
     * Integer list constructor.
     *
     * @param parameterValues List with all parameters
     * @param index           Index of the parameter that the input should use.
     * @param defaultValue    Default value for the list. Expected to be in the form "[1, 2, 3]" etc.
     */
    public IntegerListInput(List<MutablePair<String, Object>> parameterValues, int index, String defaultValue) {
        // Create HashSet instance
        set = new HashSet<>();
        parameterValues.get(index).setRight(set);

        // Set default value, if available
        if (!defaultValue.equals("-")) {
            // Remove brackets from the string and split it into numbers
            String[] numberStrings = defaultValue
                    .replace('[', ' ')
                    .replace(']', ' ')
                    .split(",");

            // Parse the numbers to add them to the set
            for (String s : numberStrings) {
                try {
                    // Trim the string, parse as integer and add it to the set
                    set.add(Integer.parseInt(s.trim()));
                } catch (NumberFormatException e) {
                    // Ignoring this "number"
                }
            }

            // Set the text field's value to the numbers, joined with ','
            this.setText(Joiner.on(',').join(set));
        }

        // Add change listener to update the HashSet
        this.textProperty().addListener((observable, oldValue, newValue) -> {
            // Allow only numbers and commas in order to separate the numbers
            String valuesString = newValue.replaceAll("[^\\d|,]", "");
            this.setText(valuesString);

            // Empty the existing HashSet, and add updated values to it
            set.clear();

            String[] numbers = valuesString.split(",");
            for (String sNum : numbers) {
                // Only for non-empty strings
                if (!sNum.isEmpty()) {
                    // Parse to integer, and add to the set
                    set.add(Integer.parseInt(sNum));
                }
            }
        });
    }
}
