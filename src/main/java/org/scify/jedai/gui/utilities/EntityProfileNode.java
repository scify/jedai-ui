package org.scify.jedai.gui.utilities;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.scify.jedai.datamodel.EntityProfile;

/**
 * Node that displays a single Entity Profile
 */
public class EntityProfileNode extends VBox {
    private final EntityProfile entity;

    public EntityProfileNode(EntityProfile entity) {
        this.entity = entity;

        // Testing
        this.getChildren().add(new Label(this.entity.toString()));
    }
}
