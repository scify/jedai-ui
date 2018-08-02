package org.scify.jedai.gui.controllers.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import jfxtras.scene.control.ToggleGroupValue;
import org.scify.jedai.gui.nodes.dynamic_configuration.ConfigurationTypeSelector;
import org.scify.jedai.gui.utilities.DialogHelper;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.utilities.RadioButtonHelper;
import org.scify.jedai.gui.wizard.Submit;
import org.scify.jedai.gui.wizard.Validate;
import org.scify.jedai.gui.wizard.WizardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EntityClusteringController {
    public VBox dirtyErContainer;
    public VBox cleanCleanErContainer;
    public VBox confTypeContainer;

    private List<String> dirtyErOptions;
    private List<String> cleanCleanErOptions;
    ToggleGroupValue dirtyValue;
    ToggleGroupValue cleanCleanValue;
    private Logger log = LoggerFactory.getLogger(EntityClusteringController.class);

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Set available options for each ER type
        dirtyErOptions = Arrays.asList(
                JedaiOptions.CENTER_CLUSTERING,
                JedaiOptions.CONNECTED_COMPONENTS_CLUSTERING,
                JedaiOptions.CUT_CLUSTERING,
                JedaiOptions.MARKOV_CLUSTERING,
                JedaiOptions.MERGE_CENTER_CLUSTERING,
                JedaiOptions.RICOCHET_SR_CLUSTERING
        );

        cleanCleanErOptions = Collections.singletonList(
                JedaiOptions.UNIQUE_MAPPING_CLUSTERING
        );

        // Create radio buttons for each ER type
        dirtyValue = RadioButtonHelper.createButtonGroup(dirtyErContainer, dirtyErOptions, model.entityClusteringProperty());
        cleanCleanValue = RadioButtonHelper.createButtonGroup(cleanCleanErContainer, cleanCleanErOptions, model.entityClusteringProperty());

        // Enable the appropriate radio buttons depending on default ER type
        setErType(model.getErType());

        // Add listener to change the available methods depending on selected ER type
        model.erTypeProperty().addListener((observable, oldValue, newValue) -> setErType(newValue));

        // Add configuration type selection control
        confTypeContainer.getChildren().add(
                new ConfigurationTypeSelector(model.entityClusteringConfigTypeProperty())
        );
    }

    /**
     * Enable/disable the appropriate radio buttons for clustering methods available for each ER type
     *
     * @param erType Dirty or clean ER type
     */
    private void setErType(String erType) {
        boolean isDirty = erType.equals(JedaiOptions.DIRTY_ER);

        dirtyErContainer.getChildren().forEach(node -> node.setDisable(!isDirty));
        cleanCleanErContainer.getChildren().forEach(node -> node.setDisable(isDirty));

        model.entityClusteringProperty().unbindBidirectional(isDirty ? cleanCleanValue.valueProperty() : dirtyValue.valueProperty());
        model.entityClusteringProperty().bindBidirectional(isDirty ? dirtyValue.valueProperty() : cleanCleanValue.valueProperty());

        // Select first option
        model.setEntityClustering(isDirty ? JedaiOptions.CENTER_CLUSTERING : JedaiOptions.UNIQUE_MAPPING_CLUSTERING);
    }

    @Validate
    public boolean validate() {
        if (model.getEntityClustering() == null || model.getEntityClustering().isEmpty()) {
            DialogHelper.showError("Entity Clustering Method", "Missing Field",
                    "Selecting an Entity Clustering Method is required.");
            return false;
        }

        return true;
    }

    @Submit
    public void submit() {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 6");
        }
    }
}


