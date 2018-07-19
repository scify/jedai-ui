package org.scify.jedai.gui.utilities;

import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.scify.jedai.datamodel.Attribute;
import org.scify.jedai.datamodel.EntityProfile;

import java.util.Arrays;
import java.util.List;

/**
 * Node that displays a single Entity Profile
 */
public class EntityProfileNode extends VBox {
    private static final Insets zeroPadding = new Insets(0, 0, 0, 0);
    private static final Font cellFont = Font.font("Arial", 11);

    public EntityProfileNode(int entityId, EntityProfile entity) {
        // The title will have the entity ID and URL
        TextFlow entityTitle = new TextFlow();
        List<String> titleItems = Arrays.asList(
                "Entity ID: ", String.valueOf(entityId),
                "\t\tURL: ", entity.getEntityUrl());

        boolean makeBold = true;
        for (String s : titleItems) {
            Text titlePart = new Text(s);
            titlePart.setStyle(makeBold ? "-fx-font-weight: bold" : "");
            entityTitle.getChildren().add(titlePart);

            // Make the items alternate between bold and regular font (to make "titles" bold)
            makeBold = !makeBold;
        }

        // Create node for showing the entity's attributes
        ListView<Attribute> attrsList = new ListView<>();
        attrsList.setFixedCellSize(15);
        attrsList.setCellFactory(lst ->
                new ListCell<Attribute>() {
                    @Override
                    protected void updateItem(Attribute item, boolean empty) {
                        super.updateItem(item, empty);
                        setPadding(zeroPadding);
                        setFont(cellFont);

                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.getName() + ": " + item.getValue());
                        }
                    }
                });
        // todo: Maybe sort the list of attributes by key?
        attrsList.getItems().addAll(entity.getAttributes());

        // Add title & attribute nodes to the entity profile node
        this.getChildren().addAll(entityTitle, attrsList);
    }
}
