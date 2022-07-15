package team.gravityrecode.clientbase.impl.property;

import lombok.Getter;
import team.gravityrecode.clientbase.api.client.IToggleable;
import team.gravityrecode.clientbase.api.property.Property;

import java.util.function.BooleanSupplier;

@Getter
public class NumberSetting extends Property<Double> {

    private final double min, max, increment;

    public NumberSetting(IToggleable owner, String name, double value, double min, double max, double increment, BooleanSupplier visible) {
        super(owner, name, value, visible);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public NumberSetting(IToggleable owner, String name, double value, double min, double max, double increment) {
        super(owner, name, value, () -> true);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public void setValue(double value) {
        value = Math.max(min, Math.min(max, value));
        super.setValue(value);
    }

}
