package main.wizard;

import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.beans.binding.When;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WizardController {
    @SuppressWarnings("FieldCanBeLocal")
    private final int INDICATOR_RADIUS = 10;

    private final String CONTROLLER_KEY = "controller";
    public Label stepsLabel;
    public TextArea stepDescriptionTextarea;

    private List<String> stepTexts;
    private List<String> stepDescriptions;

    @FXML
    VBox contentPanel;

    @FXML
    HBox hboxIndicators;

    @FXML
    Button btnNext, btnBack, btnCancel;

    @Inject
    Injector injector;

    @Inject
    WizardData model;

    private final List<Parent> steps = new ArrayList<>();

    private final IntegerProperty currentStep = new SimpleIntegerProperty(-1);

    @FXML
    public void initialize() throws Exception {
        // Initialize ArrayLists with step texts & descriptions
        this.stepTexts = new ArrayList<>();
        this.stepTexts.add("Welcome");
        this.stepTexts.add("Step 1: Data Reading");
        this.stepTexts.add("Step 2: Block Building");
        this.stepTexts.add("Step 3: Block Processing");
        this.stepTexts.add("Step 4: Entity Matching");
        this.stepTexts.add("Step 5: Entity Clustering");
        this.stepTexts.add("Selection Confirmation");
        this.stepTexts.add("");

        this.stepDescriptions = new ArrayList<>();
        this.stepDescriptions.add("Welcome description");
        this.stepDescriptions.add("Data reading description");
        this.stepDescriptions.add("Block Building description");
        this.stepDescriptions.add("Block Processing description");
        this.stepDescriptions.add("Entity Matching description");
        this.stepDescriptions.add("Entity Clustering description");
        this.stepDescriptions.add("Selection Confirmation description");
        this.stepDescriptions.add("Run complete!");

        buildSteps();
        initButtons();
        buildIndicatorCircles();
        setInitialContent();

        model.setDataset("skip");
        model.setBlockBuilding("skip");
    }

    private void initButtons() {
        btnBack.disableProperty().bind(currentStep.lessThanOrEqualTo(0));
        btnNext.disableProperty().bind(currentStep.greaterThanOrEqualTo(steps.size() - 1));

        btnCancel.textProperty().bind(
                new When(
                        currentStep.lessThan(steps.size() - 1)
                )
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

        FXMLLoader fxmlLoaderStep0 = new FXMLLoader(WizardController.class.getResource("/main/wizard-fxml/steps/Step0.fxml"), null, bf, cb);
        Parent step0 = fxmlLoaderStep0.load();
        step0.getProperties().put(CONTROLLER_KEY, fxmlLoaderStep0.getController());

        FXMLLoader fxmlLoaderStep1 = new FXMLLoader(WizardController.class.getResource("/main/wizard-fxml/steps/Step1.fxml"), null, bf, cb);
        Parent step1 = fxmlLoaderStep1.load();
        step1.getProperties().put(CONTROLLER_KEY, fxmlLoaderStep1.getController());

        FXMLLoader fxmlLoaderStep2 = new FXMLLoader(WizardController.class.getResource("/main/wizard-fxml/steps/Step2.fxml"), null, bf, cb);
        Parent step2 = fxmlLoaderStep2.load();
        step2.getProperties().put(CONTROLLER_KEY, fxmlLoaderStep2.getController());

        FXMLLoader fxmlLoaderStep3 = new FXMLLoader(WizardController.class.getResource("/main/wizard-fxml/steps/Step3.fxml"), null, bf, cb);
        Parent step3 = fxmlLoaderStep3.load();
        step3.getProperties().put(CONTROLLER_KEY, fxmlLoaderStep3.getController());

        FXMLLoader fxmlLoaderStep4 = new FXMLLoader(WizardController.class.getResource("/main/wizard-fxml/steps/Step4.fxml"), null, bf, cb);
        Parent step4 = fxmlLoaderStep4.load();
        step4.getProperties().put(CONTROLLER_KEY, fxmlLoaderStep4.getController());

        FXMLLoader fxmlLoaderStep5 = new FXMLLoader(WizardController.class.getResource("/main/wizard-fxml/steps/Step5.fxml"), null, bf, cb);
        Parent step5 = fxmlLoaderStep5.load();
        step5.getProperties().put(CONTROLLER_KEY, fxmlLoaderStep5.getController());

        FXMLLoader fxmlLoaderConfirm = new FXMLLoader(WizardController.class.getResource("/main/wizard-fxml/steps/Confirm.fxml"), null, bf, cb);
        Parent confirm = fxmlLoaderConfirm.load();
        confirm.getProperties().put(CONTROLLER_KEY, fxmlLoaderConfirm.getController());

        FXMLLoader fxmlLoaderCompleted = new FXMLLoader(WizardController.class.getResource("/main/wizard-fxml/steps/Completed.fxml"), null, bf, cb);
        Parent completed = fxmlLoaderCompleted.load();
        completed.getProperties().put(CONTROLLER_KEY, fxmlLoaderCompleted.getController());

        steps.addAll(Arrays.asList(
                step0, step1, step2, step3, step4, step5, confirm, completed
        ));
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

        model.reset();
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
