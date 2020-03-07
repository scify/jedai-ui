package org.scify.jedai.gui.utilities;

import javafx.beans.property.StringProperty;
import javafx.scene.Parent;

public class WorkflowStep {
    private final String label;
    private final String description;
    private final String fxmlPath;
    private final StringProperty configProperty;
    private Parent node;

    public WorkflowStep(String label, String description, String fxmlPath, StringProperty configProperty) {
        this.label = label;
        this.description = description;
        this.fxmlPath = fxmlPath;
        this.configProperty = configProperty;
    }

    public WorkflowStep(String label, String description, String fxmlPath) {
        this.label = label;
        this.description = description;
        this.fxmlPath = fxmlPath;
        this.configProperty = null;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public boolean hasConfigProperty() {
        return (configProperty != null);
    }

    public String getConfigProperty() {
        if (configProperty != null)
            return configProperty.get();
        else
            return null;
    }

    public StringProperty configPropertyProperty() {
        return configProperty;
    }

    public Parent getNode() {
        return node;
    }

    public void setNode(Parent node) {
        this.node = node;
    }
}
