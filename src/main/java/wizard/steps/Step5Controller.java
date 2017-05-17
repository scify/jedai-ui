package wizard.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    public VBox matchingMethodContainer;
    public VBox profileMatcherParameterContainer;
    public VBox representationModelContainer;
    public VBox similarityMetricContainer;
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

        // Create Representation model radio buttons
        List<String> representationModelOptions = Arrays.asList(
                JedaiOptions.CHARACTER_BIGRAMS,
                JedaiOptions.CHARACTER_FOURGRAMS,
                JedaiOptions.CHARACTER_TRIGRAMS,
                JedaiOptions.TOKEN_BIGRAMS,
                JedaiOptions.TOKEN_TRIGRAMS,
                JedaiOptions.TOKEN_UNIGRAMS,
                JedaiOptions._RADIO_BUTTON_SEPARATOR,
                JedaiOptions.TOKEN_BIGRAMS_TF_IDF,
                JedaiOptions.TOKEN_TRIGRAMS_TF_IDF,
                JedaiOptions.TOKEN_UNIGRAMS_TF_IDF,
                JedaiOptions._RADIO_BUTTON_SEPARATOR,
                JedaiOptions.CHARACTER_BIGRAM_GRAPHS,
                JedaiOptions.CHARACTER_FOURGRAM_GRAPHS,
                JedaiOptions.CHARACTER_TRIGRAM_GRAPHS,
                JedaiOptions.TOKEN_BIGRAM_GRAPHS,
                JedaiOptions.TOKEN_TRIGRAM_GRAPHS,
                JedaiOptions.TOKEN_UNIGRAM_GRAPHS
        );

        RadioButtonHelper.createButtonGroup(representationModelContainer, representationModelOptions, model.representationModelParamProperty());
    }

    @Validate
    public boolean validate() throws Exception {
        // todo: update validation of this step
        if (model.getEntityMatching() == null || model.getEntityMatching().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Entity Matching Method");
            alert.setHeaderText("Missing Field");
            alert.setContentText("Selecting an Entity Matching Method is required.");
            alert.showAndWait();
            return false;
        }

        if (model.getProfileMatcherParam() == null || model.getProfileMatcherParam().isEmpty()) {
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


