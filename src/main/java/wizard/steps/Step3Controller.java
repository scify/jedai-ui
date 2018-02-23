package wizard.steps;

import com.google.inject.Inject;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.BlClMethodConfiguration;
import utils.BlockCleaningCustomComparator;
import utils.JedaiOptions;
import utils.dynamic_configuration.ConfigurationTypeSelector;
import utils.dynamic_configuration.ConfigurationTypeSelectorHorizontal;
import wizard.Submit;
import wizard.Validate;
import wizard.WizardData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Step3Controller {
    public VBox containerVBox;
    public ListView<String> list;
    public VBox methodConfContainer;

    private BlockCleaningCustomComparator listComparator;
    private Map<String, SimpleBooleanProperty> optionsMap;
    private Logger log = LoggerFactory.getLogger(Step3Controller.class);

    @Inject
    private WizardData model;

    @FXML
    public void initialize() {
        // Create comparator object that will be used for list sorting later
        listComparator = new BlockCleaningCustomComparator();

        // Initialize block cleaning methods list
        model.setBlockCleaningMethods(FXCollections.observableList(new ArrayList<>()));

        // Add BlClMethodConfiguration objects to the model
        model.getBlockCleaningMethods().addAll(
                new BlClMethodConfiguration(JedaiOptions.SIZE_BASED_BLOCK_PURGING),
                new BlClMethodConfiguration(JedaiOptions.COMPARISON_BASED_BLOCK_PURGING),
                new BlClMethodConfiguration(JedaiOptions.BLOCK_FILTERING)
        );

        // Create map with method names -> boolean properties and add ConfigurationTypeSelectors
        optionsMap = new HashMap<>();

        List<BlClMethodConfiguration> blClMethods = model.getBlockCleaningMethods();
        for (BlClMethodConfiguration bcmc : blClMethods) {
            // Add to options HashMap
            optionsMap.put(bcmc.getName(), bcmc.enabledProperty());

            // Add configuration type selector for this method
            ConfigurationTypeSelector cts = new ConfigurationTypeSelectorHorizontal(bcmc.configurationTypeProperty());
            cts.bindEnabled(bcmc.enabledProperty());

            methodConfContainer.getChildren().add(cts);

            // Add separators between selectors
            if (blClMethods.indexOf(bcmc) < blClMethods.size() - 1) {
                methodConfContainer.getChildren().add(new Separator());
            }
        }

        // Add items to the list
        list.getItems().addAll(optionsMap.keySet());
        list.getItems().sort(listComparator);

        // Set list cells to have checkboxes which use the map's boolean values
        list.setCellFactory(CheckBoxListCell.forListView(optionsMap::get));
    }

    @Validate
    public boolean validate() throws Exception {
        return true;
    }

    @Submit
    public void submit() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("[SUBMIT] the user has completed step 3");
        }
    }
}
