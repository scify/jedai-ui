package org.scify.jedai.gui.nodes;

import javafx.scene.control.TreeTableCell;
import org.scify.jedai.gui.model.WorkflowResult;

/**
 * TreeTableCell that shows values as they are, except if they are equal to "-1", in which
 * case it shows a dash ("-").
 */
public class NonNegativeTreeTableCell extends TreeTableCell<WorkflowResult, Object> {
    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (!item.toString().equals("-1")) {
                setText(item.toString());
            } else {
                setText("-");
            }
        }
    }
}
