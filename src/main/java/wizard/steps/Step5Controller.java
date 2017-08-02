package wizard.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import jfxtras.scene.control.ToggleGroupValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.JedaiOptions;
import utils.RadioButtonHelper;
import wizard.MethodMapping;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Step5Controller {
    public VBox matchingMethodContainer;
    public VBox representationModelContainer;
    public VBox similarityMetricContainer;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    private
    WizardData model;

    @FXML
    public void initialize() {
        // Create options list for entity matching method
        List<String> entityMatchingOptions = Arrays.asList(
                JedaiOptions.GROUP_LINKAGE,
                JedaiOptions.PROFILE_MATCHER
        );

        // Create options list Representation model radio buttons
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

        // Create options list for Similarity Metric radio button
        List<String> similarityMethodOptions = Arrays.asList(
                JedaiOptions.ARCS_SIMILARITY,
                JedaiOptions.COSINE_SIMILARITY,
                JedaiOptions.ENHANCED_JACCARD_SIMILARITY,
                JedaiOptions.GENERALIZED_JACCARD_SIMILARITY,
                JedaiOptions.GRAPH_CONTAINMENT_SIMILARITY,
                JedaiOptions.GRAPH_NORMALIZED_VALUE_SIMILARITY,
                JedaiOptions.GRAPH_VALUE_SIMILARITY,
                JedaiOptions.GRAPH_OVERALL_SIMILARITY,
                JedaiOptions.JACCARD_SIMILARITY,
                JedaiOptions.SIGMA_SIMILARITY,
                JedaiOptions.WEIGHTED_JACCARD_SIMILARITY
        );

        // Manually create the radio buttons so we can disable some of them later at will
        ToggleGroupValue groupValue = new ToggleGroupValue();
        ToggleGroup btnsGroup = new ToggleGroup();
        Map<String, RadioButton> radioBtns = new HashMap<>();

        for (String s : similarityMethodOptions) {
            // Create radio button
            RadioButton btn = new RadioButton(s);
            btn.setUserData(s);
            btn.setToggleGroup(btnsGroup);

            // Add button to container, group value and map
            similarityMetricContainer.getChildren().add(btn);
            groupValue.add(btn, btn.getUserData());
            radioBtns.put(s, btn);
        }

        // Select first method by default
        groupValue.setValue(similarityMethodOptions.get(0));

        // Bind the group's selection to the model
        model.similarityMethodProperty().bindBidirectional(groupValue.valueProperty());

        // Add listener to enable/disable compatible similarity metric buttons depending on representation model
        model.representationModelProperty().addListener((observable, oldValue, newValue) -> {
            // Get options that are compatible with the selected representation
            List<String> compatibleOptions = MethodMapping.getAvailableMetricsForRepresentationModel(newValue);

            // Enable or disable the radio buttons
            boolean selectFirst = false;
            for (RadioButton btn : radioBtns.values()) {
                btn.setDisable(!compatibleOptions.contains((String) btn.getUserData()));
                if (btn.isDisabled() && btn.isSelected()) {
                    selectFirst = true;
                }
            }

            // If the previously selected button is now disabled, select the first non-disabled radio button
            if (selectFirst) {
                for (String s : similarityMethodOptions) {
                    RadioButton btn = radioBtns.get(s);

                    if (!btn.isDisabled()) {
                        btn.setSelected(true);
                        break;
                    }
                }
            }
        });

        // Create the rest of the radio buttons
        RadioButtonHelper.createButtonGroup(matchingMethodContainer, entityMatchingOptions, model.entityMatchingProperty());
        RadioButtonHelper.createButtonGroup(representationModelContainer, representationModelOptions, model.representationModelProperty());
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
}
