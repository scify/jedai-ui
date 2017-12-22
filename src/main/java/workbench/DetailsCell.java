package workbench;

import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.stage.Stage;
import wizard.WizardData;
import wizard.steps.CompletedController;
import wizard.steps.ConfirmController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for showing a Table cell with a link to details for a Workflow run in the Workbench.
 * Based on: http://stackoverflow.com/a/38290874
 */
public class DetailsCell<T> extends TableCell<T, Void> {
    private final Hyperlink link;
    private final List<WizardData> detailedRunData;
    private final List<WizardData> openedPopups;

    public DetailsCell(List<WizardData> detailedRunData, Injector injector) {
        this.detailedRunData = detailedRunData;
        this.openedPopups = new ArrayList<>();

        // Add hyperlink and click action
        link = new Hyperlink("View");
        link.setOnAction(evt -> {
            // Get run data for this button
            int index = this.getIndex();
            String title = "Run #" + (index + 1) + " Detailed Configuration";

            if (this.detailedRunData.size() > index) {
                // Get the model for this run
                WizardData data = this.detailedRunData.get(index);

                // Only open a new popup if another one for this data is not open
                if (!openedPopups.contains(data)) {
                    // Load FXML for the popup window
                    Parent root;
                    FXMLLoader loader = new FXMLLoader(
                            CompletedController.class.getClassLoader().getResource("wizard-fxml/steps/Confirm.fxml"),
                            null,
                            new JavaFXBuilderFactory(),
                            injector::getInstance);
                    try {
                        root = loader.load();
                        root.getProperties().put("controller", loader.getController());

                        // Set the controller's model to the data one
                        T controller = loader.getController();
                        if (controller instanceof ConfirmController) {
                            // Set the data to the controller
                            ConfirmController confirmController = (ConfirmController) controller;
                            confirmController.setModel(data, title);
                        }

                        // Show the popup window
                        Stage dialog = new Stage();
                        dialog.setScene(new Scene(root));
                        dialog.setTitle(title);

                        dialog.show();

                        // Add the data of this popup in the array list of opened popups
                        openedPopups.add(data);

                        // When the popup closes, remove its data from the opened list so it can be opened again
                        dialog.setOnCloseRequest(event -> openedPopups.remove(data));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(empty ? null : link);
    }
}
