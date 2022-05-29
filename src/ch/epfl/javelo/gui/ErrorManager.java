package ch.epfl.javelo.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public final class ErrorManager {

    private static final double FROM_OPACITY = 0;
    private static final double TO_OPACITY = 0.8;
    private static final double DURATION_TIME_1 = 0.2;
    private static final double DURATION_TIME_2 = 0.5;
    private static final String VBOX_STYLE = "error.css";
    private final VBox vBoxError;
    private final SequentialTransition sequentialTransition;

    public ErrorManager() {
        vBoxError = new VBox();
        vBoxError.setMouseTransparent(true);
        vBoxError.getStylesheets().setAll(VBOX_STYLE);

        FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(DURATION_TIME_1), vBoxError);
        fadeTransition1.setFromValue(FROM_OPACITY);
        fadeTransition1.setToValue(TO_OPACITY);

        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeTransition2 = new FadeTransition(Duration.seconds(DURATION_TIME_2), vBoxError);
        fadeTransition2.setFromValue(TO_OPACITY);
        fadeTransition2.setToValue(FROM_OPACITY);

        sequentialTransition = new SequentialTransition(fadeTransition1, pauseTransition, fadeTransition2);
    }

    public Pane pane() {
        return vBoxError;
    }

    public void displayError(String errorMessage) {
        sequentialTransition.stop();
        vBoxError.getChildren().clear();
        java.awt.Toolkit.getDefaultToolkit().beep();
        vBoxError.getChildren().add(new Text(errorMessage));
        sequentialTransition.play();
    }
}
