package org.scify.jedai.gui.nodes;

import javafx.scene.control.ListCell;
import org.scify.jedai.gui.model.JedaiMethodConfiguration;
import org.scify.jedai.gui.utilities.JedaiOptions;

/**
 * ListCell that has a JedaiMethodConfiguration object as its content, and displays whether it's enabled, and its
 * configuration.
 */
public class JedaiMethodConfigurationListCell extends ListCell<JedaiMethodConfiguration> {
    @Override
    protected void updateItem(JedaiMethodConfiguration item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
        } else {
            String representation = item.getName() + ": " + (item.isEnabled() ? "Enabled" : "Disabled")
                    + ", Configuration: " + item.getConfigurationType();

            if (item.getConfigurationType().equals(JedaiOptions.MANUAL_CONFIG)) {
                // Manual configuration selected, show parameters
                representation += ", Parameters: " + item.getManualParameters();
            }

            // Set the text
            setText(representation);
        }
    }
}
