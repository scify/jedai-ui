package wizard.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JedaiOptions;
import utils.RadioButtonHelper;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

import java.util.Arrays;
import java.util.List;

public class Step5Controller {
    public TextArea pMatcherTextArea;
    public VBox matchingMethodContainer;
    public VBox profileMatcherParameterContainer;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        // Create radio buttons for entity matching method
        List<String> entityMatchingOptions = Arrays.asList(
                JedaiOptions.GROUP_LINKAGE,
                JedaiOptions.PROFILE_MATCHER
        );

        RadioButtonHelper.createButtonGroup(matchingMethodContainer, entityMatchingOptions, model.entityMatchingProperty());

        // Create radio buttons for profile matcher parameter
        List<String> profileMatcherOptions = Arrays.asList(
                JedaiOptions.REPRESENTATION,
                JedaiOptions.SIMILARITY
        );

        RadioButtonHelper.createButtonGroup(profileMatcherParameterContainer, profileMatcherOptions, model.profileMatcherParamProperty());

        // Show profile matcher parameter controls only when Profile Matcher is selected
        profileMatcherParameterContainer.visibleProperty()
                .bind(model.entityMatchingProperty().isEqualTo(JedaiOptions.PROFILE_MATCHER));
        pMatcherTextArea.visibleProperty()
                .bind(model.entityMatchingProperty().isEqualTo(JedaiOptions.PROFILE_MATCHER));

    }

    @Validate
    public boolean validate() throws Exception {
        if (model.getEntityMatching() == null || model.getEntityMatching().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Entity Matching Method");
            alert.setHeaderText("Missing Field");
            alert.setContentText("Selecting an Entity Matching Method is required.");
            alert.showAndWait();
            return false;
        }

        if (model.getEntityMatching().equals(JedaiOptions.PROFILE_MATCHER) &&
                (model.getProfileMatcherParam() == null || model.getProfileMatcherParam().isEmpty())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Profile Matcher Parameter");
            alert.setHeaderText("Missing Field");
            alert.setContentText("When using Profile Matcher for Entity Matching, selecting a parameter for it is required.");
            alert.showAndWait();
            return false;
        }

        return true;
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 5");
        }
    }
}


