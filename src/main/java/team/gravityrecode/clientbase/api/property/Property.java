package team.gravityrecode.clientbase.api.property;

import lombok.Getter;
import lombok.Setter;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.moduleBase.Module;

import java.util.function.BooleanSupplier;

@Getter
@Setter
public class Property<T> {

    private final Module owner;
    private final String name;
    protected T value;
    private BooleanSupplier visible;

    protected Property(Module owner, String name, T value, BooleanSupplier visible) {
        this.owner = owner;
        this.name = name;
        this.value = value;
        this.visible = visible;
        Client.INSTANCE.getPropertyManager().add(this);
    }

    public T getValue() { return value;}
}
