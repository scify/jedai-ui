package org.scify.jedai.gui.utilities;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.scify.jedai.datamodel.EntityProfile;

/**
 * Node that displays a single Entity Profile
 */
public class EntityProfileNode extends VBox {
    private final EntityProfile entity;

    public EntityProfileNode(EntityProfile entity) {
        this.entity = entity;

        // Border around entity
        this.borderProperty().setValue(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        // Add HBox for entity name & URL
        HBox entityTitle = new HBox();
        entityTitle.getChildren().addAll(
                // todo: Entity ID?
                new Label("Entity: " + this.entity.getEntityUrl())
        );

        // todo: Create node for showing the entity's attributes
        Node entityAttributes = new Label(this.entity.toString());

        // Add title & attribute nodes to the entity profile node
        this.getChildren().addAll(entityTitle, entityAttributes);
    }
}
