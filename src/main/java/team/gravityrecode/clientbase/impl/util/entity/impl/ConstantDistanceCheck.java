package team.gravityrecode.clientbase.impl.util.entity.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import team.gravityrecode.clientbase.impl.util.entity.ICheck;

public final class ConstantDistanceCheck implements ICheck {
    private final float distance;

    public ConstantDistanceCheck(final float distance) {
        this.distance = distance;
    }


    @Override
    public boolean validate(Entity entity) {
        return Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity) <= distance;
    }
}
