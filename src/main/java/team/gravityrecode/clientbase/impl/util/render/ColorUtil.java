package team.gravityrecode.clientbase.impl.util.render;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import me.jinthium.optimization.ApacheMath;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.impl.module.visual.Hud;
import team.gravityrecode.clientbase.impl.property.interfaces.INameable;

import java.awt.*;

@AllArgsConstructor
public class ColorUtil {

    public static double rainbowState;
    public static int offset;
    public static ColorType colorType;
    public static Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);

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

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static int getGradientOffset(Color color1, Color color2, double offset) {
        if (offset > 1) {
            double left = offset % 1;
            int off = (int) offset;
            offset = off % 2 == 0 ? left : 1 - left;
        }
        double inverse_percent = 1 - offset;
        int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offset);
        int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offset);
        int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart).getRGB();
    }

    @AllArgsConstructor
    public enum ColorType {
        CUSTOM(hud.color.getValue().getRGB()),
        RAINBOW(rainbow(8 * offset)),
        GRADIENT(getGradientOffset(hud.color.getValue(), hud.color.getValue().darker(), offset));
        int color;

        public int getColor() {
            return color;
        }
    }

    public static int getHudColor(ColorType colorType, int offsetted) {
        offset = offsetted;
        colorType = getCurrentColourType();
        switch (hud.colourMode.getValue()) {
            case CUSTOM:
                colorType = ColorType.CUSTOM;
                break;
            case GRADIENT:
                colorType = ColorType.GRADIENT;
                break;
            case RAINBOW:
                colorType = ColorType.RAINBOW;
                break;
        }
        return colorType.color;
    }

    public static ColorType getCurrentColourType(){
        switch (hud.colourMode.getValue()){
            case CUSTOM:
                colorType = ColorType.CUSTOM;
                break;
            case RAINBOW:
                colorType = ColorType.RAINBOW;
                break;
            case GRADIENT:
                colorType = ColorType.GRADIENT;
                break;
        }
        return colorType;
    }
}
