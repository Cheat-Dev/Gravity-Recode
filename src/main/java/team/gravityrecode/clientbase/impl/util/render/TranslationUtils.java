package team.gravityrecode.clientbase.impl.util.render;

public class TranslationUtils {
    private double x;
    private double y;

    public TranslationUtils(float x, float y) {
        this.x = (double) x;
        this.y = (double) y;
    }

    public void interpolate(double x, double y, double smoothing) {
        this.x = AnimationUtils.animate(x, this.x, smoothing);
        this.y = AnimationUtils.animate(y, this.y, smoothing);
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }
}