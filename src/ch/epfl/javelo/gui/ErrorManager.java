package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Error Manager displays the error messages
 *
 * @author Temi Romeo Messmer (345250)
 * @author Thomas Marin Zamblera (344438)
 */
public final class ErrorManager {

    private static final float FADE_VALUE = 0.8f;
    private final VBox vBox;
    private final Text vBoxText;
    private SequentialTransition sequentialTransition;

    /**
     * Constructor of ErrorManager
     */
    public ErrorManager(){
        vBox = new VBox();
        vBoxText = new Text();

        vBox.setMouseTransparent(true);
        vBox.getStylesheets().add("error.css");
        vBox.getChildren().add(vBoxText);

        createTransition();
    }

    /**
     * Displays the error with a message and produces an error sound
     *
     * @param message error message to display
     */
    public void displayError(String message){
        sequentialTransition.stop();
        vBoxText.setText(message);
        sequentialTransition.play();
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    /**
     * Pane of the error messages
     *
     * @return pane of the error messages
     */
    public Pane pane(){
        return vBox;
    }

    private void createTransition(){
        FadeTransition fadeTransition1 = new FadeTransition(Duration.millis(200), vBox);
        fadeTransition1.setFromValue(0);
        fadeTransition1.setToValue(FADE_VALUE);

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(2000));

        FadeTransition fadeTransition2 = new FadeTransition(Duration.millis(500), vBox);
        fadeTransition2.setToValue(0);
        fadeTransition2.setFromValue(FADE_VALUE);

        sequentialTransition = new SequentialTransition(fadeTransition1, pauseTransition, fadeTransition2);
    }
}
