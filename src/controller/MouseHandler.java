package controller;

import view.View;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import mandelbrot.Point;
import mandelbrot.WritableGrid;
import static java.lang.Math.abs;

/**
 *
 * @author Tom
 */
public class MouseHandler implements EventHandler<MouseEvent> {

    /**
     *
     * The grid
     */
    private final WritableGrid grid;

    /**
     *
     * The initial x coordinate of the mouse when the mouse started dragging
     */
    private double mouseXInitial;

    /**
     *
     * The initial y coordinate of the mouse when the mouse started dragging
     */
    private double mouseYInitial;

    /**
     *
     * The last drawn rectangle on the screen
     */
    private Rectangle lastRectangle;

    /**
     *
     * Indicates if there is a drawn rectangle on the screen
     */
    private boolean rectanglePresent = false;

    /**
     *
     * @param g The grid
     */
    public MouseHandler(WritableGrid g) {
        grid = g;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            mouseXInitial = event.getSceneX();
            mouseYInitial = event.getSceneY();

        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && event.isPrimaryButtonDown()) {
            if (!event.isStillSincePress()) {
                double mouseXFinal = event.getSceneX();
                double mouseYFinal = event.getSceneY();
                double width = mouseXFinal - mouseXInitial;
                double height = mouseYFinal - mouseYInitial;

                Rectangle r;
                if (width < 0) {
                    if (height > 0) {
                        r = new Rectangle(mouseXFinal, mouseYInitial, abs(width), height);
                    } else {
                        r = new Rectangle(mouseXFinal, mouseYFinal, abs(width), abs(height));
                    }

                } else if (height > 0) {
                    r = new Rectangle(mouseXInitial, mouseYInitial, width, height);
                } else {
                    r = new Rectangle(mouseXInitial, mouseYFinal, width, abs(height));
                }
                r.setStroke(Color.rgb(230, 230, 250, 0.5));
                r.setFill(Color.rgb(30, 144, 255, 0.1));

                if (!rectanglePresent) {
                    View.drawSelection(r);
                    lastRectangle = r;
                    rectanglePresent = true;
                } else {
                    View.removeSelection(lastRectangle);
                    View.drawSelection(r);
                    lastRectangle = r;
                }
            }
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED && !event.isStillSincePress()) {
            String mousebutton = event.getButton().toString();
            if ("PRIMARY".equals(mousebutton)) {
                View.removeSelection(lastRectangle);
                rectanglePresent = false;
                double x = lastRectangle.getX() + 0.5 * lastRectangle.getWidth();
                double y = lastRectangle.getY() + 0.5 * lastRectangle.getHeight();
                Point p = grid.coordinates[(int) x][(int) y];
                grid.scalefactor *= (grid.gridW / lastRectangle.getWidth());
                grid.calcCoordinates(p.getX(), p.getY());
            }
        } else if (event.getEventType() == MouseEvent.MOUSE_CLICKED && event.isStillSincePress() && !event.isPrimaryButtonDown()) {

            String mousebutton = event.getButton().toString();
            switch (mousebutton) {
                case "PRIMARY": {
                    event.getButton();
                    double mouseX = event.getSceneX();
                    double mouseY = event.getSceneY();
                    //System.out.println("Mouse clicked, x:" + mouseX + " y:" + mouseY);
                    Point p = grid.coordinates[(int) mouseX][(int) mouseY];
                    //System.out.println("We gaan kijken naar het middelpunt met coordinaat x:" + p.a + " y:" + p.b);
                    grid.scalefactor *= 2;
                    grid.calcCoordinates(p.getX(), p.getY());
                    break;
                }
                case "SECONDARY": {
                    double mouseX = event.getSceneX();
                    double mouseY = event.getSceneY();
                    //System.out.println("Mouse clicked, x:" + mouseX + " y:" + mouseY);
                    Point p = grid.coordinates[(int) mouseX][(int) mouseY];
                    //System.out.println("We gaan kijken naar het middelpunt met coordinaat x:" + p.a + " y:" + p.b);
                    grid.scalefactor /= 2;
                    grid.calcCoordinates(p.getX(), p.getY());
                    break;
                }
            }
        }
    }
}
