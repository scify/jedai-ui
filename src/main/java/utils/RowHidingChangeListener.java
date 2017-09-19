package utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.RowConstraints;

import java.util.List;

/**
 * Change Listener which takes as input a list of RowConstraints, and when the String value is set to Clean-Clean ER,
 * then the rows are set to height 30, otherwise 0. Used in ConfirmController to show/hide the rows for the 2nd Dataset.
 */
public class RowHidingChangeListener implements ChangeListener<String> {
    private List<RowConstraints> constraintsList;

    public RowHidingChangeListener(List<RowConstraints> constraintsList) {
        this.constraintsList = constraintsList;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        // When ER type is Clean-Clean, show this row
        if (newValue.equals(JedaiOptions.CLEAN_CLEAN_ER)) {
            // Show the row
            for (RowConstraints rowConstraint : constraintsList) {
                rowConstraint.setMaxHeight(30);
            }
        } else {
            // Hide the row
            for (RowConstraints rowConstraint : constraintsList) {
                rowConstraint.setMaxHeight(0);
            }
        }
    }
}
