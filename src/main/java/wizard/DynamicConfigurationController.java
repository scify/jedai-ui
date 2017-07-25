package wizard;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.apache.jena.atlas.json.JsonArray;

public class DynamicConfigurationController {
    public VBox configVBox;

    @Inject
    private WizardData model;

    private JsonArray parameters;

    @FXML
    public void initialize() {
    }

    public void setParameters(JsonArray parameters) {
        this.parameters = parameters;
    }
}
