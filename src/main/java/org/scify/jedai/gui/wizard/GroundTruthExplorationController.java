package org.scify.jedai.gui.wizard;

import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.layout.VBox;
import org.scify.jedai.datamodel.EntityProfile;
import org.scify.jedai.datamodel.IdDuplicates;

import java.util.List;
import java.util.Set;

public class GroundTruthExplorationController {
    private final int pageSize = 10;
    public Pagination entityPagination;
    public VBox containerVBox;


    @FXML
    public void initialize() {
    }

    /**
     * Show the entities in the window.
     */
    private void updateView() {
        //todo: Implement

        // Setup pagination
//        entityPagination.setPageCount(??);
        entityPagination.setPageFactory(pageIndex -> {
            // Create node that we will add entities to
            VBox vBox = new VBox();

            //todo: Add entities
            return vBox;
        });
    }

    /**
     * Set the duplicates to show, for 1 dataset (Dirty ER)
     *
     * @param duplicates Set of duplicates
     * @param entities   Entities of the dataset
     */
    public void setDuplicates(Set<IdDuplicates> duplicates, List<EntityProfile> entities) {
        System.out.println("Setting duplicates with 1 dataset");
        //todo: Implement
    }

    /**
     * Set the duplicates to show, for 2 datasets (Clean-Clean ER)
     *
     * @param duplicates Set of the duplicates
     * @param entitiesD1 Entities of the 1st dataset
     * @param entitiesD2 Entities of the 2nd dataset
     */
    public void setDuplicates(Set<IdDuplicates> duplicates,
                              List<EntityProfile> entitiesD1, List<EntityProfile> entitiesD2) {
        System.out.println("Setting duplicates with 2 datasets");
        //todo: Implement
    }
}
