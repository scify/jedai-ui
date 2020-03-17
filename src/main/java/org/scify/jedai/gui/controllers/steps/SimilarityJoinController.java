package org.scify.jedai.gui.controllers.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.utilities.RadioButtonHelper;
import org.scify.jedai.gui.wizard.WizardData;

import java.util.Arrays;
import java.util.List;

public class SimilarityJoinController {
    public VBox containerVBox;
    public VBox methodsVBox;

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Create list with options
        List<String> similarityJoinOptions = Arrays.asList(
                JedaiOptions.ALL_PAIRS_CHAR_BASED,
                JedaiOptions.ALL_PAIRS_TOKEN_BASED,
                JedaiOptions.FAST_SS,
                JedaiOptions.PASS_JOIN,
                JedaiOptions.PP_JOIN,
                JedaiOptions.TOP_K
        );

        // Create the radio button group
        RadioButtonHelper.createButtonGroup(
                methodsVBox, similarityJoinOptions, model.similarityJoinProperty()
        );
    }
}
