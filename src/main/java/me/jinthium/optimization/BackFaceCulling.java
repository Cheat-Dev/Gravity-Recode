package me.jinthium.optimization;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;

public class BackFaceCulling {
    private static boolean shouldCull = true;

    public static void backFaceCullingStart(Entity entity) {
        shouldCull = entity instanceof EntityPlayer;

        if (shouldCull) {
            GlStateManager.enableCull();
        } else {
            GlStateManager.disableCull();
        }
    }

    public static void backFaceCullingEnd() {
        if (shouldCull) {
            GlStateManager.disableCull();
        } else {
            GlStateManager.enableCull();
        }
    }

    public static float getVisibleHeight(Entity entity) {
        return entity instanceof EntityZombie && ((EntityZombie) entity).isChild() ? entity.height / 2 : entity.height;
    }
}