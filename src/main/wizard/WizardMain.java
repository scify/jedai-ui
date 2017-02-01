package main.wizard;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Wizard code structure from
 * https://github.com/bekwam/examples-javafx-repos1/tree/master/examples-javafx-parent/examples-javafx-wizard
 */
public class WizardMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        final Injector injector = Guice.createInjector(new WizardModule());

        final Parent p = FXMLLoader.load(WizardMain.class.getResource("/main/wizard-fxml/Wizard.fxml"),
                null,
                new JavaFXBuilderFactory(),
                injector::getInstance
        );

        final Scene scene = new Scene(p);

        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setTitle("JedAI");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
