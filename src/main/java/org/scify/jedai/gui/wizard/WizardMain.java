package org.scify.jedai.gui.wizard;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Wizard code structure from
 * https://github.com/bekwam/examples-javafx-repos1/tree/master/examples-javafx-parent/examples-javafx-wizard
 */
public class WizardMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        final Injector injector = Guice.createInjector(new WizardModule());

        final URL fxml = WizardMain.class.getClassLoader().getResource("wizard-fxml/Wizard.fxml");

        if (fxml != null) {
            final Parent p = FXMLLoader.load(fxml,
                    null,
                    new JavaFXBuilderFactory(),
                    injector::getInstance
            );

            final Scene scene = new Scene(p);

            primaryStage.setScene(scene);
            primaryStage.setWidth(800);
            primaryStage.setHeight(600);
            primaryStage.setTitle("Java gEneric DAta Integration (JedAI) Toolkit");

            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
