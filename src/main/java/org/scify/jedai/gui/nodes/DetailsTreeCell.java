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
    private final static List<Integer> openedPopups = new ArrayList<>();
    private final Hyperlink link;
    private final List<TreeItem<WorkflowResult>> workflowResultRows;
    private final List<WizardData> detailedRunData;
    private final Injector injector;

    public DetailsTreeCell(List<TreeItem<WorkflowResult>> workflowResultRows, List<WizardData> detailedRunData,
                           Injector injector) {
        this.workflowResultRows = workflowResultRows;
        this.detailedRunData = detailedRunData;
        this.injector = injector;

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
            Parent myParent = this.getParent();
            if (myParent instanceof TreeTableRow) {
                TreeTableRow myRow = (TreeTableRow) myParent;
                TreeItem treeItem = myRow.getTreeItem();
                if (!workflowResultRows.contains(treeItem)) {
                    // This row does not represent an entire workflow (only a step of it), but since we know the tree is
                    // up to 2 levels deep, we can get the parent of this tree item and find its index instead.
                    treeItem = treeItem.getParent();
                }

                // Find the index of the tree item that represents the entire workflow
                index = workflowResultRows.indexOf(treeItem);
            }

            String title = "Run #" + (index + 1) + " Detailed Configuration";

            // Open the popup if one for this index isn't already opened
            if (this.detailedRunData.size() > index && !openedPopups.contains(index)) {
                // Get the model for this run
                WizardData data = this.detailedRunData.get(index);

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

                // Add the index of this popup in the array list of opened popups
                openedPopups.add(index);

                // When the popup closes, remove its index from the opened list so it can be opened again
                int indexToRemove = index;
                dialog.setOnCloseRequest(event -> openedPopups.remove(Integer.valueOf(indexToRemove)));
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
