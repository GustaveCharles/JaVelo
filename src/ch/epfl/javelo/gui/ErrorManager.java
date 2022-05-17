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
    private static final VBox vBoxError = new VBox();
    //

    public Pane pane(){
        return vBoxError;
    }

    public void displayError(String errorMessage){
        java.awt.Toolkit.getDefaultToolkit().beep();
        vBoxError.getChildren().add(new Text(errorMessage));
        vBoxError.getStylesheets().setAll("error.css");
        vBoxError.setMouseTransparent(true);
        FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.2), vBoxError);
        fadeTransition1.setFromValue(0);
        fadeTransition1.setToValue(0.8);
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
        FadeTransition fadeTransition2 = new FadeTransition(Duration.seconds(0.5), vBoxError);
        fadeTransition2.setFromValue(0.8);
        fadeTransition2.setToValue(0);

        SequentialTransition sequentialTransition = new SequentialTransition(fadeTransition1, pauseTransition, fadeTransition2);

        if (sequentialTransition.getStatus() == Animation.Status.RUNNING) {
            sequentialTransition.stop();
        } else {
            sequentialTransition.play();
        }
    }
}
