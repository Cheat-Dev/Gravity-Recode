package team.gravityrecode.clientbase.impl.property;

import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.property.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

public class MultipleBoolSetting extends Property<List<MultiBoolean>> {

    public MultipleBoolSetting(Module owner, String name, BooleanSupplier visible, ArrayList<MultiBoolean> values) {
        super(owner, name, values, visible);
    }

    public MultipleBoolSetting(Module owner, String name, ArrayList<MultiBoolean> values){
        this(owner, name, () -> true, values);
    }

    public boolean isSelected(final String name) {
        final MultiBoolean selection = this.getValue().stream().filter(s -> s.getName().equals(name)).findAny().orElse(null);
        if (selection == null)
            return false;
        return selection.getValue();
    }
}