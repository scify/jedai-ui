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
import utils.BlClMethodConfiguration;
import utils.BlockCleaningCustomComparator;
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

    private BlockCleaningCustomComparator listComparator;
    private Map<String, SimpleBooleanProperty> optionsMap;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Create comparator object that will be used for list sorting later
        listComparator = new BlockCleaningCustomComparator();

        // Initialize block cleaning methods list
        model.setBlockCleaningMethods(FXCollections.observableList(new ArrayList<>()));

        // Add BlClMethodConfiguration objects to the model
        model.getBlockCleaningMethods().addAll(
                new BlClMethodConfiguration(JedaiOptions.SIZE_BASED_BLOCK_PURGING),
                new BlClMethodConfiguration(JedaiOptions.COMPARISON_BASED_BLOCK_PURGING),
                new BlClMethodConfiguration(JedaiOptions.BLOCK_FILTERING)
        );

        // Create map with method names -> BlClMethodConfiguration objects and method names -> boolean properties
        Map<String, BlClMethodConfiguration> methodDetails = new HashMap<>();
        optionsMap = new HashMap<>();

        for (BlClMethodConfiguration bcmc : model.getBlockCleaningMethods()) {
            methodDetails.put(bcmc.getMethodName(), bcmc);
            optionsMap.put(bcmc.getMethodName(), bcmc.methodEnabledProperty());
        }

        /*
         * todo: change BlClMethodConfiguration object to use StringProperty for configuration type
         * todo: set configuration type with a ConfigurationTypeSelector (now that it's a string property)
         */

        // Add items to the list
        list.getItems().addAll(optionsMap.keySet());
        list.getItems().sort(listComparator);

        // Set list cells to have checkboxes which use the map's boolean values
        list.setCellFactory(CheckBoxListCell.forListView(optionsMap::get));

        // Listen for changes in each BooleanProperty
        for (String s : optionsMap.keySet()) {
            optionsMap.get(s).addListener((observable, oldValue, newValue) -> {
                // Add/remove the string to/from the model
                //todo: update for new object type
                if (newValue) {
//                    selectedList.getItems().add(s);
//                    model.getBlockCleaningMethods().add(s);
                } else {
//                    model.getBlockCleaningMethods().remove(s);
//                    selectedList.getItems().remove(s);
                }

                // Sort the list to the correct order using the custom comparator
                selectedList.getItems().sort(listComparator);
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
