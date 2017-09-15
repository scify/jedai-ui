package utils.dynamic_configuration;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class FileSelectorInput extends HBox {
    private static File previousFolder = null;

    /**
     * @param parameterValues List of parameters
     * @param index           Index that this input should use to set its value in the parameters list
     * @param windowNode      A Node that will be used for getting the window to show the FileChooser
     * @param defaultValue    (Optional) Path to a previously-selected file for this input
     */
    public FileSelectorInput(List<Object> parameterValues, int index, Node windowNode, String defaultValue) {
        this.setSpacing(5.0);

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
            File file = fileChooser.showOpenDialog(windowNode.getScene().getWindow());

            if (file != null) {
                fileField.textProperty().setValue(file.getAbsolutePath());
                // Save the file's directory to remember it if the FileChooser is opened again
                previousFolder = file.getParentFile();
            }
        });

        // Add nodes to HBox
        this.getChildren().addAll(fileField, browseBtn);

        // If there is a default value, use it
        if (!defaultValue.equals("-")) {
            // Set the value to the text field, which will then update the parameterValues list
            fileField.setText(defaultValue);
        }
    }
}
