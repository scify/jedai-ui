package wizard.steps;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

public class Step3Controller {
    public VBox containerVBox;
    public ListView<String> list;
    public ListView<String> selectedList;
    public Button myBtn;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Create lists for selection
        list.setItems(FXCollections.observableArrayList(
                "Block Filtering",
                "Block Scheduling",
                "Size-based Block Purging",
                "Comparison-based Block Purging"
        ));

        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        list.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            selectedList.setItems(list.getSelectionModel().getSelectedItems());
        });

        // Set selected values as selected in the list, if they exist in model
        if (model.getBlockProcessingMethods() != null && !model.getBlockProcessingMethods().isEmpty()) {
            for (String selectedValue : model.getBlockProcessingMethods()) {
                list.getSelectionModel().select(selectedValue);
            }
        }

        // When the selection changes, add the methods to the model
        selectedList.itemsProperty().addListener((obs, ov, nv) -> {
            model.setBlockProcessingMethods(selectedList.getItems());
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
