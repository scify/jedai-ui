package wizard.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.RadioButtonHelper;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

import java.util.Arrays;
import java.util.List;

public class Step4Controller {
    public VBox radioBtnsContainer;
    private Logger log = LoggerFactory.getLogger(Step4Controller.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        List<String> options = Arrays.asList(
                "No cleaning",
                "Comparison Propagation",
                "Cardinality Edge Pruning (CEP)",
                "Cardinality Node Pruning (CNP)",
                "Weighed Edge Pruning (WEP)",
                "Weighed Node Pruning (WNP)",
                "Reciprocal Cardinality Node Pruning (ReCNP)",
                "Reciprocal Weighed Node Pruning (ReWNP)"
        );

        RadioButtonHelper.createButtonGroup(radioBtnsContainer, options, model.comparisonRefinementMethodProperty());
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
