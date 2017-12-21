package wizard;

import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import utils.JPair;
import utils.dynamic_configuration.HelpTooltip;
import utils.dynamic_configuration.input.*;

import java.util.ArrayList;
import java.util.List;

public class DynamicConfigurationController {
    public Label configureParamsLabel;
    public GridPane configGrid;
    public Button saveBtn;

    private JsonArray jsonParamDescriptions;
    private ListProperty<JPair<String, Object>> parametersProperty;
    private List<JPair<String, Object>> parameterValues;

    @FXML
    public void initialize() {
        parameterValues = new ArrayList<>();
    }

    /**
     * Set the parameters to show in the configuration window, and create UI elements for the user to set them.
     *
     * @param jsonParamDescriptions Parameters as specified by the JedAI library
     */
    public void setParameters(JsonArray jsonParamDescriptions, ListProperty<JPair<String, Object>> parametersProperty) {
        this.jsonParamDescriptions = jsonParamDescriptions;
        this.parametersProperty = parametersProperty;

        // Determine if there are, and we should use previously set values for the parameters
        ObservableList<JPair<String, Object>> prevParams = parametersProperty.get();

        boolean usePrevParams = false;
        if (prevParams != null && !prevParams.isEmpty() && prevParams.size() == jsonParamDescriptions.size()) {
            // We can use the previously set parameters for this
            usePrevParams = true;
        }

        // If the method is parameter-free, display it
        if (this.jsonParamDescriptions.isEmpty()) {
            configureParamsLabel.setText("This is a parameter-free method!");
        } else {
            // Generate the form to configure the method
            int gridRows = 0;
            for (JsonValue jsonParam : this.jsonParamDescriptions) {
                if (jsonParam.isObject()) {
                    JsonObject jsonParamObj = jsonParam.getAsObject();

                    // If we have previous values for the parameters, modify the JSON object's default value
                    if (usePrevParams) {
                        // Save original default value to another parameter, in order to be able to display it
                        JsonValue originalDefault = jsonParamObj.get("defaultValue");
                        if (originalDefault != null) {
                            jsonParamObj.put("defaultValueOriginal", originalDefault);
                        }

                        // Put as the new defaultValue the previous one
                        jsonParamObj.put("defaultValue", prevParams.get(gridRows).getRight().toString());
                    }

                    // Create controls for setting this parameter
                    addParameterControls(jsonParamObj, gridRows++);
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
        // Get name of parameter & create label for it
        String name = param.get("name").getAsString().value();
        Label label = new Label(name);

        // Depending on what type the parameter is, get the appropriate control for it
        // If the label is "File Path", make the type "JEDAI_FILEPATH", in order to show file selection controls
        String paramType = name.equals("File Path") ? "JEDAI_FILEPATH" : param.get("class").getAsString().value();

        String defaultValue = param.get("defaultValue").getAsString().value();
        String minValue = param.get("minValue").getAsString().value();
        String maxValue = param.get("maxValue").getAsString().value();

        Node control = getNodeForType(index, name, paramType, defaultValue, minValue, maxValue);

        // Create description text and add it to a HelpTooltip node
        String description = param.get("description").getAsString().value();

        // Check if there are any minimum/maximum or default values for the parameter
        String minMaxDefDescription = "\n";

        if (!minValue.equals("-")) {
            minMaxDefDescription += "\nMinimum value: " + minValue;
        }

        if (!maxValue.equals("-")) {
            minMaxDefDescription += "\nMaximum value: " + maxValue;
        }

        // Get true default value
        if (param.hasKey("defaultValueOriginal")) {
            defaultValue = param.get("defaultValueOriginal").getAsString().value();
        }

        if (!defaultValue.equals("-")) {
            minMaxDefDescription += "\nDefault value: " + defaultValue;
        }

        // If there is any of the min/max/default values, add the string to the description
        if (minMaxDefDescription.length() > 1) {
            description += minMaxDefDescription;
        }

        HelpTooltip help = new HelpTooltip(description);

        // Add controls to the grid
        configGrid.addRow(index, label, control, help);
    }

    /**
     * Create and return the node for setting the given parameter type
     *
     * @param index        Index of the parameter that the generated node is for
     * @param paramType    Type of the parameter
     * @param defaultValue Default value for the generated control
     * @param minValue     Minimum allowed value for the input
     * @param maxValue     Maximum allowed value for the input
     * @return Appropriate node for setting the parameter's value
     */
    private Node getNodeForType(int index, String name, String paramType, String defaultValue, String minValue, String maxValue) {
        Node control;

        switch (paramType) {
            case "JEDAI_FILEPATH":
                parameterValues.add(new JPair<>(name, ""));

                control = new FileSelectorInput(parameterValues, index, configGrid, defaultValue);
                break;
            case "java.lang.Integer":
                parameterValues.add(new JPair<>(name, -1));

                control = new IntegerInput(parameterValues, index, defaultValue, minValue, maxValue);
                break;
            case "java.lang.Double":
                parameterValues.add(new JPair<>(name, -1.0));

                control = new DoubleInput(parameterValues, index, defaultValue, minValue, maxValue);
                break;
            case "java.lang.String":
                parameterValues.add(new JPair<>(name, ""));

                control = new StringInput(parameterValues, index, defaultValue);
                break;
            case "java.lang.Boolean":
                parameterValues.add(new JPair<>(name, false));

                control = new BooleanInput(parameterValues, index, defaultValue);
                break;
            case "java.lang.Character":
                parameterValues.add(new JPair<>(name, ','));

                control = new CharacterInput(parameterValues, index, defaultValue);
                break;
            case "java.util.Set<Integer>":
                parameterValues.add(new JPair<>(name, null));

                control = new IntegerListInput(parameterValues, index, defaultValue);
                break;
            case "java.util.Set<String>":
                parameterValues.add(new JPair<>(name, null));

                control = new StringListInput(parameterValues, index, defaultValue);
                break;
            default:
                parameterValues.add(new JPair<>(name, null));

                control = new EnumerationInput(parameterValues, index, paramType, defaultValue);
                break;
        }

        return control;
    }

    /**
     * Get the parameter values from the generated forms, save them to the model and close the window.
     *
     * @param actionEvent Button event
     */
    public void saveBtnHandler(ActionEvent actionEvent) {
        // Save the parameters to the model
        parametersProperty.setValue(FXCollections.observableList(parameterValues));

        // Get a handle to the stage and close it
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }
}
