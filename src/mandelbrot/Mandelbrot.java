package mandelbrot;

import view.View;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author Tom
 */
public class Mandelbrot extends Application {

    @Override
    public void start(Stage primaryStage) {
        /**
         *
         * The width of the screen, chunkSIZE in queue must be a divisor of this
         * value.
         */
        int width = 500;

        /**
         *
         * The height of the screen, chunkSIZE in queue must be a divisor of
         * this value
         */
        int height = 500;

        WritableGrid grid = new WritableGrid(width, height);
        View view = new View(primaryStage, grid, width, height);
        grid.addObserver(view);
        view.initiate(0.02, -0.01);
    }

    /**
     *
     * @param args Arguments passed to main
     */
    public static void main(String[] args) {
        launch();
    }

}
