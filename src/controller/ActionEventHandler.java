package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import mandelbrot.WritableGrid;

/**
 *
 * @author Tomirio
 */
public class ActionEventHandler implements EventHandler<ActionEvent> {

    private WritableGrid grid;

    /**
     *
     * @param grid the grid
     */
    public ActionEventHandler(WritableGrid grid) {
        this.grid = grid;

    }

    @Override
    public void handle(ActionEvent event) {
        Button b = (Button) event.getSource();
        if (b.getId() == "zoomButton") {
            System.out.println("Er werd op de zoom knop gedrukt!");

        }
    }

}
