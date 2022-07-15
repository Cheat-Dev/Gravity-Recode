package team.gravityrecode.clientbase.impl.property;
import lombok.Getter;
import team.gravityrecode.clientbase.api.client.IToggleable;
import team.gravityrecode.clientbase.api.property.Property;
import team.gravityrecode.clientbase.impl.property.interfaces.INameable;

import java.util.Arrays;
import java.util.List;

@Getter
public class EnumSetting<T extends INameable> extends Property<T> {
    private final List<T> enumList;

    @SafeVarargs
    public EnumSetting(IToggleable owner, String name, T... modes) {
        super(owner, name, modes[0]);
        this.enumList = Arrays.asList(modes);
    }

    public void setValue(String value) {
        super.setValue(enumList.stream()
                .filter(mode -> mode.getName().equalsIgnoreCase(value))
                .findFirst()
                .orElse(this.enumList.get(0)));
    }

}
