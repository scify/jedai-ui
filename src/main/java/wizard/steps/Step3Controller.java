package wizard.steps;

import com.google.inject.Inject;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        // Create map with options
        Map<String, ObservableValue<Boolean>> map = new HashMap<>();
        map.put("Block Filtering", new SimpleBooleanProperty(false));
        map.put("Block Scheduling", new SimpleBooleanProperty(false));
        map.put("Size-based Block Purging", new SimpleBooleanProperty(false));
        map.put("Comparison-based Block Purging", new SimpleBooleanProperty(false));

        // Add items to the list
        list.getItems().addAll(map.keySet());

        // Set list cells to have checkboxes which use the map's boolean values
        list.setCellFactory(CheckBoxListCell.forListView(map::get));

        // Listen for changes in each BooleanProperty
        for (String s : map.keySet()) {
            map.get(s).addListener((observable, oldValue, newValue) -> {
                // If observable list has not been initialized, create it
                if (model.getBlockProcessingMethods() == null) {
                    model.setBlockProcessingMethods(FXCollections.observableList(new ArrayList<>()));
                }

                // Add/remove the string to/from the model
                if (newValue) {
                    selectedList.getItems().add(s);
                    model.getBlockProcessingMethods().add(s);
                } else {
                    model.getBlockProcessingMethods().remove(s);
                    selectedList.getItems().remove(s);
                }
            });
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
