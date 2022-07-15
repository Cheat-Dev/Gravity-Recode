package team.gravityrecode.clientbase.impl.util.util.entity;

import net.minecraft.entity.Entity;
@FunctionalInterface
public interface ICheck {
    boolean validate(Entity entity);
}