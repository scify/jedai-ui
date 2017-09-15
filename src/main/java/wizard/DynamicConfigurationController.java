package wizard;

import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import utils.dynamic_configuration.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DynamicConfigurationController {
    public Label configureParamsLabel;
    public GridPane configGrid;
    public Button saveBtn;

    private JsonArray jsonParamDescriptions;
    private ListProperty<Object> parametersProperty;
    private List<Object> parameterValues;

    @FXML
    public void initialize() {
        parameterValues = new ArrayList<>();
    }

    /**
     * Set the parameters to show in the configuration window, and create UI elements for the user to set them.
     *
     * @param jsonParamDescriptions Parameters as specified by the JedAI library
     */
    public void setParameters(JsonArray jsonParamDescriptions, ListProperty<Object> parametersProperty) {
        this.jsonParamDescriptions = jsonParamDescriptions;
        this.parametersProperty = parametersProperty;

        // Determine if there are, and we should use previously set values for the parameters
        ObservableList<Object> prevParams = parametersProperty.get();

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
                    // If we have previous values for the parameters, modify the JSON object's default value
                    if (usePrevParams) {
                        jsonParam.getAsObject().put("defaultValue", prevParams.get(gridRows).toString());
                    }

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
        // If the label is "File Path", make the type "JEDAI_FILEPATH", in order to show file selection controls
        String paramType = param.get("name").getAsString().value().equals("File Path") ? "JEDAI_FILEPATH" : param.get("class").getAsString().value();

        String defaultValue = param.get("defaultValue").getAsString().value();
        String minValue = param.get("minValue").getAsString().value();
        String maxValue = param.get("maxValue").getAsString().value();

        Node control = getNodeForType(index, paramType, defaultValue, minValue, maxValue);

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
     * @param index        Index of the parameter that the generated node is for
     * @param paramType    Type of the parameter
     * @param defaultValue Default value for the generated control
     * @param minValue     Minimum allowed value for the input
     * @param maxValue     Maximum allowed value for the input
     * @return Appropriate node for setting the parameter's value
     */
    private Node getNodeForType(int index, String paramType, String defaultValue, String minValue, String maxValue) {
        Node control = null;
        //todo: use default/min/max values for all types

        switch (paramType) {
            case "JEDAI_FILEPATH":
                parameterValues.add("");    // Add initial value

                control = new FileSelectorInput(parameterValues, index, configGrid);
                break;
            case "java.lang.Integer":
                parameterValues.add(-1);    // Add the initial value

                control = new IntegerInput(parameterValues, index, defaultValue, minValue, maxValue);
                break;
            case "java.lang.Double":
                parameterValues.add(-1.0);

                control = new DoubleInput(parameterValues, index, defaultValue, minValue, maxValue);
                break;
            case "java.lang.String":
                parameterValues.add("");

                control = new StringInput(parameterValues, index, defaultValue);
                break;
            case "java.lang.Boolean":
                parameterValues.add(false);

                control = new BooleanInput(parameterValues, index, defaultValue);

                break;
            case "java.lang.Character":
                parameterValues.add(',');

                control = new CharacterInput(parameterValues, index, defaultValue);
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
                // Create Set instance
                Set<String> stringSet = new HashSet<>();
                parameterValues.add(stringSet);

                // Create the list container with an add button
                VBox stringVBox = new VBox();

                // Create the editable list
                ObservableList<String> stringList = FXCollections.observableArrayList();

                ListView<String> simpleList = new ListView<>(stringList);
                simpleList.setEditable(true);
                simpleList.setCellFactory(TextFieldListCell.forListView());

                simpleList.setOnEditCommit(t -> simpleList.getItems().set(t.getIndex(), t.getNewValue()));

                // Create the add/remove item buttons
                Button addBtn = new Button("Add");
                addBtn.onActionProperty().setValue(event -> stringList.add("(double click to edit)"));

                Button removeBtn = new Button("Remove last");
                removeBtn.onActionProperty().setValue(event -> {
                    ObservableList<String> list = simpleList.getItems();

                    // Only try to remove if list contains at least 1 item
                    if (list.size() > 0) {
                        list.remove(list.size() - 1);
                    }
                });

                HBox addRemoveHBox = new HBox();
                addRemoveHBox.setSpacing(5);
                addRemoveHBox.setAlignment(Pos.CENTER);
                addRemoveHBox.getChildren().add(addBtn);
                addRemoveHBox.getChildren().add(removeBtn);

                // Add a listener to the string list, so when it changes we update the Set
                simpleList.getItems().addListener((ListChangeListener<String>) c -> {
                    // Clear the current set's contents, and add the new ones
                    stringSet.clear();
                    stringSet.addAll(simpleList.getItems());
                });

                // Add buttons to the container, and return the container
                stringVBox.setSpacing(5);
                stringVBox.getChildren().add(simpleList);
                stringVBox.getChildren().add(addRemoveHBox);

                control = stringVBox;

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
