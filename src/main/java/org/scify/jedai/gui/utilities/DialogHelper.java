package org.scify.jedai.gui.utilities;

import javafx.scene.control.Alert;

public class DialogHelper {
    /**
     * Show an error popup with customizable title, header & content text
     *
     * @param title   Title of error message
     * @param header  Header of error message
     * @param content Text of error message
     */
    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
