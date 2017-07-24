package wizard;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class DynamicConfigurationController {
    public VBox configVBox;

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
    }
}
