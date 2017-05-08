package wizard.steps;

import com.google.inject.Inject;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JedaiOptions;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Step3Controller {
    public VBox containerVBox;
    public ListView<String> list;
    public ListView<String> selectedList;

    private Map<String, SimpleBooleanProperty> optionsMap;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Initialize block cleaning methods list
        model.setBlockCleaningMethods(FXCollections.observableList(new ArrayList<>()));

        // Create map with options
        optionsMap = new HashMap<>();
        optionsMap.put(JedaiOptions.SIZE_BASED_BLOCK_PURGING, new SimpleBooleanProperty(false));
        optionsMap.put(JedaiOptions.COMPARISON_BASED_BLOCK_PURGING, new SimpleBooleanProperty(false));
        optionsMap.put(JedaiOptions.BLOCK_FILTERING, new SimpleBooleanProperty(false));
        optionsMap.put(JedaiOptions.BLOCK_SCHEDULING, new SimpleBooleanProperty(false));

        // Add items to the list
        list.getItems().addAll(optionsMap.keySet());
        updateOptions(model.getErType());

        // Set list cells to have checkboxes which use the map's boolean values
        list.setCellFactory(CheckBoxListCell.forListView(optionsMap::get));

        // Listen for changes in each BooleanProperty
        for (String s : optionsMap.keySet()) {
            optionsMap.get(s).addListener((observable, oldValue, newValue) -> {
                // Add/remove the string to/from the model
                if (newValue) {
                    selectedList.getItems().add(s);
                    model.getBlockCleaningMethods().add(s);
                } else {
                    model.getBlockCleaningMethods().remove(s);
                    selectedList.getItems().remove(s);
                }
            });
        }

        // Listen for changes in the model, and change the values of the boolean properties
        model.blockCleaningMethodsProperty().addListener((observable, oldValue, newValue) -> {
            // Set the value of each checkbox to true or false depending on if it's in the list or not
            for (String method : optionsMap.keySet()) {
                optionsMap.get(method).setValue(
                        newValue.contains(method)
                );
            }
        });

        // Listen for ER type changes, and remove Block Scheduling in Dirty ER
        model.erTypeProperty().addListener((observable, oldValue, newValue) -> updateOptions(newValue));
    }

    /**
     * Update the available Block Cleaning methods, depending on which ER type is selected
     *
     * @param erType
     */
    private void updateOptions(String erType) {
        // Check which ER method is selected
        if (erType.equals(JedaiOptions.DIRTY_ER)) {
            // Remove it from the list for Dirty ER
            list.getItems().remove(JedaiOptions.BLOCK_SCHEDULING);

            // Set block scheduling to deselected
            optionsMap.get(JedaiOptions.BLOCK_SCHEDULING).setValue(false);
        } else {
            // For Clean Clean ER, add block scheduling to the list of options
            if (!list.getItems().contains(JedaiOptions.BLOCK_SCHEDULING)) {
                list.getItems().add(JedaiOptions.BLOCK_SCHEDULING);
            }
        }
    }

    @Validate
    public boolean validate() throws Exception {
        return true;
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 3");
        }
    }
}
