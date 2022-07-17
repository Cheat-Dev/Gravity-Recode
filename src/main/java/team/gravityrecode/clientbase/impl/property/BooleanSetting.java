package team.gravityrecode.clientbase.impl.property;

import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.property.Property;

import java.util.function.BooleanSupplier;

public class BooleanSetting extends Property<Boolean> {
    public BooleanSetting(Module owner, String name, Boolean value, BooleanSupplier visible) {
        super(owner, name, value, visible);
    }

    public BooleanSetting(Module owner, String name, Boolean value){
        this(owner, name, value, () -> true);
    }
}
