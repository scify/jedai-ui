package wizard;

import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

public class DynamicConfigurationController {
    public Label configureParamsLabel;
    public GridPane configGrid;
    public Button saveBtn;

    @Inject
    private WizardData model;

    private JsonArray parameters;

    @FXML
    public void initialize() {
    }

    /**
     * Set the parameters to show in the configuration window, and create UI elements for the user to set them.
     *
     * @param parameters Parameters as specified by the JedAI library
     */
    public void setParameters(JsonArray parameters) {
        this.parameters = parameters;

        // If the method is parameter-free, display it
        if (this.parameters.isEmpty()) {
            configureParamsLabel.setText("This is a parameter-free method!");
        } else {
            // Generate the form to configure the method
            int gridRows = 0;
            for (JsonValue jsonParam : this.parameters) {
                if (jsonParam.isObject()) {
                    // Create controls for setting this parameter
                    addParameterControls(jsonParam.getAsObject(), gridRows++);
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
     * @param index Grid row to add the controls to.
     */
    private void addParameterControls(JsonObject param, int index) {
        // Create the label
        Label label = new Label(param.get("name").getAsString().value());

        // Depending on what type the parameter is, create the appropriate control for it
        Node control = null;
        String paramType = param.get("class").getAsString().value();

        switch (paramType) {
            case "java.lang.Integer":
                // Create integer controls
                TextField integerField = new TextField();
                integerField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*")) {
                        integerField.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                });

                control = integerField;

                break;
            case "java.lang.Double":
                // Create double controls
                TextField doubleField = new TextField();
                doubleField.textProperty().addListener((observable, oldValue, newValue) -> {
                    try {
                        //noinspection ResultOfMethodCallIgnored
                        Double.parseDouble(newValue);
                    } catch (NumberFormatException e) {
                        // Problem parsing, not a double
                        doubleField.setText(oldValue);
                    }
                });

                control = doubleField;

                break;
            default:
                // If the type is an enumeration, create it and add radio buttons for it
                try {
                    Class<?> cls = Class.forName(paramType);

                    if (cls.isEnum()) {
                        // Create combobox with the enum's values
                        control = new ComboBox<>(FXCollections.observableArrayList(cls.getEnumConstants()));
                    } else {
                        throw new UnsupportedOperationException("Type " + paramType + " is unknown, and is not an enumeration!");
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
        }

        // Add text area with parameter description
        TextArea descriptionArea = new TextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setMaxWidth(200);
        descriptionArea.textProperty().setValue(param.get("description").getAsString().value());

        // Add controls to the grid
        configGrid.addRow(index, label, control, descriptionArea);
    }

    /**
     * Get the parameter values from the generated forms, save them to the model and close the window.
     *
     * @param actionEvent Button event
     */
    public void saveBtnHandler(ActionEvent actionEvent) {
        //todo
    }
}
