package org.scify.jedai.gui.wizard;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.beans.binding.When;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.scify.jedai.entitymatching.GroupLinkage;
import org.scify.jedai.entitymatching.ProfileMatcher;
import org.scify.jedai.gui.controllers.steps.CompletedController;
import org.scify.jedai.gui.model.JedaiMethodConfiguration;
import org.scify.jedai.gui.utilities.*;
import org.scify.jedai.utilities.IDocumentation;
import org.scify.jedai.utilities.enumerations.BlockBuildingMethod;
import org.scify.jedai.utilities.enumerations.RepresentationModel;
import org.scify.jedai.utilities.enumerations.SchemaClusteringMethod;
import org.scify.jedai.utilities.enumerations.SimilarityMetric;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class WizardController {
    @SuppressWarnings("FieldCanBeLocal")
    private final int INDICATOR_RADIUS = 10;

    private final String CONTROLLER_KEY = "controller";
    public Label stepsLabel;
    public TextArea stepDescriptionTextarea;

    private Map<Integer, StringProperty> configurationTypes;

    @FXML
    VBox contentPanel;

    @FXML
    HBox hboxIndicators;

    @FXML
    Button btnNext, btnBack, btnCancel;

    @Inject
    private Injector injector;

    @Inject
    private WizardData model;

    private final List<Parent> steps = new ArrayList<>();

    private final List<WorkflowStep> initialSteps;
    private final List<WorkflowStep> finalSteps;
    private List<WorkflowStep> intermediateSteps = new ArrayList<>();

    private int totalSteps;

    private final IntegerProperty currentStep = new SimpleIntegerProperty(-1);

    public WizardController() {
        // Initialize initial steps, which always stay the same
        this.initialSteps = new ArrayList<>(
                Arrays.asList(
                        new WorkflowStep(
                                JedaiOptions.STEP_LABEL_WELCOME,
                                JedaiOptions.STEP_DESCRIPTION_WELCOME,
                                "wizard-fxml/steps/Welcome.fxml"
                        ),
                        new WorkflowStep(
                                JedaiOptions.STEP_LABEL_WORKFLOW_SELECTION,
                                JedaiOptions.STEP_DESCRIPTION_WORKFLOW_SELECTION,
                                "wizard-fxml/steps/WorkflowSelection.fxml"
                        )
                )
        );

        // Initialize last steps
        this.finalSteps = new ArrayList<>(
                Arrays.asList(
                        new WorkflowStep(
//                                JedaiOptions.STEP_LABEL_SELECTION_CONFIRMATION,
//                                JedaiOptions.STEP_DESCRIPTION_SELECTION_CONFIRMATION,
//                                "wizard-fxml/steps/Confirm.fxml"
//                        ),
//                        new WorkflowStep(
                                JedaiOptions.STEP_LABEL_WORKFLOW_EXECUTION,
                                JedaiOptions.STEP_DESCRIPTION_WORKFLOW_EXECUTION,
                                "wizard-fxml/steps/Completed.fxml"
                        )
                )
        );

        // Set total steps (will be updated later as well)
        totalSteps = initialSteps.size() + finalSteps.size();
    }

    @FXML
    public void initialize() {
        // todo: most things below will be replaced

        // Initialize hashmap with configuration types
        this.configurationTypes = new HashMap<>();
        this.configurationTypes.put(3, model.schemaClusteringConfigTypeProperty());
        this.configurationTypes.put(6, model.comparisonCleaningConfigTypeProperty());
        this.configurationTypes.put(7, model.entityMatchingConfigTypeProperty());
        this.configurationTypes.put(8, model.entityClusteringConfigTypeProperty());

        buildSteps(initialSteps);
        buildSteps(finalSteps);
        initButtons();
        buildIndicatorCircles();
        setInitialContent();
    }

    /**
     * Given a step number, return its WorkflowStep object for the current workflow.
     * If initial, intermediate and final steps were 1 list, get the step with index stepNum...
     *
     * @param stepNum Number of step to get.
     * @return WorkflowStep object of the wanted step.
     */
    private WorkflowStep getStep(int stepNum) {
        if (stepNum < initialSteps.size()) {
            return initialSteps.get(stepNum);
        } else if (stepNum < initialSteps.size() + intermediateSteps.size()) {
            return intermediateSteps.get(stepNum - initialSteps.size());
        } else {
            return finalSteps.get(stepNum - initialSteps.size() - intermediateSteps.size());
        }
    }

    /**
     * Similar to getStep, but returns the step's node
     *
     * @param stepNum Number of step to get node for.
     * @return Node of step
     */
    private Parent getStepNode(int stepNum) {
        return getStep(stepNum).getNode();
    }

    private void initButtons() {
        // Disable back button in the 1st step and when workflow is running
        btnBack.disableProperty().bind(currentStep.lessThanOrEqualTo(0).or(model.workflowRunningProperty()));

        // Disable next step button in the last step and when workflow is running
        btnNext.disableProperty()
                .bind(currentStep.greaterThanOrEqualTo(this.totalSteps - 1).or(model.workflowRunningProperty()));
//                .bind(currentStep.greaterThanOrEqualTo(steps.size() - 1).or(model.workflowRunningProperty()));

        // Make the cancel button's text show "Start Over" in the last step
        btnCancel.textProperty().bind(
//                new When(currentStep.lessThan(steps.size() - 1))
                new When(currentStep.lessThan(this.totalSteps - 1))
                        .then("Cancel")
                        .otherwise("Start Over")
        );

        // Disable the cancel/start over button in the 1st step and when workflow is running
        btnCancel.disableProperty().bind(currentStep.lessThanOrEqualTo(0).or(model.workflowRunningProperty()));
    }

    /**
     * Set the stepsLabel and stepDescriptionTextArea values to the ones for the given step
     *
     * @param stepNum Step number
     */
    private void setLabelAndDescription(int stepNum) {
        WorkflowStep step = this.getStep(stepNum);
        stepsLabel.setText(step.getLabel());
        stepDescriptionTextarea.setText(step.getDescription());
    }

    private void setInitialContent() {
        currentStep.set(0);  // First element
        contentPanel.getChildren().add(getStepNode(currentStep.get()));

        // Set step text & description
        setLabelAndDescription(currentStep.get());
    }

    private void buildIndicatorCircles() {
        // Remove old indicators
        hboxIndicators.getChildren().clear();

        // Build new indicators
        for (int i = 0; i < this.totalSteps; i++) {
            hboxIndicators.getChildren().add(createIndicatorCircle(i));
        }
    }

    /**
     * Load FXML files for a list of WorkflowSteps
     *
     * @param steps List of steps to load nodes for
     */
    private void buildSteps(List<WorkflowStep> steps) {
        for (WorkflowStep step : steps) {
            step.setNode(
                    DialogHelper.loadFxml(this.getClass(), injector, step.getFxmlPath())
            );
        }
    }

    private Circle createIndicatorCircle(int i) {
        Circle circle = new Circle(INDICATOR_RADIUS, Color.WHITE);
        circle.setStroke(Color.BLACK);

        circle.fillProperty().bind(
                new When(
                        currentStep.greaterThanOrEqualTo(i))
                        .then(Color.DODGERBLUE)
                        .otherwise(Color.WHITE));

        return circle;
    }

    private void goToStep(int stepNum) {
        // Remove old step from view
        contentPanel.getChildren().remove(
                getStepNode(currentStep.get())
        );

        // Increment the current step
        currentStep.set(stepNum);

        // Show the next step (incremented currentStep)
        contentPanel.getChildren().add(
                getStepNode(currentStep.get())
        );

        // Set step label & description
        setLabelAndDescription(currentStep.get());
    }

    @FXML
    public void next() {
        Parent p = getStepNode(currentStep.get());
        Object controller = p.getProperties().get(CONTROLLER_KEY);

        // validate
        Method v = getMethod(Validate.class, controller);
        if (v != null) {
            try {
                Object retval = v.invoke(controller);
                if (retval != null && !((Boolean) retval)) {
                    return;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        // submit
        Method sub = getMethod(Submit.class, controller);
        if (sub != null) {
            try {
                sub.invoke(controller);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        // todo: check & update this entire if/else if!!!!!!!!!!!!!
        if (currentStep.get() < (totalSteps - 1)) {
            // Check if configuration is manual, and show manual configuration window before next step
            if (this.configurationTypes.containsKey(currentStep.get())
                    && this.configurationTypes.get(currentStep.get()).getValue().equals(JedaiOptions.MANUAL_CONFIG)) {

                // Get method instance & model parameter property in order to show the configuration modal
                IDocumentation method = null;
                String methodName;
                ListProperty<JPair<String, Object>> parametersProperty = null;

                switch (currentStep.get()) {
                    case 3:
                        // Schema Clustering
                        // todo: when selecting "no SC" and manual configuration, we can't advance to next step
                        parametersProperty = model.schemaClusteringParametersProperty();

                        methodName = model.getSchemaClustering();
                        method = SchemaClusteringMethod.getModel(
                                RepresentationModel.CHARACTER_TRIGRAMS,
                                SimilarityMetric.ENHANCED_JACCARD_SIMILARITY,
                                MethodMapping.schemaClusteringMethods.get(methodName));
                        break;
                    case 6:
                        // Comparison Cleaning
                        // todo: when selecting "no CoCl" and manual configuration, we can't advance to next step
                        parametersProperty = model.comparisonCleaningParametersProperty();

                        methodName = model.getComparisonCleaning();
                        method = MethodMapping.getMethodByName(methodName);

                        break;
                    case 7:
                        // Entity Matching
                        parametersProperty = model.entityMatchingParametersProperty();

                        methodName = model.getEntityMatching();
                        method = (methodName.equals(JedaiOptions.GROUP_LINKAGE)) ?
                                new GroupLinkage() : new ProfileMatcher();

                        break;
                    case 8:
                        // Entity Clustering
                        parametersProperty = model.entityClusteringParametersProperty();

                        methodName = model.getEntityClustering();
                        method = MethodMapping.getEntityClusteringMethod(methodName);

                        break;
                }

                // Check that the method isn't null
                if (method == null)
                    return;

                // Display the configuration modal
                DynamicMethodConfiguration.displayModal(getClass(), injector, method, parametersProperty);

                // If configuration failed, don't go to next step
                if (!DynamicMethodConfiguration.configurationOk(method, parametersProperty.get())) {
                    return;
                }
            } else if (currentStep.get() == 4 || currentStep.get() == 5) {
                boolean isBlockBuilding = (currentStep.get() == 4);

                // Special case: Block Building and Cleaning can have multiple methods.
                // We need to check each one separately. Get the methods...
                List<JedaiMethodConfiguration> methodConfigs =
                        isBlockBuilding ? model.getBlockBuildingMethods() : model.getBlockCleaningMethods();

                for (JedaiMethodConfiguration methodConfig : methodConfigs) {
                    // If the method is enabled and its configuration type is set to manual...
                    if (methodConfig.isEnabled() && methodConfig.getConfigurationType().equals(JedaiOptions.MANUAL_CONFIG)) {
                        // Get an instance of the method

                        IDocumentation method;
                        if (isBlockBuilding) {
                            // Get block building method
                            method = BlockBuildingMethod.getDefaultConfiguration(
                                    MethodMapping.blockBuildingMethods.get(methodConfig.getName())
                            );
                        } else {
                            // Get block cleaning method
                            method = MethodMapping.getMethodByName(methodConfig.getName());
                        }

                        // Configure the method
                        DynamicMethodConfiguration.displayModal(getClass(), injector, method,
                                methodConfig.manualParametersProperty());

                        // If configuration failed, don't go to next step
                        if (!DynamicMethodConfiguration.configurationOk(method, methodConfig.getManualParameters())) {
                            return;
                        }
                    }
                }
            }

            // Go to the next step
            this.goToStep(currentStep.get() + 1);
        }
    }

    @FXML
    public void back() {
        // Go to previous step if we aren't in the 1st step already
        if (currentStep.get() > 0) {
            this.goToStep(currentStep.get() - 1);
        }
    }

    @FXML
    public void cancel() {
        // Go to first step
        this.goToStep(0);

        // todo: Get controller of last step, to reset its data (only if it's an instance of the CompletedController)
//        Object ctrl = steps.get(steps.size() - 1).getProperties().get(CONTROLLER_KEY);
//
//        if (ctrl instanceof CompletedController) {
//            ((CompletedController) ctrl).resetData();
//        }
    }

    private Method getMethod(Class<? extends Annotation> an, Object obj) {
        if (an == null) {
            return null;
        }

        if (obj == null) {
            return null;
        }

        Method[] methods = obj.getClass().getMethods();
        if (methods != null && methods.length > 0) {
            for (Method m : methods) {
                if (m.isAnnotationPresent(an)) {
                    return m;
                }
            }
        }
        return null;
    }
}
