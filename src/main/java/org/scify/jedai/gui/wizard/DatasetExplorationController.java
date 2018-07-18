package org.scify.jedai.gui.wizard;

import javafx.fxml.FXML;
import org.scify.jedai.datamodel.EntityProfile;
import org.scify.jedai.gui.utilities.DataReadingHelper;
import org.scify.jedai.gui.utilities.JPair;

import java.util.List;

public class DatasetExplorationController {
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
//        System.out.println(entities.get(0));
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
