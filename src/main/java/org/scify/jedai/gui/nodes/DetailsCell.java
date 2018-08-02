package org.scify.jedai.gui.nodes;

import com.google.inject.Injector;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.stage.Stage;
import org.scify.jedai.gui.controllers.steps.ConfirmController;
import org.scify.jedai.gui.utilities.DialogHelper;
import org.scify.jedai.gui.wizard.WizardData;

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
                    Parent root = DialogHelper.loadFxml(this.getClass(), injector,
                            "wizard-fxml/steps/Confirm.fxml");

                    // Get the controller
                    Object controller = null;
                    if (root != null) {
                        controller = root.getProperties().get("controller");
                    }

                    // Set the controller's model to the data one
                    if (controller instanceof ConfirmController) {
                        // Set the data to the controller
                        ConfirmController confirmController = (ConfirmController) controller;
                        confirmController.setModel(data, title);
                    }

                    // Show the popup window
                    Stage dialog = DialogHelper.showScene(root, null, false, title);

                    // Add the data of this popup in the array list of opened popups
                    openedPopups.add(data);

                    // When the popup closes, remove its data from the opened list so it can be opened again
                    dialog.setOnCloseRequest(event -> openedPopups.remove(data));
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
