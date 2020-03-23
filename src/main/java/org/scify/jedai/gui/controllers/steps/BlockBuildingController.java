package org.scify.jedai.gui.controllers.steps;

import com.google.inject.Inject;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import org.scify.jedai.gui.model.JedaiMethodConfiguration;
import org.scify.jedai.gui.nodes.dynamic_configuration.ConfigurationTypeSelector;
import org.scify.jedai.gui.nodes.dynamic_configuration.ConfigurationTypeSelectorHorizontal;
import org.scify.jedai.gui.utilities.BlockBuildingComparator;
import org.scify.jedai.gui.utilities.DialogHelper;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.wizard.Submit;
import org.scify.jedai.gui.wizard.Validate;
import org.scify.jedai.gui.wizard.WizardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class BlockBuildingController {
    public VBox containerVBox;
    public VBox methodConfContainer;
    public ListView<String> list;
    private Logger log = LoggerFactory.getLogger(BlockBuildingController.class);

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Initialize block building methods list
        model.setBlockBuildingMethods(FXCollections.observableArrayList());

        // Create List with options
        List<String> options = Arrays.asList(
                JedaiOptions.STANDARD_TOKEN_BUILDING,
                JedaiOptions.SORTED_NEIGHBORHOOD,
                JedaiOptions.SORTED_NEIGHBORHOOD_EXTENDED,
                JedaiOptions.Q_GRAMS_BLOCKING,
                JedaiOptions.Q_GRAMS_BLOCKING_EXTENDED,
                JedaiOptions.SUFFIX_ARRAYS_BLOCKING,
                JedaiOptions.SUFFIX_ARRAYS_BLOCKING_EXTENDED,
                JedaiOptions.LSH_SUPERBIT_BLOCKING,
                JedaiOptions.LSH_MINHASH_BLOCKING
        );

        // For each option, add a JedaiMethodConfiguration object to the model
        for (String s : options) {
            model.getBlockBuildingMethods().add(new JedaiMethodConfiguration(s));
        }

        // Enable the 1st method by default
        model.getBlockBuildingMethods().get(0).setEnabled(true);

        // Create map with method names -> boolean properties and add ConfigurationTypeSelectors
        Map<String, SimpleBooleanProperty> optionsMap = new HashMap<>();

        List<JedaiMethodConfiguration> blBuMethods = model.getBlockBuildingMethods();
        for (JedaiMethodConfiguration mConf : blBuMethods) {
            // Add to options HashMap
            optionsMap.put(mConf.getName(), mConf.enabledProperty());

            // Add configuration type selector for this method
            ConfigurationTypeSelector cts = new ConfigurationTypeSelectorHorizontal(mConf.configurationTypeProperty());
            cts.bindEnabled(mConf.enabledProperty());

            methodConfContainer.getChildren().add(cts);

            // Add separators between selectors
            if (blBuMethods.indexOf(mConf) < blBuMethods.size() - 1) {
                methodConfContainer.getChildren().add(new Separator());
            }
        }

        // Add items to the list
        list.getItems().addAll(optionsMap.keySet());
        list.getItems().sort(new BlockBuildingComparator());

        // Set list cells to have checkboxes which use the map's boolean values
        list.setCellFactory(CheckBoxListCell.forListView(optionsMap::get));
    }

    @Validate
    public boolean validate() {
        if (model.getWorkflow().equals(JedaiOptions.WORKFLOW_BLOCKING_BASED)) {
            // For blocking-based workflow, we need at least one enabled method
            for (JedaiMethodConfiguration mc : model.getBlockBuildingMethods()) {
                if (mc.isEnabled()) {
                    return true;
                }
            }

            // Show error and do not continue
            DialogHelper.showError("Error", "No method selected!",
                    "You must select at least one method for Block Building!");
            return false;
        } else {
            // Progressive workflow can have no selected block building methods.
            return true;
        }
    }

    @Submit
    public void submit() {
        // Count number of enabled block building methods
        int enabledMethods = 0;
        for (JedaiMethodConfiguration mc : model.getBlockBuildingMethods()) {
            if (mc.isEnabled()) {
                enabledMethods++;
            }
        }

        // Set the number of enabled block building methods in the model
        model.setEnabledBlockBuildingMethods(enabledMethods);

        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 2");
        }
    }
}
