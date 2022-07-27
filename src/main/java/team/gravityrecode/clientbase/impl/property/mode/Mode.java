package team.gravityrecode.clientbase.impl.property.mode;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.property.Property;

import java.util.Objects;

@Getter
@Setter
public abstract class Mode {

    protected final Minecraft mc = Minecraft.getMinecraft();
    private final Module owner;
    private Property<?> property;
    private final String name;


    public Mode(Module owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getOwner() {
        return (T) owner;
    }

    public boolean isEnabled() {
        return Objects.equals(property.getValue(), this);
    }

    public final void init() {
        for (Property<?> property : Client.INSTANCE.getPropertyManager().get(this.getOwner())) {
            property.setVisible(() -> this.property.getValue() == this);
        }
    }

    public void onEnable() {
        Client.INSTANCE.getPubSubEventBus().subscribe(this);
    }

    public void onDisable() {
        Client.INSTANCE.getPubSubEventBus().unsubscribe(this);
    }

}
