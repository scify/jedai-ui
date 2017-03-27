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
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Initialize block cleaning methods list
        model.setBlockCleaningMethods(FXCollections.observableList(new ArrayList<>()));

        // Create map with options
        Map<String, SimpleBooleanProperty> map = new HashMap<>();
        map.put(JedaiOptions.SIZE_BASED_BLOCK_PURGING, new SimpleBooleanProperty(false));
        map.put(JedaiOptions.COMPARISON_BASED_BLOCK_PURGING, new SimpleBooleanProperty(false));
        map.put(JedaiOptions.BLOCK_FILTERING, new SimpleBooleanProperty(false));
        map.put(JedaiOptions.BLOCK_SCHEDULING, new SimpleBooleanProperty(false));

        // Add items to the list
        list.getItems().addAll(map.keySet());

        // Set list cells to have checkboxes which use the map's boolean values
        list.setCellFactory(CheckBoxListCell.forListView(map::get));

        // Listen for changes in each BooleanProperty
        for (String s : map.keySet()) {
            map.get(s).addListener((observable, oldValue, newValue) -> {
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
            for (String method : map.keySet()) {
                map.get(method).setValue(
                        newValue.contains(method)
                );
            }
        });
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
