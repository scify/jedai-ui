package org.scify.jedai.gui.utilities.dynamic_configuration;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HelpTooltip extends Label {
    public HelpTooltip(String tooltipText) {
        // Set text as question mark and general style
        this.setText("?");
        this.setPrefSize(35, 35);
        this.setMinSize(35, 35);
        this.setFont(Font.font("System", FontWeight.BOLD, 20));
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #9098ff; -fx-background-radius: 30px");

        // Create and add tooltip (need to set its font because otherwise it's inherited from the Label)
        Tooltip descriptionTooltip = new Tooltip(tooltipText);
        descriptionTooltip.setPrefWidth(250);
        descriptionTooltip.setWrapText(true);
        descriptionTooltip.setFont(new Font("System", 12));
        this.setTooltip(descriptionTooltip);
    }
}
