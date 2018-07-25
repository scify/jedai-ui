package org.scify.jedai.gui.nodes.dynamic_configuration;

import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationTypeSelectorHorizontal extends ConfigurationTypeSelector {
    public ConfigurationTypeSelectorHorizontal(StringProperty modelProperty) {
        super(modelProperty);

        // Set preferred height of element
        this.setSpacing(0);
        this.setPrefHeight(22);

        // Get a list of the children
        List<Node> prevChildren = new ArrayList<>(this.getChildren());

        // Remove them from the VBox
        this.getChildren().clear();

        // Create HBox with the selection items
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(prevChildren);
        hBox.setSpacing(5f);

        // Add the HBox to the VBox
        this.getChildren().add(hBox);
    }
}
