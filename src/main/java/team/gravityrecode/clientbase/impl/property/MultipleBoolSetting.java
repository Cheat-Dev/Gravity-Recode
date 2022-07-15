package team.gravityrecode.clientbase.impl.property;

import team.gravityrecode.clientbase.api.client.IToggleable;
import team.gravityrecode.clientbase.api.property.Property;

import java.util.Arrays;
import java.util.List;

public class MultipleBoolSetting extends Property<List<BooleanSetting>> {

    protected MultipleBoolSetting(IToggleable owner, String name, BooleanSetting... values) {
        super(owner, name, Arrays.asList(values));
    }

    public boolean isSelected(final String name) {
        final BooleanSetting selection = this.getValue().stream().filter(s -> s.getName().equals(name)).findAny().orElse(null);
        if (selection == null)
            return false;
        return selection.getValue();
    }
}