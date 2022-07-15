package team.gravityrecode.clientbase.impl.property.mode;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.client.IToggleable;
import team.gravityrecode.clientbase.api.property.Property;

import java.util.Objects;

@Getter
@Setter
public abstract class Mode implements IToggleable {

    protected final Minecraft mc = Minecraft.getMinecraft();
    private final IToggleable owner;
    private Property<?> property;
    private final String name;


    public Mode(IToggleable owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    @SuppressWarnings("unchecked")
    public <T extends IToggleable> T getOwner() {
        return (T) owner;
    }

    @Override
    public boolean isEnabled() {
        return Objects.equals(property.getValue(), this);
    }

    public final void init() {
        for (Property<?> property : Client.INSTANCE.getPropertyManager().get(this)) {
            property.setVisible(() -> this.property.getValue() == this);
        }
    }

    public void onEnable() {}

    public void onDisable() {}

}
