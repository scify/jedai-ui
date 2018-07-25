package org.scify.jedai.gui.nodes.dynamic_configuration;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.utilities.RadioButtonHelper;

import java.util.List;

public class ConfigurationTypeSelector extends VBox {
    public ConfigurationTypeSelector(StringProperty modelProperty) {
        this.setSpacing(5.);

        // Create the radio buttons
        List<String> buttons = FXCollections.observableArrayList(
                JedaiOptions.DEFAULT_CONFIG,
                JedaiOptions.AUTOMATIC_CONFIG,
                JedaiOptions.MANUAL_CONFIG
        );

        RadioButtonHelper.createButtonGroup(this, buttons, modelProperty);
    }

    /**
     * Enable or disable the configuration selector by binding its disable property to a custom boolean property.
     *
     * @param booleanProperty Boolean property to bind to
     */
    public void bindEnabled(BooleanProperty booleanProperty) {
        this.disableProperty().bind(booleanProperty.not());
    }
}
