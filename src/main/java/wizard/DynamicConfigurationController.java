package wizard;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.apache.jena.atlas.json.JsonArray;

public class DynamicConfigurationController {
    public VBox configVBox;
    public Label configureParamsLabel;

    @Inject
    private WizardData model;

    private JsonArray parameters;

    @FXML
    public void initialize() {
    }

    public void setParameters(JsonArray parameters) {
        this.parameters = parameters;

        // If the method is parameter-free, display it
        if (this.parameters.isEmpty()) {
            configureParamsLabel.setText("This is a parameter-free method!");
        }
    }
}
