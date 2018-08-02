package org.scify.jedai.gui.controllers;

import gnu.trove.iterator.TIntIterator;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.scify.jedai.datamodel.EntityProfile;
import org.scify.jedai.datamodel.EquivalenceCluster;
import org.scify.jedai.gui.nodes.EntityProfileNode;

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
                // Create nodes that will hold the entities (HBox into ScrollPane)
                ScrollPane entitiesPane = new ScrollPane();
                entitiesPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                entitiesPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

                HBox entitiesContainer = new HBox();

                // Add nodes that show duplicates depending on ER type
                if (dirtyEr) {
                    // Iterate on the list of entities for the 1st dataset (there is no 2nd)
                    TIntIterator iter = dup.getEntityIdsD1().iterator();
                    while (iter.hasNext()) {
                        // Get the entity ID
                        int entityId = iter.next();

                        // Find the entity in the dataset
                        EntityProfile entity = entitiesD1.get(entityId);

                        // Add node for this entity
                        EntityProfileNode entityProfileNode = new EntityProfileNode(entityId, entity);

                        // Add the entity node to the entities container
                        entitiesContainer.getChildren().add(entityProfileNode);
                    }
                } else {
                    //todo
                }

                // Fix sizes
                entitiesContainer.setFillHeight(true);
                entitiesPane.setFitToHeight(true);

                // Put the HBox in the scrolling entities pane
                entitiesPane.setContent(entitiesContainer);

                // Put the entities pane to the window
                vBox.getChildren().add(entitiesPane);
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
