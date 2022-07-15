package team.gravityrecode.clientbase.api.property;

import lombok.Getter;
import lombok.Setter;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.client.IToggleable;

import java.util.function.BooleanSupplier;

@Getter
@Setter
public class Property<T> {

    private final IToggleable owner;
    private final String name;
    private T value;
    private BooleanSupplier visible;

    protected Property(IToggleable owner, String name, T value) {
        this.owner = owner;
        this.name = name;
        this.value = value;
        this.visible = () -> true;
        Client.INSTANCE.getPropertyManager().add(this);
    }

}
