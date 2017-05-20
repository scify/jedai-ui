package workbench;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;

/**
 * Class for showing a Table cell with a link to details for a Workflow run in the Workbench.
 * Based on: http://stackoverflow.com/a/38290874
 */
public class DetailsCell<T> extends TableCell<T, Void> {
    private final Hyperlink link;

    public DetailsCell() {
        link = new Hyperlink("View");
        link.setOnAction(evt -> {
            // todo: show details popup here...
//            this.getTableRow()......
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);

        setGraphic(empty ? null : link);
    }
}
