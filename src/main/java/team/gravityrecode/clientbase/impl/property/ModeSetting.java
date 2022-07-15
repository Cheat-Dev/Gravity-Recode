package team.gravityrecode.clientbase.impl.property;
import lombok.Getter;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.client.IToggleable;
import team.gravityrecode.clientbase.api.property.Property;
import team.gravityrecode.clientbase.impl.property.mode.Mode;

import java.util.Arrays;
import java.util.List;

@Getter
public class ModeSetting extends Property<Mode> {

    private final List<Mode> modeList;

    public ModeSetting(IToggleable owner, String name, Mode... modes) {
        super(owner, name, modes[0]);
        this.modeList = Arrays.asList(modes);
        for(Mode mode : modes) {
            mode.setProperty(this);
        }
    }

    public void setValue(String value) {
        if (getOwner().isEnabled())
            Client.INSTANCE.getPubSubEventBus().unsubscribe(this.getValue());
        super.setValue(modeList.stream().filter(mode -> mode.getName().equalsIgnoreCase(value)).findFirst().orElse(this.modeList.get(0)));
        if (getOwner().isEnabled())
            Client.INSTANCE.getPubSubEventBus().subscribe(this.getValue());
    }

}
