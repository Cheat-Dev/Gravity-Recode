package team.gravityrecode.clientbase.impl.property;
import team.gravityrecode.clientbase.api.client.IToggleable;
import team.gravityrecode.clientbase.api.property.Property;

import java.awt.Color;

public class ColorSetting extends Property<Color> {

    public ColorSetting(IToggleable owner, String name, Color value) {
        super(owner, name, value);
    }

    public int getColor() {
        return this.getValue().getRGB();
    }

}
