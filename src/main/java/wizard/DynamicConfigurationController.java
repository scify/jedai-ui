package wizard;

import com.google.inject.Inject;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

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
        } else {
            // Generate the form to configure the method
            for (JsonValue jsonParam : this.parameters) {
                if (jsonParam.isObject()) {
                    // Create controls for setting this parameter
                    addParameterControls(jsonParam.getAsObject());
                } else if (jsonParam.isArray()) {
                    throw new UnsupportedOperationException("Cannot handle JSON array yet (?)");
                }
            }
        }
    }

    /**
     * Create the appropriate controls to set a method parameter's value, based on the given JSON specification for it.
     *
     * @param param The parameter to create controls for.
     */
    private void addParameterControls(JsonObject param) {
        // Create new HBox for this parameter's controls
        HBox hBox = new HBox();
        ObservableList<Node> hBoxChildren = hBox.getChildren();

        // Add label to the HBox
        hBoxChildren.add(new Label(param.get("name").toString()));

        // Depending on what type the parameter is, add the appropriate control for it
        String paramType = param.get("class").toString();
        switch (paramType) {
            case "java.lang.Integer":
                //todo: Create integer controls
                break;
            case "java.lang.Double":
                //todo: Create double controls
                break;
            default:
                //todo: Assuming it's an enumeration, add enum controls
        }

        // Add text area with parameter description
        TextArea descriptionArea = new TextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setMaxWidth(200);
        descriptionArea.textProperty().setValue(param.get("description").toString());

        hBoxChildren.add(descriptionArea);

        configVBox.getChildren().add(hBox);
    }
}
