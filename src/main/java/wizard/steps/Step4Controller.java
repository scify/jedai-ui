package wizard.steps;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

public class Step4Controller {
    public VBox containerVBox;
    public ComboBox<String> methodCombobox;
    private Logger log = LoggerFactory.getLogger(Step4Controller.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        // Add options to combobox
        ObservableList<String> comboboxOptions =
                FXCollections.observableArrayList(
                        "Comparison Propagation",
                        "Cardinality Edge Pruning (CEP)",
                        "Cardinality Node Pruning (CNP)",
                        "Weighed Edge Pruning (WEP)",
                        "Weighed Node Pruning (WNP)",
                        "Reciprocal Cardinality Node Pruning (ReCNP)",
                        "Reciprocal Weighed Node Pruning (ReWNP)"
                );

        methodCombobox.setItems(comboboxOptions);

        // Bind combobox selection to model
        methodCombobox.valueProperty().bindBidirectional(model.comparisonRefinementMethodProperty());
    }

    @Validate
    public boolean validate() throws Exception {
        return true;
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 4");
        }
    }
}
