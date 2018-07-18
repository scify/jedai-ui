package org.scify.jedai.gui.utilities;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.scify.jedai.datamodel.Attribute;
import org.scify.jedai.datamodel.EntityProfile;

/**
 * Node that displays a single Entity Profile
 */
public class EntityProfileNode extends VBox {
    private static final Insets zeroPadding = new Insets(0, 0, 0, 0);
    private static final Font cellFont = Font.font("Arial", 11);
    private final EntityProfile entity;

    public EntityProfileNode(EntityProfile entity) {
        this.entity = entity;

        // Add HBox for entity name & URL
        HBox entityTitle = new HBox();
        entityTitle.getChildren().addAll(
                // todo: Entity ID?
                new Label("Entity: " + this.entity.getEntityUrl())
        );

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
                            setText(item.toString());
                        }
                    }
                });
        attrsList.getItems().addAll(this.entity.getAttributes());

        // Add title & attribute nodes to the entity profile node
        this.getChildren().addAll(entityTitle, attrsList);
    }
}
