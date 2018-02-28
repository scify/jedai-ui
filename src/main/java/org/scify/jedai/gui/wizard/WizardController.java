package org.scify.jedai.gui.wizard;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.beans.binding.When;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import org.scify.jedai.entitymatching.GroupLinkage;
import org.scify.jedai.entitymatching.ProfileMatcher;
import org.scify.jedai.utilities.IDocumentation;
import org.scify.jedai.utilities.enumerations.BlockBuildingMethod;
import org.scify.jedai.gui.utilities.BlClMethodConfiguration;
import org.scify.jedai.gui.utilities.JPair;
import org.scify.jedai.gui.utilities.JedaiOptions;
import org.scify.jedai.gui.utilities.dynamic_configuration.MethodConfiguration;
import org.scify.jedai.gui.wizard.steps.CompletedController;

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

    private List<String> stepTexts;
    private List<String> stepDescriptions;
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

    private final IntegerProperty currentStep = new SimpleIntegerProperty(-1);

    @FXML
    public void initialize() throws Exception {
        // Initialize ArrayLists with step texts & descriptions
        this.stepTexts = Arrays.asList(
                "Welcome",
                "Step 1: Data Reading",
                "Step 2: Block Building",
                "Step 3: Block Cleaning",
                "Step 4: Comparison Cleaning",
                "Step 5: Entity Matching",
                "Step 6: Entity Clustering",
                "Selection Confirmation",
                "Workflow Execution"
        );

        this.stepDescriptions = Arrays.asList(
                "Welcome to JedAI, an open source, high scalability toolkit that offers out-of-the-box solutions for Entity Resolution over structured (relational) and semi-structured (RDF) data.",
                "Data Reading transforms the input data into a list of entity profiles.",
                "Block Building clusters entities into overlapping blocks in a lazy manner that relies on unsupervised blocking keys: every token in an attribute value forms a key. Blocks are then extracted, based on its equality or on its similarity with other keys.",
                "Block Cleaning aims to clean a set of overlapping blocks from unnecessary comparisons, which can be either redundant (i.e., repeated) or superfluous (i.e., between non-matching entities). Its methods operate on the coarse level of individual blocks or entities.",
                "Similar to Block Cleaning, Comparison Cleaning aims to clean a set of blocks from both redundant and superfluous comparisons. Unlike Block Cleaning, its methods operate on the finer granularity of individual comparisons.",
                "Entity Matching compares pairs of entity profiles, associating every pair with a similarity in [0,1]. Its output comprises the similarity graph, i.e., an undirected, weighted graph where the nodes correspond to entities and the edges connect pairs of compared entities.",
                "Entity Clustering takes as input the similarity graph produced by Entity Matching and partitions it into a set of equivalence clusters, with every cluster corresponding to a distinct real-world object.",
                "Confirm the selected values and press the \"Next\" button to go to the results page.",
                "Press \"Run algorithm\" to run the algorithm. You can export the results to a CSV file with the \"Export CSV\" button."
        );

        // Initialize hashmap with configuration types
        this.configurationTypes = new HashMap<>();
        this.configurationTypes.put(2, model.blockBuildingConfigTypeProperty());
        this.configurationTypes.put(4, model.comparisonCleaningConfigTypeProperty());
        this.configurationTypes.put(5, model.entityMatchingConfigTypeProperty());
        this.configurationTypes.put(6, model.entityClusteringConfigTypeProperty());

        buildSteps();
        initButtons();
        buildIndicatorCircles();
        setInitialContent();
    }

    private void initButtons() {
        btnBack.disableProperty().bind(currentStep.lessThanOrEqualTo(0));
        btnNext.disableProperty().bind(currentStep.greaterThanOrEqualTo(steps.size() - 1));

        btnCancel.textProperty().bind(
                new When(currentStep.lessThan(steps.size() - 1))
                        .then("Cancel")
                        .otherwise("Start Over")
        );
    }

    /**
     * Set the stepsLabel and stepDescriptionTextArea values to the ones for the given step
     *
     * @param stepNum Step number
     */
    private void setLabelAndDescription(int stepNum) {
        stepsLabel.setText(stepTexts.get(stepNum));
        stepDescriptionTextarea.setText(stepDescriptions.get(stepNum));
    }

    private void setInitialContent() {
        currentStep.set(0);  // first element
        contentPanel.getChildren().add(steps.get(currentStep.get()));

        // Set step text & description
        setLabelAndDescription(0);
    }

    private void buildIndicatorCircles() {
        for (int i = 0; i < steps.size(); i++) {
            hboxIndicators.getChildren().add(createIndicatorCircle(i));
        }
    }

    private void buildSteps() throws java.io.IOException {
        final JavaFXBuilderFactory bf = new JavaFXBuilderFactory();

        final Callback<Class<?>, Object> cb = (clazz) -> injector.getInstance(clazz);

        // Specify step FXMLs in order that they should appear
        ArrayList<String> controllers = new ArrayList<>(Arrays.asList(
                "wizard-fxml/steps/Step0.fxml",
                "wizard-fxml/steps/Step1.fxml",
                "wizard-fxml/steps/Step2.fxml",
                "wizard-fxml/steps/Step3.fxml",
                "wizard-fxml/steps/Step4.fxml",
                "wizard-fxml/steps/Step5.fxml",
                "wizard-fxml/steps/Step6.fxml",
                "wizard-fxml/steps/Confirm.fxml",
                "wizard-fxml/steps/Completed.fxml"
        ));

        // Create steps and add them to the list
        for (String ctrlPath : controllers) {
            // Create step
            FXMLLoader loader = new FXMLLoader(WizardController.class.getClassLoader().getResource(ctrlPath), null, bf, cb);
            Parent step = loader.load();
            step.getProperties().put(CONTROLLER_KEY, loader.getController());

            // Add step to steps list
            steps.add(step);
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

    @FXML
    public void next() {
        Parent p = steps.get(currentStep.get());
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

        if (currentStep.get() < (steps.size() - 1)) {
            // Check if configuration is manual, and show manual configuration window before next step
            if (this.configurationTypes.containsKey(currentStep.get())
                    && this.configurationTypes.get(currentStep.get()).getValue().equals(JedaiOptions.MANUAL_CONFIG)) {
//                System.out.println("Manually configuring step " + currentStep.get());

                // Get method instance & model parameter property in order to show the configuration modal
                IDocumentation method = null;
                String methodName;
                ListProperty<JPair<String, Object>> parametersProperty = null;

                switch (currentStep.get()) {
                    case 2:
                        // Block Building
                        parametersProperty = model.blockBuildingParametersProperty();

                        methodName = model.getBlockBuilding();
                        method = BlockBuildingMethod.getDefaultConfiguration(
                                MethodMapping.blockBuildingMethods.get(methodName)
                        );

                        break;
                    case 4:
                        // Comparison Cleaning
                        parametersProperty = model.comparisonCleaningParametersProperty();

                        methodName = model.getComparisonCleaning();
                        method = MethodMapping.getMethodByName(methodName);

                        break;
                    case 5:
                        // Entity Matching
                        parametersProperty = model.entityMatchingParametersProperty();

                        methodName = model.getEntityMatching();
                        method = (methodName.equals(JedaiOptions.GROUP_LINKAGE)) ?
                                new GroupLinkage() : new ProfileMatcher();

                        break;
                    case 6:
                        // Entity Clustering
                        parametersProperty = model.entityClusteringParametersProperty();

                        methodName = model.getEntityClustering();
                        method = MethodMapping.getEntityClusteringMethod(methodName);

                        break;
                }

                // Display the configuration modal
                if (method != null) {
                    MethodConfiguration.displayModal(getClass(), injector, method, parametersProperty);
                }

                // If configuration failed, don't go to next step
                if (!MethodConfiguration.configurationOk(method, parametersProperty.get())) {
                    return;
                }
            } else if (currentStep.get() == 3) {
                // Special case: Block Cleaning can have multiple methods. We need to check each one separately
                for (BlClMethodConfiguration bcmc : model.getBlockCleaningMethods()) {
                    // If the method is enabled and its configuration type is set to manual...
                    if (bcmc.isEnabled() && bcmc.getConfigurationType().equals(JedaiOptions.MANUAL_CONFIG)) {
                        // Get an instance of the method
                        IDocumentation method = MethodMapping.getMethodByName(bcmc.getName());

                        // Configure the method
                        MethodConfiguration.displayModal(getClass(), injector, method, bcmc.manualParametersProperty());

                        // If configuration failed, don't go to next step
                        if (!MethodConfiguration.configurationOk(method, bcmc.getManualParameters())) {
                            return;
                        }
                    }
                }
            }

            // Go to next step
            contentPanel.getChildren().remove(steps.get(currentStep.get()));
            currentStep.set(currentStep.get() + 1);
            contentPanel.getChildren().add(steps.get(currentStep.get()));

            // Set step label & description
            setLabelAndDescription(currentStep.getValue());
        }
    }

    @FXML
    public void back() {
        if (currentStep.get() > 0) {
            contentPanel.getChildren().remove(steps.get(currentStep.get()));
            currentStep.set(currentStep.get() - 1);
            contentPanel.getChildren().add(steps.get(currentStep.get()));

            // Set step label & description
            setLabelAndDescription(currentStep.getValue());
        }
    }

    @FXML
    public void cancel() {
        contentPanel.getChildren().remove(steps.get(currentStep.get()));
        currentStep.set(0);  // first screen
        contentPanel.getChildren().add(steps.get(currentStep.get()));

        setLabelAndDescription(currentStep.getValue());

        model.reset();

        // Get controller of last step, to reset its data (only if it's an instance of the CompletedController)
        Object ctrl = steps.get(steps.size() - 1).getProperties().get(CONTROLLER_KEY);

        if (ctrl instanceof CompletedController) {
            ((CompletedController) ctrl).resetData();
        }
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
