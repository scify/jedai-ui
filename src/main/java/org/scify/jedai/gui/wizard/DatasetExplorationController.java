package org.scify.jedai.gui.wizard;

import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.layout.VBox;
import org.scify.jedai.datamodel.EntityProfile;
import org.scify.jedai.gui.utilities.DataReadingHelper;
import org.scify.jedai.gui.utilities.EntityProfileNode;
import org.scify.jedai.gui.utilities.JPair;

import java.util.ArrayList;
import java.util.List;

public class DatasetExplorationController {
    private final int pageSize = 10;

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
        List<EntityProfile> entities = DataReadingHelper.getEntities(this.datasetType, this.datasetParams);

        // Find number of pages we need to show 10 entities per page
        int pagesNum = (entities == null) ? 0 : entities.size() / pageSize;
        // todo: Check if we need +1 page

        // Setup pagination
        entityPagination.setPageCount(pagesNum);
        entityPagination.setPageFactory(pageIndex -> {
            // Create node that we will add entities to
            VBox vBox = new VBox();

            // Get the entities to show
            List<EntityProfile> pageEntities = (entities == null) ?
                    new ArrayList<>() : entities.subList(pageIndex * pageSize, (pageIndex + 1) * pageSize);

            // Generate an entity profile node for each entity
            for (EntityProfile ep : pageEntities) {
                vBox.getChildren().add(new EntityProfileNode(ep));
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
