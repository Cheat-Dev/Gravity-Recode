package team.gravityrecode.clientbase.impl.util.util.entity.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import team.gravityrecode.clientbase.impl.util.util.entity.ICheck;

public final class VoidCheck implements ICheck {
    @Override
    public boolean validate(Entity entity) {
        return isBlockUnder(entity);
    }

    private boolean isBlockUnder(Entity entity) {
        for (int offset = 0; offset < entity.posY + entity.getEyeHeight(); offset += 2) {
            AxisAlignedBB boundingBox = entity.getEntityBoundingBox().offset(0, -offset, 0);

            if (!Minecraft.getMinecraft().theWorld.getCollidingBoundingBoxes(entity, boundingBox).isEmpty()) {
                return true;
            }
        }

        return false;
    }
}
