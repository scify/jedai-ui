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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

import java.io.File;
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
    private static File previousFolder = null;

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

                // Create HBox with text field + Browse button
                HBox hBox = new HBox();
                hBox.setSpacing(5.0);

                TextField fileField = new TextField();
                fileField.textProperty().addListener((observable, oldValue, newValue) -> parameterValues.set(index, newValue));

                Button browseBtn = new Button("Browse");

                // Setup the button action
                FileChooser fileChooser = new FileChooser();
                browseBtn.onActionProperty().setValue(event -> {
                    // Open file chooser
                    if (previousFolder != null) {
                        fileChooser.setInitialDirectory(previousFolder);
                    }
                    File file = fileChooser.showOpenDialog(configGrid.getScene().getWindow());

                    if (file != null) {
                        fileField.textProperty().setValue(file.getAbsolutePath());
                        // Save the file's directory to remember it if the FileChooser is opened again
                        previousFolder = file.getParentFile();
                    }
                });

                // Add nodes to HBox, and set it as the control
                hBox.getChildren().addAll(fileField, browseBtn);
                control = hBox;
                break;
            case "java.lang.Integer":
                parameterValues.add(-1);    // Add the initial value

                // Create integer controls
                TextField integerField = new TextField();

                // Get minimum and maximum Integer values
                int min;
                if (!minValue.equals("-")) {
                    // Use minimum from parameters
                    min = Integer.parseInt(minValue);
                } else {
                    min = Integer.MIN_VALUE;
                }

                int max;
                if (!maxValue.equals("-")) {
                    // Use maximum from parameters
                    max = Integer.parseInt(maxValue);
                } else {
                    max = Integer.MAX_VALUE;
                }


                // Set default value
                if (!defaultValue.equals("-")) {
                    integerField.textProperty().setValue(defaultValue);
                }

                // Add change listener to restrict to numbers input only
                integerField.textProperty().addListener((observable, oldValue, newValue) -> {
                    // Check that only numbers have been entered
                    if (!newValue.matches("\\d*")) {
                        integerField.setText(newValue.replaceAll("[^\\d]", ""));
                    } else {
                        // Check if the number is out of the minimum/maximum bounds
                        try {
                            int intValue = Integer.parseInt(newValue);
                            if (intValue < min) {
                                // Less than minimum, set to the minimum
                                integerField.setText("" + min);
                            } else if (intValue > max) {
                                // Exceeds the max, set it to the max
                                integerField.setText("" + max);
                            }

                            // Save the value
                            parameterValues.set(index, newValue);
                        } catch (NumberFormatException e) {
                            if (newValue.length() == 0) {
                                integerField.setText("");
                            } else {
                                // Set to maximum value
                                integerField.setText("" + max);
                            }
                        }

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
                char defaultChar = ',';
                parameterValues.add(defaultChar);

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
                        charField.textProperty().setValue(String.valueOf(defaultChar));
                        parameterValues.set(index, defaultChar);
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
