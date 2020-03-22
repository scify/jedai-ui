package org.scify.jedai.gui.controllers.steps;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.utilities.RadioButtonHelper;
import org.scify.jedai.gui.wizard.WizardData;

import java.util.Arrays;
import java.util.List;

public class SimilarityJoinController {
    public VBox containerVBox;
    public VBox methodsVBox;
    public TextField dataset1AttrField;
    public TextField dataset2AttrField;
    public VBox dataset2VBox;

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
                JedaiOptions.PP_JOIN
        );

        // Create the radio button group
        RadioButtonHelper.createButtonGroup(
                methodsVBox, similarityJoinOptions, model.similarityJoinProperty()
        );

        model.similarityJoinProperty().addListener((observable, oldValue, newValue) -> {
            // Reset manual parameters, because otherwise it can cause the UI to try to load a double as an int to
            // try to "edit" the previously selected parameters, which won't work.
            model.setSimilarityJoinParameters(null);
        });

        // Only show the 2nd attribute name input when clean-clean ER is selected
        dataset2VBox.visibleProperty().bind(model.erTypeProperty().isEqualTo(JedaiOptions.CLEAN_CLEAN_ER));

        // Bind the text field values to the model properties
        dataset1AttrField.textProperty().bindBidirectional(model.dataset1AttributeProperty());
        dataset2AttrField.textProperty().bindBidirectional(model.dataset2AttributeProperty());
    }
}
