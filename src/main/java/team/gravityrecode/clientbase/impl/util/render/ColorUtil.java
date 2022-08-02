package team.gravityrecode.clientbase.impl.util.render;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import me.jinthium.optimization.ApacheMath;

import java.awt.*;

@AllArgsConstructor
public class ColorUtil {

    public static double rainbowState;

    public static float getOffset(int index) {
        long ms = (long) (1.3 * 1000L);
        long currentMillis = -1;
        currentMillis = System.currentTimeMillis();
        final float offset = (currentMillis + (3 * 2 / (index + 1) * 50)) % ms / (ms / 2.0F);
        return offset;
    }

    public static int rainbow(int delay) {
        rainbowState = Math.ceil((double) ((System.currentTimeMillis() + delay) / 75L));
        rainbowState %= 90;
        return Color.getHSBColor((float) (rainbowState / 45.0), 0.2f, 1f).getRGB();
    }
    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = ApacheMath.min(1, ApacheMath.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount),
                interpolateInt(color1.getGreen(), color2.getGreen(), amount),
                interpolateInt(color1.getBlue(), color2.getBlue(), amount),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }
    public static Double interpolate(double oldValue, double newValue, double interpolationValue){
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

}
