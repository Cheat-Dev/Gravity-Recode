package team.gravityrecode.clientbase.impl.manager;

import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.property.Property;
import team.gravityrecode.clientbase.impl.property.ModeSetting;
import team.gravityrecode.clientbase.impl.property.mode.Mode;

import java.util.Objects;
import java.util.stream.Stream;

public class PropertyManager extends AbstractManager<Property<?>> {

    @Override
    public void init() {
        this.stream()
                .filter(property -> property instanceof ModeSetting)
                .map(property -> (ModeSetting) property)
                .forEach(modeSetting -> modeSetting.getModeList()
                        .forEach(Mode::init));
    }

    @Override
    public <U extends Property<?>> U getByName(String name) {
        throw new RuntimeException("Use PropertyManager#get(IToggleable, String) instead.");
    }

    public Property<?> get(Module toggleable, String name) {
        return this.getStream(toggleable)
                .filter(property -> property.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Property<?>[] get(Module toggleable) {
        return this.getStream(toggleable)
                .filter(property -> Objects.equals(property.getOwner(), toggleable))
                .toArray(Property[]::new);
    }

    private Property<?>[] getDisplayableSettings(Module toggleable) {
        return this.getStream(toggleable)
                .filter(option -> option.getVisible().getAsBoolean())
                .toArray(Property[]::new);
    }

    public Stream<Property<?>> getStream(Module toggleable) {
        return this.stream().filter(property -> Objects.equals(property.getOwner(), toggleable));
    }

}
