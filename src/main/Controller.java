package main;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Controller {
    @FXML
    private Button startBtn;
    
    private Button dataReadingPrevBtn;
    
    @FXML
    private void nextStepBtnHandler(ActionEvent event) throws IOException {
        Stage stage = null;
        Parent root = null;

        System.out.println("THIS FUNCTIONNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
        
        if (event.getSource() == startBtn) {
            // Go to Data Reading scene
            stage = (Stage)startBtn.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("layous/DataReading.fxml"));
        } else if (event.getSource() == startBtn) {
            //todo: other scene's buttons
        }
        
        // Create a new scene with root and set the stage
        if (root != null && stage != null) {
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }
    
    private void prevStepBtnHandler(ActionEvent event) throws IOException {
        Stage stage = null;
        Parent root = null;
        
        if (event.getSource() == dataReadingPrevBtn) {
            // Go to Intro scene
            stage = (Stage)dataReadingPrevBtn.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("layous/Introduction.fxml"));
        } else if (event.getSource() == dataReadingPrevBtn) {
            //todo: other scene's buttons
        }
        
        // Create a new scene with root and set the stage
        if (root != null && stage != null) {
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }
}
