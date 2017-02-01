package main.wizard.popup;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import main.wizard.WizardData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockProcessingController {
    public ListView<String> list;
    public ListView<String> selectedList;
    public Button saveBtn;
    private Logger log = LoggerFactory.getLogger(BlockProcessingController.class);

    private WizardData model;

    @FXML
    public void initialize() {
        System.out.println("i am initialized");

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
    }

    public void saveBtnHandler(ActionEvent actionEvent) {
        // Set the model's value to be the selected items from the list
        model.blockProcessingMethodsProperty().setValue(list.getSelectionModel().getSelectedItems());

        // todo: close this
    }

    /**
     * Set the data model. Also, because it is called right after loading the controller, checks if there are any values
     * that should be shown as selected on the list (in case the popup opens a 2nd time after having selected items)
     *
     * @param model The model to set
     */
    public void setModel(WizardData model) {
        this.model = model;

        // Set selected values as selected in the list, if they exist in model
        if (model.getBlockProcessingMethods() != null && !model.getBlockProcessingMethods().isEmpty()) {
            for (String selectedValue : model.getBlockProcessingMethods()) {
                list.getSelectionModel().select(selectedValue);
            }
        }
    }
}
