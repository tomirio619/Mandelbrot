package mandelbrot;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import javafx.scene.paint.Color;

/**
 *
 * @author Tom
 */
public class Point {

    /**
     *
     * The original x coordinate of the point
     */
    protected final double a;

    /**
     *
     * The original y coordinate of the point
     */
    protected final double b;

    /**
     *
     * The converged x value after the calculations are applied
     */
    protected double xn;

    /**
     *
     * The converged y value after the calculations are applied
     */
    protected double yn;

    /**
     *
     * The maximum number of iterations The maximum number of iterations is
     * 9500. Extra heap space must be assigned: Project properties -> Run -> VM
     * Options Enter "-Xms512m" as argument (without quotes).
     */
    public static int maxIter = 6000;

    /**
     *
     * @param a x coordinate of the point
     * @param b y coordinate of the point
     */
    public Point(double a, double b) {
        this.a = a;
        this.b = b;
        this.xn = a;
        this.yn = b;
    }

    /**
     *
     * @return The mandelvalue of the point
     */
    public int getMandelValue() {
        if (isInCardioid() || isInBulb2()) {
            return maxIter;
        } else {
            return mandelValue(a, b, 0);
        }
    }

    /**
     *
     * @param x x value of the point (the calculation may or may not be applied)
     * @param y y value of the point (the calculation may or may not be applied)
     * @param counter The current iteration
     * @return The mandelvalue of this point
     */
    public int mandelValue(double x, double y, int counter) {
        double x2 = x * x;
        double y2 = y * y;
        if (x2 + y2 > 4.0) {
            return counter;
        } else if (counter >= maxIter) {
            return maxIter;
        } else {
            xn = x2 - y2 + a;
            yn = 2.0 * x * y + b;
            return mandelValue(xn, yn, counter + 1);
        }

    }

    /**
     *
     * @return  <code>true</code> If the point is within the main cardioid
     * <code>false</code> otherwise
     */
    public boolean isInCardioid() {
        double p = sqrt(pow(a - 0.25, 2) + b * b);
        double q = p - 2 * p * p + 0.25;
        return a < q;
    }

    /**
     *
     * @return <code>true</code> If the point is within the period-2-bulb
     * <code>false</code> otherwise
     */
    public boolean isInBulb2() {
        double z = pow(a + 1, 2) + b * b;
        return z < (1 / 16);
    }

    /**
     *
     * @return Smooth colouring by applying Berstein Polynomials to get the
     * color responding to the point's mandelvalue
     */
    public Color bersteinColouring() {
        int mandelValue = getMandelValue();
        double t = (double) mandelValue / (double) maxIter;
        int red = (int) (9.0 * (1 - t) * t * t * t * 255.0);
        int green = (int) (15.0 * (1 - t) * (1 - t) * t * t * 255.0);
        int blue = (int) (8.5 * (1 - t) * (1 - t) * (1 - t) * t * 255.0);
        return Color.rgb(red, green, blue);
    }

    /**
     *
     * @return x coordinate
     */
    public double getX() {
        return a;
    }

    /**
     *
     * @return y coordinate
     */
    public double getY() {
        return b;
    }
    
    /**
     *
     * @return String for this class
     */
    @Override
    public String toString() {
        return "x:" + a + " y:" + b;
    }
}
