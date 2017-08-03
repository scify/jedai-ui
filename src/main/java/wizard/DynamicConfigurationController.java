package wizard;

import com.google.inject.Inject;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DynamicConfigurationController {
    public Label configureParamsLabel;
    public GridPane configGrid;
    public Button saveBtn;

    @Inject
    private WizardData model;

    private JsonArray parameters;
    private ListProperty<Object> parametersProperty;
    private List<Object> parameterValues;

    @FXML
    public void initialize() {
        parameterValues = new ArrayList<>();
    }

    /**
     * Set the parameters to show in the configuration window, and create UI elements for the user to set them.
     *
     * @param parameters Parameters as specified by the JedAI library
     */
    public void setParameters(JsonArray parameters, ListProperty<Object> parametersProperty) {
        this.parameters = parameters;
        this.parametersProperty = parametersProperty;

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

        // Depending on what type the parameter is, get the appropriate control for it
        String paramType = param.get("class").getAsString().value();
        Node control = getNodeForType(index, paramType);

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
     * Create and return the node for setting the given parameter type
     *
     * @param index     Index of the parameter that the generated node is for
     * @param paramType Type of the parameter
     * @return Appropriate node for setting the parameter's value
     */
    private Node getNodeForType(int index, String paramType) {
        Node control = null;

        switch (paramType) {
            case "java.lang.Integer":
                parameterValues.add(-1);    // Add the initial value

                // Create integer controls
                TextField integerField = new TextField();
                integerField.textProperty().addListener((observable, oldValue, newValue) -> {
                    // Check that only numbers have been entered
                    if (!newValue.matches("\\d*")) {
                        integerField.setText(newValue.replaceAll("[^\\d]", ""));
                    } else {
                        // Save the value
                        parameterValues.set(index, newValue);
                    }
                });

                control = integerField;

                break;
            case "java.lang.Double":
                parameterValues.add(-1.0);

                // Create double controls
                TextField doubleField = new TextField();
                doubleField.textProperty().addListener((observable, oldValue, newValue) -> {
                    // Check that the number is a double
                    try {
                        //noinspection ResultOfMethodCallIgnored
                        double value = Double.parseDouble(newValue);

                        // Save the value
                        parameterValues.set(index, value);
                    } catch (NumberFormatException e) {
                        // Problem parsing, so not a double. Set previous value.
                        doubleField.setText(oldValue);
                    }
                });

                control = doubleField;

                break;
            case "java.lang.String":
                parameterValues.add("");

                // Create String controls
                TextField stringField = new TextField();

                // When the text field value changes, update the parameter in the list
                stringField.textProperty().addListener((observable, oldValue, newValue) -> parameterValues.set(index, newValue));

                control = stringField;

                break;
            case "java.lang.Boolean":
                parameterValues.add(false);

                // Create Boolean controls
                CheckBox checkBox = new CheckBox();

                // When the checkbox is toggled, update the parameter in the list
                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> parameterValues.set(index, newValue));

                control = checkBox;

                break;
            case "java.lang.Character":
                char defaultValue = ',';
                parameterValues.add(defaultValue);

                // Create String controls
                TextField charField = new TextField();

                // When the character field value changes, update the parameter in the list
                charField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.length() > 1) {
                        // Use only the last String character
                        char val = newValue.charAt(newValue.length() - 1);

                        charField.textProperty().setValue(String.valueOf(val));
                        parameterValues.set(index, val);
                    } else if (newValue.length() == 1) {
                        // Set the single character as the parameter value
                        parameterValues.set(index, newValue.charAt(0));
                    } else {
                        // Cannot be empty, set to the default value...
                        charField.textProperty().setValue(String.valueOf(defaultValue));
                        parameterValues.set(index, defaultValue);
                    }
                });

                control = charField;

                break;
            case "java.util.Set<Integer>":
                // Create Set instance
                Set<Integer> set = new HashSet<>();
                parameterValues.add(set);

                // Create the text field for numbers input
                TextField integerListField = new TextField();

                integerListField.textProperty().addListener((observable, oldValue, newValue) -> {
                    // Allow only numbers and commas in order to separate the numbers
                    String valuesString = newValue.replaceAll("[^\\d|,]", "");
                    integerListField.setText(valuesString);

                    // Empty the existing HashSet, and add updated values to it
                    set.clear();

                    String[] numbers = valuesString.split(",");
                    for (String sNum : numbers) {
                        // Only for non-empty strings
                        if (!sNum.isEmpty()) {
                            // Parse to integer, and add to the set
                            set.add(Integer.parseInt(sNum));
                        }
                    }
                });

                control = integerListField;

                break;
            case "java.util.Set<String>":
                parameterValues.add(null);
                control = new TextField();
                ((TextField) control).setBackground(new Background(new BackgroundFill(Paint.valueOf("#ff0000"), null, null)));
                //todo: implement this
                break;
            default:
                // If the type is an enumeration, create it and add radio buttons for it
                try {
                    Class<?> cls = Class.forName(paramType);

                    if (cls.isEnum()) {
                        Object[] enumValues = cls.getEnumConstants();

                        parameterValues.add(enumValues[0]); // Initialize the parameter value with the 1st enum constant

                        // Create combobox with the enum's values
                        ComboBox<Object> comboBox = new ComboBox<>(FXCollections.observableArrayList(enumValues));

                        // Add change listener to save the value when the selection changes
                        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                            // Save the value
                            parameterValues.set(index, newValue);
                        });

                        control = comboBox;
                    } else {
                        throw new UnsupportedOperationException("Type " + paramType + " is unknown, and is not an enumeration!");
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
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
