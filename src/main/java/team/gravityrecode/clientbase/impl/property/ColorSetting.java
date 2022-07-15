package team.gravityrecode.clientbase.impl.property;
import team.gravityrecode.clientbase.api.client.IToggleable;
import team.gravityrecode.clientbase.api.property.Property;

import java.awt.Color;
import java.util.function.BooleanSupplier;

public class ColorSetting extends Property<Color> {

    public ColorSetting(IToggleable owner, String name, Color value, BooleanSupplier visible) {
        super(owner, name, value, visible);
    }

    public ColorSetting(IToggleable owner, String name, Color value) {
        this(owner, name, value, () -> true);
    }

    public int getColor() {
        return this.getValue().getRGB();
    }

}
