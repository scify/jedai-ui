package org.scify.jedai.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.VBox;
import org.scify.jedai.datamodel.EntityProfile;
import org.scify.jedai.gui.nodes.EntityProfileNode;
import org.scify.jedai.gui.utilities.DataReader;
import org.scify.jedai.gui.utilities.JPair;

import java.util.List;

public class DatasetExplorationController {
    private final int pageSize = 10;

    public VBox containerVBox;
    public Pagination entityPagination;
    private String datasetType = null;
    private List<JPair<String, Object>> datasetParams = null;

    @FXML
    public void initialize() {
    }

    /**
     * Show the entities in the window.
     */
    private void updateView() {
        // Read dataset
        List<EntityProfile> entities = DataReader.getEntities(this.datasetType, this.datasetParams);

        // Find number of pages we need to show 10 entities per page
        int pagesNum = 0;
        if (entities != null && !entities.isEmpty()) {
            pagesNum = entities.size() / pageSize;

            // Add last page if there are remaining items
            if (entities.size() % pageSize > 0) {
                pagesNum++;
            }
        } else {
            // Show no data message (by replacing the pagination component with a message label)
            containerVBox.getChildren().set(1, new Label("The dataset is empty!"));
        }

        // Setup pagination
        entityPagination.setPageCount(pagesNum);
        entityPagination.setPageFactory(pageIndex -> {
            // Create node that we will add entities to
            VBox vBox = new VBox();

            // Check that we have entities
            if (entities != null) {
                // Get the entities to show
                int firstEntity = pageIndex * pageSize;
                int lastEntity = Math.min((pageIndex + 1) * pageSize, entities.size());
                List<EntityProfile> pageEntities = entities.subList(firstEntity, lastEntity);

                // Generate an entity profile node for each entity. Their IDs start at firstEntity + 1 (to start from 1)
                int entityId = firstEntity + 1;
                for (EntityProfile ep : pageEntities) {
                    // Add the new entity profile node to the page
                    vBox.getChildren().add(new EntityProfileNode(entityId, ep));

                    // Increment the entity ID counter
                    entityId++;
                }
            }

            // Return generated node for this page
            return vBox;
        });
    }

    public void setDatasetType(String datasetType) {
        this.datasetType = datasetType;

        // Check if we can show the entities
        if (this.datasetType != null && this.datasetParams != null) {
            updateView();
        }
    }

    public void setDatasetParams(List<JPair<String, Object>> datasetParams) {
        this.datasetParams = datasetParams;

        // Check if we can show the entities
        if (this.datasetType != null && this.datasetParams != null) {
            updateView();
        }
    }
}
