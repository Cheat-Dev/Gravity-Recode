package team.gravityrecode.clientbase.impl.util.render.animations.impl;

import team.gravityrecode.clientbase.impl.util.render.animations.Animation;
import team.gravityrecode.clientbase.impl.util.render.animations.Direction;
import me.jinthium.optimization.ApacheMath;

public class EaseInOutRect extends Animation {

    public EaseInOutRect(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public EaseInOutRect(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    protected double getEquation(double x1) {
        double x = x1 / duration;
        return x < 0.5 ? 2 * ApacheMath.pow(x, 2) : 1 - ApacheMath.pow(-2 * x + 2, 2) / 2;
    }

}
