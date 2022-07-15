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
    protected T value;
    private BooleanSupplier visible;

    protected Property(IToggleable owner, String name, T value, BooleanSupplier visible) {
        this.owner = owner;
        this.name = name;
        this.value = value;
        this.visible = visible;
        Client.INSTANCE.getPropertyManager().add(this);
    }

    public T getValue() { return value;}
}
