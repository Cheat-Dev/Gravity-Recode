package team.gravityrecode.clientbase.impl.property;

import team.gravityrecode.clientbase.api.client.IToggleable;
import team.gravityrecode.clientbase.api.property.Property;

import java.util.function.BooleanSupplier;

public class MultiBoolean extends Property<Boolean> {
    public MultiBoolean(IToggleable owner, String name, Boolean value, BooleanSupplier visible) {
        super(owner, name, value, visible);
    }

    public MultiBoolean(IToggleable owner, String name, Boolean value){
        this(owner, name, value, () -> true);
    }
}
