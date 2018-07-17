package org.scify.jedai.gui.wizard;

import javafx.fxml.FXML;
import org.scify.jedai.gui.utilities.JPair;

import java.util.List;

public class DatasetExplorationController {
    private String datasetType = null;
    private List<JPair<String, Object>> datasetParams = null;

    @FXML
    public void initialize() {
    }

    public void setDatasetType(String datasetType) {
        this.datasetType = datasetType;
    }

    public void setDatasetParams(List<JPair<String, Object>> datasetParams) {
        this.datasetParams = datasetParams;
    }
}
