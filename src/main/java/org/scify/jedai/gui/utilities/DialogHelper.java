package org.scify.jedai.gui.utilities;

import com.google.inject.Injector;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class DialogHelper {
    /**
     * Show an error popup with customizable title, header & content text
     *
     * @param title   Title of error message
     * @param header  Header of error message
     * @param content Text of error message
     */
    public static void showError(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /**
     * Load an FXML file and return the Parent it was loaded in
     *
     * @param clClass  Class to use for getting the class loader
     * @param injector Injector of the app
     * @param fxmlPath Path to the FXML file to load
     * @return Parent with loaded FXML
     */
    public static Parent loadFxml(Class clClass, Injector injector, String fxmlPath) {
        Parent root;
        FXMLLoader loader = new FXMLLoader(
                clClass.getClassLoader().getResource(fxmlPath),
                null,
                new JavaFXBuilderFactory(),
                injector::getInstance
        );

        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        root.getProperties().put("controller", loader.getController());
        return root;
    }

    /**
     * Show a dialog with the given Parent, modality and title. Can show or show and wait.
     *
     * @param root     Parent with loaded FXML
     * @param modality Modality of the dialog
     * @param wait     If true, will show the dialog and wait to return
     * @param title    Window title of dialog
     * @return Stage of the loaded dialog, in case it's needed
     */
    public static Stage showScene(Parent root, Modality modality, boolean wait, String title) {
        Stage dialog = new Stage();
        dialog.setScene(new Scene(root));

        // Set title
        dialog.setTitle(title);

        // Add modality (if given)
        if (modality != null) {
            dialog.initModality(modality);
        }

        // Show dialog
        if (wait) {
            dialog.showAndWait();
        } else {
            dialog.show();
        }

        return dialog;
    }
}
