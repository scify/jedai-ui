package utils;

import javafx.beans.property.StringProperty;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import jfxtras.scene.control.ToggleGroupValue;

import java.util.List;

public class RadioButtonHelper {
    /**
     * Create a radio button for each String in the list, add them to the container, and bind the selected value to the
     * String property
     *
     * @param container     Container to put radio buttons in
     * @param radioBtns     List of Strings, one for each radio button
     * @param modelProperty Property to bind selected radio button's value to
     */
    public static ToggleGroupValue createButtonGroup(Pane container, List<String> radioBtns, StringProperty modelProperty) {
        // Create new ToggleGroup and ToggleGroupValue for the radio buttons
        ToggleGroupValue groupValue = new ToggleGroupValue();
        ToggleGroup btnsGroup = new ToggleGroup();

        // Create a radio button for each option
        for (String s : radioBtns) {
            // Create radio button for this option
            RadioButton radioBtn = new RadioButton(s);
            radioBtn.setUserData(s);
            radioBtn.setToggleGroup(btnsGroup);

            // Add to RadioButton to the container pane and the ToggleGroupValue
            container.getChildren().add(radioBtn);
            groupValue.add(radioBtn, radioBtn.getUserData());
        }

        // Bind toggle group value to model
        modelProperty.bindBidirectional(groupValue.valueProperty());

        // Select first option
        groupValue.setValue(radioBtns.get(0));

        return groupValue;
    }
}
