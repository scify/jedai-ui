package utils.console_area;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Helper class to show an OutputStream in a JavaFX TextArea
 * 
 * Source: http://stackoverflow.com/q/13841884
 */
public class ConsoleArea extends OutputStream {
    private TextArea output;

    public ConsoleArea(TextArea ta) {
        this.output = ta;
    }

    @Override
    public void write(int i) throws IOException {
        Platform.runLater(() -> output.appendText(String.valueOf((char) i)));
    }
}
