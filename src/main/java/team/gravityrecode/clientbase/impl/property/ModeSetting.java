package team.gravityrecode.clientbase.impl.property;
import lombok.Getter;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.property.Property;
import team.gravityrecode.clientbase.impl.property.mode.Mode;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

@Getter
public class ModeSetting extends Property<Mode> {

    private final List<Mode> modeList;

    public ModeSetting(Module owner, String name, BooleanSupplier visible, Mode... modes) {
        super(owner, name, modes[0], visible);
        this.modeList = Arrays.asList(modes);
        for(Mode mode : modes) {
            mode.setProperty(this);
        }
    }

    public ModeSetting(Module owner, String name, Mode... modes) {
        super(owner, name, modes[0], () -> true);
        this.modeList = Arrays.asList(modes);
        for(Mode mode : modes) {
            mode.setProperty(this);
        }
    }

    public void setValue(String value) {
        super.setValue(modeList.stream().filter(mode -> mode.getName().equalsIgnoreCase(value)).findFirst().orElse(this.modeList.get(0)));
    }

}
