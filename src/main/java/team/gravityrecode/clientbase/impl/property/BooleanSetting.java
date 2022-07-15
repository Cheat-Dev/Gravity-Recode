package team.gravityrecode.clientbase.impl.property;

import team.gravityrecode.clientbase.api.client.IToggleable;
import team.gravityrecode.clientbase.api.property.Property;

public class BooleanSetting extends Property<Boolean> {
    public BooleanSetting(IToggleable owner, String name, Boolean value) {
        super(owner, name, value);
    }
}
