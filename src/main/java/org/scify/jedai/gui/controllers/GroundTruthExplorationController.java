package org.scify.jedai.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.layout.VBox;
import org.scify.jedai.datamodel.EntityProfile;
import org.scify.jedai.datamodel.EquivalenceCluster;

import java.util.List;

public class GroundTruthExplorationController {
    private final int pageSize = 10;
    public Pagination entityPagination;
    public VBox containerVBox;

    private boolean dirtyEr = true;
    private List<EquivalenceCluster> duplicates = null;
    private List<EntityProfile> entitiesD1 = null;
    private List<EntityProfile> entitiesD2 = null;

    @FXML
    public void initialize() {
    }

    /**
     * Show the entities in the window.
     */
    private void updateView() {
        // Find how many pages we need
        int pagesNum = duplicates.size() / pageSize;
        if (duplicates.size() % pageSize > 0) {
            pagesNum++;
        }

        // Setup pagination
        entityPagination.setPageCount(pagesNum);
        entityPagination.setPageFactory(pageIndex -> {
            // Create node that we will add entities to
            VBox vBox = new VBox();

            // Get entities to add
            List<EquivalenceCluster> duplicatesToShow =
                    duplicates.subList(pageIndex * pageSize, (pageIndex + 1) * pageSize);

            for (EquivalenceCluster dup : duplicatesToShow) {
                System.out.println(dup);
                // todo: Add node to show duplicates
            }

            return vBox;
        });
    }

    /**
     * Set the duplicates to show, for 1 dataset (Dirty ER)
     *
     * @param duplicates Set of duplicates
     * @param entities   Entities of the dataset
     */
    public void setDuplicates(List<EquivalenceCluster> duplicates, List<EntityProfile> entities) {
        this.dirtyEr = true;
        this.duplicates = duplicates;
        this.entitiesD1 = entities;

        updateView();
    }

    /**
     * Set the duplicates to show, for 2 datasets (Clean-Clean ER)
     *
     * @param duplicates Set of the duplicates
     * @param entitiesD1 Entities of the 1st dataset
     * @param entitiesD2 Entities of the 2nd dataset
     */
    public void setDuplicates(List<EquivalenceCluster> duplicates,
                              List<EntityProfile> entitiesD1, List<EntityProfile> entitiesD2) {
        this.dirtyEr = false;
        this.duplicates = duplicates;
        this.entitiesD1 = entitiesD1;
        this.entitiesD2 = entitiesD2;

        updateView();
    }
}
