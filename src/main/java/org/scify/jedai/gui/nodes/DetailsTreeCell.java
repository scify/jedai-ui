package org.scify.jedai.gui.nodes;

import com.google.inject.Injector;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;
import javafx.stage.Stage;
import org.scify.jedai.gui.controllers.steps.ConfirmController;
import org.scify.jedai.gui.model.WorkflowResult;
import org.scify.jedai.gui.utilities.DialogHelper;
import org.scify.jedai.gui.wizard.WizardData;

import java.util.ArrayList;
import java.util.List;

public class DetailsTreeCell extends TreeTableCell<WorkflowResult, String> {
    private final Hyperlink link;
    private final List<TreeItem<WorkflowResult>> workflowResultRows;
    private final List<WizardData> detailedRunData;
    private final List<WizardData> openedPopups;
    private final Injector injector;

    public DetailsTreeCell(List<TreeItem<WorkflowResult>> workflowResultRows, List<WizardData> detailedRunData,
                           Injector injector) {
        this.workflowResultRows = workflowResultRows;
        this.detailedRunData = detailedRunData;
        this.injector = injector;
        this.openedPopups = new ArrayList<>();

        this.link = createLink();
    }

    /**
     * Create a JavaFX Hyperlink that when clicked, shows the detailed configuration for a workflow.
     *
     * @return The described link
     */
    private Hyperlink createLink() {
        Hyperlink link = new Hyperlink("View");

        link.setOnAction(evt -> {
            // Get the TreeTableRow for this item
            int index = Integer.MAX_VALUE;
            Parent p = this.getParent();
            if (p instanceof TreeTableRow) {
                TreeTableRow parent = (TreeTableRow) p;
                TreeItem parentItem = parent.getTreeItem();
                if (workflowResultRows.contains(parentItem)) {
                    // This TreeItem represents a whole workflow's results, so we can show the popup. Find the index
                    // by checking the position of this TreeItem against the root TreeItem's children.
                    index = workflowResultRows.indexOf(parentItem);
                }
            }

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

        return link;
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : link);
    }
}
