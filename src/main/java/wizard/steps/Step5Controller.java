package wizard.steps;

import EntityMatching.GroupLinkage;
import EntityMatching.ProfileMatcher;
import Utilities.IDocumentation;
import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.MethodConfiguration;
import utils.JedaiOptions;
import utils.RadioButtonHelper;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

import java.util.Arrays;
import java.util.List;

public class Step5Controller {
    public VBox matchingMethodContainer;
    public Button paramsBtn;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    private WizardData model;

    @Inject
    private Injector injector;

    @FXML
    public void initialize() {
        // Create options list for entity matching method
        List<String> entityMatchingOptions = Arrays.asList(
                JedaiOptions.GROUP_LINKAGE,
                JedaiOptions.PROFILE_MATCHER
        );

        // Create the radio buttons
        RadioButtonHelper.createButtonGroup(matchingMethodContainer, entityMatchingOptions, model.entityMatchingProperty());
    }

    @Validate
    public boolean validate() throws Exception {
        return true;
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 5");
        }
    }

    public void customizeParameters(ActionEvent actionEvent) {
        // Get the selected method
        IDocumentation method = (model.getEntityMatching().equals(JedaiOptions.GROUP_LINKAGE)) ?
                new GroupLinkage() : new ProfileMatcher();

        // Display the configuration window
        MethodConfiguration.displayModal(getClass(), injector, method, model.entityMatchingParametersProperty());
    }
}
