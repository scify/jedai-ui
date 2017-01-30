package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private int step;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("layouts/Introduction.fxml"));
        primaryStage.setTitle("JedAI");
        primaryStage.setScene(new Scene(root, 768, 480));
        primaryStage.show();
        this.step = 0;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
