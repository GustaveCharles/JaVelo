package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Represents an error message on screen
 */
public final class ErrorManager {

    /**
     * The opacity at the beginning of the transition and conversely
     */
    private static final double FROM_OPACITY = 0;

    /**
     * The opacity at the end of the transition and conversely
     */
    private static final double TO_OPACITY = 0.8;

    /**
     * The duration for the first animation
     */
    private static final double DURATION_TIME_1 = 0.2;

    /**
     * The duration for the last animation
     */
    private static final double DURATION_TIME_2 = 0.5;

    /**
     * The time the error message remains on screen
     */
    private static final int DISPLAY_DURATION = 2;

    /**
     * The style for the VBox
     */
    private static final String VBOX_STYLE = "error.css";

    private final VBox vBoxError;
    private final SequentialTransition sequentialTransition;

    /**
     * Manages the display of error messages
     */
    public ErrorManager() {
        vBoxError = new VBox();
        vBoxError.setMouseTransparent(true);
        vBoxError.getStylesheets().setAll(VBOX_STYLE);

        FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(DURATION_TIME_1), vBoxError);
        fadeTransition1.setFromValue(FROM_OPACITY);
        fadeTransition1.setToValue(TO_OPACITY);

        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(DISPLAY_DURATION));

        FadeTransition fadeTransition2 = new FadeTransition(Duration.seconds(DURATION_TIME_2), vBoxError);
        fadeTransition2.setFromValue(TO_OPACITY);
        fadeTransition2.setToValue(FROM_OPACITY);

        sequentialTransition = new SequentialTransition(fadeTransition1, pauseTransition, fadeTransition2);
    }

    /**
     * A getter for the error pane
     *
     * @return a pane
     */
    public Pane pane() {
        return vBoxError;
    }

    /**
     * Triggers all the transitions required for the error message animation on screen
     *
     * @param errorMessage the error message
     */
    public void displayError(String errorMessage) {
        sequentialTransition.stop();
        vBoxError.getChildren().clear();
        java.awt.Toolkit.getDefaultToolkit().beep();
        vBoxError.getChildren().add(new Text(errorMessage));
        sequentialTransition.play();
    }
}
