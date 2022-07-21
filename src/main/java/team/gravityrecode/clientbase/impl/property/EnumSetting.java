package team.gravityrecode.clientbase.impl.property;
import lombok.Getter;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.property.Property;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

@Getter
public class EnumSetting<T> extends Property<T> {
    private final List<T> enumList;

    @SafeVarargs
    public EnumSetting(Module owner, String name, BooleanSupplier visible, T... modes) {
        super(owner, name, modes[0], visible);
        this.enumList = Arrays.asList(modes);
    }

    public EnumSetting(Module owner, String name, T... modes) {
        this(owner, name, () -> true, modes[0]);
    }

    public void setValue(String value) {
        super.setValue(enumList.stream()
                .filter(mode -> mode.toString().equalsIgnoreCase(value))
                .findFirst()
                .orElse(this.enumList.get(0)));
    }

}
