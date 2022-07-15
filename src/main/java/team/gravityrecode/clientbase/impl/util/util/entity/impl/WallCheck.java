package team.gravityrecode.clientbase.impl.util.util.entity.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import team.gravityrecode.clientbase.impl.util.util.entity.ICheck;

public final class WallCheck implements ICheck {
    @Override
    public boolean validate(Entity entity) {
        return Minecraft.getMinecraft().thePlayer.canEntityBeSeen(entity);
    }
}
