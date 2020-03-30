package org.scify.jedai.gui.wizard;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.beans.binding.When;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.apache.commons.lang3.tuple.MutablePair;
import org.scify.jedai.entitymatching.GroupLinkage;
import org.scify.jedai.entitymatching.ProfileMatcher;
import org.scify.jedai.gui.model.JedaiMethodConfiguration;
import org.scify.jedai.gui.utilities.DialogHelper;
import org.scify.jedai.gui.utilities.DynamicMethodConfiguration;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.utilities.WorkflowStep;
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

    private List<WorkflowStep> initialSteps;
    private List<WorkflowStep> finalSteps;
    private List<WorkflowStep> intermediateSteps = new ArrayList<>();

    private List<WorkflowStep> blockingWorkflowSteps;
    private List<WorkflowStep> joinWorkflowSteps;
    private List<WorkflowStep> progressiveWorkflowSteps;

    private Map<String, WorkflowStep> availableSteps;

    private int totalSteps = -1;

    private final IntegerProperty currentStep = new SimpleIntegerProperty(-1);

    @FXML
    public void initialize() {
        // Create WorkflowSteps for all possible steps
        initializeStepsMap();

        // Initialize lists of steps for the beginning/end of workflows as well as the three workflows
        initializeStepLists();

        // Build steps
        buildSteps(initialSteps);
        buildSteps(finalSteps);

        // Add a listener to change the intermediate steps of the workflow then the selected workflow changes
        this.model.workflowProperty().addListener((observable, oldValue, newValue) -> switchWorkflow(newValue));

        // Switch to the default selected workflow
        this.switchWorkflow(this.model.getWorkflow());

        // Initialize wizard GUI content
        initButtons();
        buildIndicatorCircles();
        setInitialContent();
    }

    /**
     * Create the map of step name -> WorkflowStep object for that step
     * (containing its name, description, configuration etc.
     */
    private void initializeStepsMap() {
        this.availableSteps = new HashMap<>();

        this.availableSteps.put(JedaiOptions.STEP_LABEL_WELCOME,
                new WorkflowStep(
                        JedaiOptions.STEP_LABEL_WELCOME,
                        JedaiOptions.STEP_DESCRIPTION_WELCOME,
                        "wizard-fxml/steps/Welcome.fxml"
                ));

        this.availableSteps.put(JedaiOptions.STEP_LABEL_WORKFLOW_SELECTION,
                new WorkflowStep(
                        JedaiOptions.STEP_LABEL_WORKFLOW_SELECTION,
                        JedaiOptions.STEP_DESCRIPTION_WORKFLOW_SELECTION,
                        "wizard-fxml/steps/WorkflowSelection.fxml"
                ));

        this.availableSteps.put(JedaiOptions.STEP_LABEL_DATA_READING,
                new WorkflowStep(
                        JedaiOptions.STEP_LABEL_DATA_READING,
                        JedaiOptions.STEP_DESCRIPTION_DATA_READING,
                        "wizard-fxml/steps/DataReading.fxml"
                ));

        this.availableSteps.put(JedaiOptions.STEP_LABEL_SCHEMA_CLUSTERING,
                new WorkflowStep(
                        JedaiOptions.STEP_LABEL_SCHEMA_CLUSTERING,
                        JedaiOptions.STEP_DESCRIPTION_SCHEMA_CLUSTERING,
                        "wizard-fxml/steps/SchemaClustering.fxml",
                        model.schemaClusteringConfigTypeProperty()
                ));

        this.availableSteps.put(JedaiOptions.STEP_LABEL_BLOCK_BUILDING,
                new WorkflowStep(
                        JedaiOptions.STEP_LABEL_BLOCK_BUILDING,
                        JedaiOptions.STEP_DESCRIPTION_BLOCK_BUILDING,
                        "wizard-fxml/steps/BlockBuilding.fxml"
                ));

        this.availableSteps.put(JedaiOptions.STEP_LABEL_BLOCK_CLEANING,
                new WorkflowStep(
                        JedaiOptions.STEP_LABEL_BLOCK_CLEANING,
                        JedaiOptions.STEP_DESCRIPTION_BLOCK_CLEANING,
                        "wizard-fxml/steps/BlockCleaning.fxml"
                ));

        this.availableSteps.put(JedaiOptions.STEP_LABEL_COMPARISON_CLEANING,
                new WorkflowStep(
                        JedaiOptions.STEP_LABEL_COMPARISON_CLEANING,
                        JedaiOptions.STEP_DESCRIPTION_COMPARISON_CLEANING,
                        "wizard-fxml/steps/ComparisonCleaning.fxml",
                        model.comparisonCleaningConfigTypeProperty()
                ));

        this.availableSteps.put(JedaiOptions.STEP_LABEL_ENTITY_MATCHING,
                new WorkflowStep(
                        JedaiOptions.STEP_LABEL_ENTITY_MATCHING,
                        JedaiOptions.STEP_DESCRIPTION_ENTITY_MATCHING,
                        "wizard-fxml/steps/EntityMatching.fxml",
                        model.entityMatchingConfigTypeProperty()
                ));

        this.availableSteps.put(JedaiOptions.STEP_LABEL_ENTITY_CLUSTERING,
                new WorkflowStep(
                        JedaiOptions.STEP_LABEL_ENTITY_CLUSTERING,
                        JedaiOptions.STEP_DESCRIPTION_ENTITY_CLUSTERING,
                        "wizard-fxml/steps/EntityClustering.fxml",
                        model.entityClusteringConfigTypeProperty()
                ));

        this.availableSteps.put(JedaiOptions.STEP_LABEL_SIMILARITY_JOIN,
                new WorkflowStep(
                        JedaiOptions.STEP_LABEL_SIMILARITY_JOIN,
                        JedaiOptions.STEP_DESCRIPTION_SIMILARITY_JOIN,
                        "wizard-fxml/steps/SimilarityJoin.fxml",
                        new SimpleStringProperty(JedaiOptions.MANUAL_CONFIG) // Similarity Join is always manual config.
                ));

        this.availableSteps.put(JedaiOptions.STEP_LABEL_PRIORITIZATION,
                new WorkflowStep(
                        JedaiOptions.STEP_LABEL_PRIORITIZATION,
                        JedaiOptions.STEP_DESCRIPTION_PRIORITIZATION,
                        "wizard-fxml/steps/Prioritization.fxml",
                        model.prioritizationConfigTypeProperty()
                ));

        this.availableSteps.put(JedaiOptions.STEP_LABEL_SELECTION_CONFIRMATION,
                new WorkflowStep(
                        JedaiOptions.STEP_LABEL_SELECTION_CONFIRMATION,
                        JedaiOptions.STEP_DESCRIPTION_SELECTION_CONFIRMATION,
                        "wizard-fxml/steps/Confirm.fxml"
                ));

        this.availableSteps.put(JedaiOptions.STEP_LABEL_WORKFLOW_EXECUTION,
                new WorkflowStep(
                        JedaiOptions.STEP_LABEL_WORKFLOW_EXECUTION,
                        JedaiOptions.STEP_DESCRIPTION_WORKFLOW_EXECUTION,
                        "wizard-fxml/steps/Completed.fxml"
                ));
    }

    /**
     * Create the lists of steps for start of workflow, end of workflow, and the intermediate steps for each of the
     * three JedAI workflows.
     */
    private void initializeStepLists() {
        // Initialize initial steps, which always stay the same
        this.initialSteps = new ArrayList<>(
                Arrays.asList(
                        availableSteps.get(JedaiOptions.STEP_LABEL_WELCOME),
                        availableSteps.get(JedaiOptions.STEP_LABEL_WORKFLOW_SELECTION),
                        availableSteps.get(JedaiOptions.STEP_LABEL_DATA_READING)
                )
        );

        // Initialize last steps
        this.finalSteps = new ArrayList<>(
                Arrays.asList(
//                        availableSteps.get(JedaiOptions.STEP_LABEL_SELECTION_CONFIRMATION),
                        availableSteps.get(JedaiOptions.STEP_LABEL_WORKFLOW_EXECUTION)
                )
        );

        // Initialize blocking-based, join-based and progressive workflow lists of steps
        this.blockingWorkflowSteps = new ArrayList<>(
                Arrays.asList(
                        availableSteps.get(JedaiOptions.STEP_LABEL_SCHEMA_CLUSTERING),
                        availableSteps.get(JedaiOptions.STEP_LABEL_BLOCK_BUILDING),
                        availableSteps.get(JedaiOptions.STEP_LABEL_BLOCK_CLEANING),
                        availableSteps.get(JedaiOptions.STEP_LABEL_COMPARISON_CLEANING),
                        availableSteps.get(JedaiOptions.STEP_LABEL_ENTITY_MATCHING),
                        availableSteps.get(JedaiOptions.STEP_LABEL_ENTITY_CLUSTERING)
                )
        );

        this.joinWorkflowSteps = new ArrayList<>(
                Arrays.asList(
                        availableSteps.get(JedaiOptions.STEP_LABEL_SIMILARITY_JOIN),
                        availableSteps.get(JedaiOptions.STEP_LABEL_ENTITY_CLUSTERING)
                )
        );

        this.progressiveWorkflowSteps = new ArrayList<>(
                Arrays.asList(
                        availableSteps.get(JedaiOptions.STEP_LABEL_SCHEMA_CLUSTERING),
                        availableSteps.get(JedaiOptions.STEP_LABEL_BLOCK_BUILDING),
                        availableSteps.get(JedaiOptions.STEP_LABEL_BLOCK_CLEANING),
                        availableSteps.get(JedaiOptions.STEP_LABEL_COMPARISON_CLEANING),
                        availableSteps.get(JedaiOptions.STEP_LABEL_PRIORITIZATION),
                        availableSteps.get(JedaiOptions.STEP_LABEL_ENTITY_MATCHING),
                        availableSteps.get(JedaiOptions.STEP_LABEL_ENTITY_CLUSTERING)
                )
        );
    }

    /**
     * Update the totalSteps variable with the number of steps in the current workflow.
     */
    private void updateTotalSteps() {
        this.totalSteps = this.initialSteps.size() + this.intermediateSteps.size() + this.finalSteps.size();
    }

    private void switchWorkflow(String workflow) {
        // Add intermediate steps
        switch (workflow) {
            case JedaiOptions.WORKFLOW_BLOCKING_BASED:
                this.intermediateSteps = this.blockingWorkflowSteps;
                break;
            case JedaiOptions.WORKFLOW_JOIN_BASED:
                this.intermediateSteps = this.joinWorkflowSteps;
                break;
            case JedaiOptions.WORKFLOW_PROGRESSIVE:
                this.intermediateSteps = this.progressiveWorkflowSteps;
                break;
        }

        // Build the steps
        // todo: Check if we could build them once and re-use
        buildSteps(intermediateSteps);

        // Set totalSteps to the correct number of steps
        updateTotalSteps();

        // Rebuild indicator circles
        buildIndicatorCircles();

        // Re-initialize buttons as the number of steps could have changed
        initButtons();
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
     * Get the WorkflowStep object for the current step
     *
     * @return WorkflowStep object for the current step
     */
    private WorkflowStep getCurrentStep() {
        return getStep(currentStep.get());
    }

    /**
     * (Re)bind back/next/cancel button properties.
     */
    private void initButtons() {
        // Disable back button in the 1st step and when workflow is running
        btnBack.disableProperty().unbind();
        btnBack.disableProperty().bind(currentStep.lessThanOrEqualTo(0).or(model.workflowRunningProperty()));

        // Disable next step button in the last step and when workflow is running
        btnNext.disableProperty().unbind();
        btnNext.disableProperty()
                .bind(currentStep.greaterThanOrEqualTo(this.totalSteps - 1).or(model.workflowRunningProperty()));

        // Make the cancel button's text show "Start Over" in the last step
        btnCancel.textProperty().unbind();
        btnCancel.textProperty().bind(
//                new When(currentStep.lessThan(steps.size() - 1))
                new When(currentStep.lessThan(this.totalSteps - 1))
                        .then("Cancel")
                        .otherwise("Start Over")
        );

        // Disable the cancel/start over button in the 1st step and when workflow is running
        btnCancel.disableProperty().unbind();
        btnCancel.disableProperty().bind(currentStep.lessThanOrEqualTo(0).or(model.workflowRunningProperty()));
    }

    /**
     * Update the stepsLabel and stepDescriptionTextArea values to the ones for the current step
     */
    private void updateLabelAndDescription() {
        WorkflowStep step = this.getCurrentStep();

        stepsLabel.setText(step.getLabel());
        stepDescriptionTextarea.setText(step.getDescription());
    }

    private void setInitialContent() {
        currentStep.set(0);  // First element
        contentPanel.getChildren().add(this.getCurrentStep().getNode());

        // Set step text & description
        updateLabelAndDescription();
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
                this.getCurrentStep().getNode()
        );

        // Increment the current step
        currentStep.set(stepNum);

        // Show the next step (incremented currentStep)
        contentPanel.getChildren().add(
                this.getCurrentStep().getNode()
        );

        // Set step label & description
        updateLabelAndDescription();
    }

    /**
     * Return whether the currently selected ER type is Clean-Clean ER.
     *
     * @return True if clean-clean ER is selected.
     */
    private boolean isCleanCleanEr() {
        return model.getErType().equals(JedaiOptions.CLEAN_CLEAN_ER);
    }

    @FXML
    public void next() {
        // Stop if we can't go to next step
        if (currentStep.get() >= (this.totalSteps - 1)) {
            return;
        }

        // Get current step node to check if everything is valid
        Parent p = this.getCurrentStep().getNode();
        Object controller = p.getProperties().get(CONTROLLER_KEY);

        // Validate
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

        // Submit
        Method sub = getMethod(Submit.class, controller);
        if (sub != null) {
            try {
                sub.invoke(controller);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        // Get WorkflowStep object for current step, to check for manual configuration
        WorkflowStep step = this.getCurrentStep();

        // Check if configuration is manual
        if (step.hasConfigProperty() && step.getConfigProperty().equals(JedaiOptions.MANUAL_CONFIG)) {
            // Get method instance & model parameter property in order to show the configuration modal
            IDocumentation method = null;
            String methodName;
            ListProperty<MutablePair<String, Object>> parametersProperty = null;

            switch (step.getLabel()) {
                case JedaiOptions.STEP_LABEL_SCHEMA_CLUSTERING:
                    // Schema Clustering
                    parametersProperty = model.schemaClusteringParametersProperty();
                    methodName = model.getSchemaClustering();

                    // Go to next step if "No schema clustering" is selected
                    if (methodName.equals(JedaiOptions.NO_SCHEMA_CLUSTERING)) {
                        this.goToStep(currentStep.get() + 1);
                        return;
                    }

                    // Create a method instance to manually configure it
                    method = SchemaClusteringMethod.getModel(
                            RepresentationModel.CHARACTER_TRIGRAMS,
                            SimilarityMetric.ENHANCED_JACCARD_SIMILARITY,
                            MethodMapping.schemaClusteringMethods.get(methodName));
                    break;
                case JedaiOptions.STEP_LABEL_COMPARISON_CLEANING:
                    // Comparison Cleaning
                    parametersProperty = model.comparisonCleaningParametersProperty();
                    methodName = model.getComparisonCleaning();

                    // Go to next step if "No comparison cleaning" is selected
                    if (methodName.equals(JedaiOptions.NO_CLEANING)) {
                        this.goToStep(currentStep.get() + 1);
                        return;
                    }

                    // Create method instance to manually configure it
                    method = MethodMapping.getMethodByName(methodName, this.isCleanCleanEr());
                    break;
                case JedaiOptions.STEP_LABEL_ENTITY_MATCHING:
                    // Entity Matching
                    parametersProperty = model.entityMatchingParametersProperty();

                    methodName = model.getEntityMatching();
                    method = (methodName.equals(JedaiOptions.GROUP_LINKAGE)) ?
                            new GroupLinkage(new ArrayList<>()) : new ProfileMatcher(new ArrayList<>());

                    break;
                case JedaiOptions.STEP_LABEL_ENTITY_CLUSTERING:
                    // Entity Clustering
                    parametersProperty = model.entityClusteringParametersProperty();

                    methodName = model.getEntityClustering();
                    method = MethodMapping.getEntityClusteringMethod(methodName);

                    break;
                case JedaiOptions.STEP_LABEL_SIMILARITY_JOIN:
                    // Similarity Join
                    parametersProperty = model.similarityJoinParametersProperty();

                    methodName = model.getSimilarityJoin();
                    method = MethodMapping.getSimilarityJoinMethodByName(methodName);
                    break;
                case JedaiOptions.STEP_LABEL_PRIORITIZATION:
                    // Prioritization
                    parametersProperty = model.prioritizationParametersProperty();

                    methodName = model.getPrioritization();
                    method = MethodMapping.getPrioritizationMethodByName(methodName, 0);
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
        } else if (step.getLabel().equals(JedaiOptions.STEP_LABEL_BLOCK_BUILDING) ||
                step.getLabel().equals(JedaiOptions.STEP_LABEL_BLOCK_CLEANING)) {
            // Special case: Block Building and Cleaning can have multiple methods.
            boolean isBlockBuilding = (step.getLabel().equals(JedaiOptions.STEP_LABEL_BLOCK_BUILDING));

            // We need to check each method separately. Get the methods...
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
                        method = MethodMapping.getMethodByName(methodConfig.getName(), this.isCleanCleanEr());
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
        if (methods.length > 0) {
            for (Method m : methods) {
                if (m.isAnnotationPresent(an)) {
                    return m;
                }
            }
        }
        return null;
    }
}
