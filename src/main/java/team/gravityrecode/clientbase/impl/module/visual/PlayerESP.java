package team.gravityrecode.clientbase.impl.module.visual;

import me.jinthium.optimization.ApacheMath;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.render.Render3DEvent;
import team.gravityrecode.clientbase.impl.util.render.RenderUtil;
import team.gravityrecode.clientbase.impl.util.render.animations.Animation;
import team.gravityrecode.clientbase.impl.util.render.animations.Direction;
import team.gravityrecode.clientbase.impl.util.render.animations.SmoothStep;
import team.gravityrecode.clientbase.impl.util.render.animations.impl.EaseInOutRect;
import team.gravityrecode.clientbase.impl.util.render.secondary.RenderUtils;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(moduleName = "PlayerESP", moduleCategory = Module.ModuleCategory.VISUAL)
public class PlayerESP extends Module {

    Animation animation;

    @EventHandler
    public void onRender3D(Render3DEvent event){
        if (mc.thePlayer.ticksExisted <= 5) return;
        for (Entity entityLivingBase : mc.theWorld.getLoadedEntityList()) {
            if (entityLivingBase instanceof EntityPlayer && (entityLivingBase != mc.thePlayer || mc.gameSettings.thirdPersonView != 0)) {
            drawESP(entityLivingBase, new Color(0, 255, 255).getRGB());
            }
        }
    }

    public void drawESP(Entity entity, int colour){
        animation = new EaseInOutRect(200, 1);
        animation.setDirection(Direction.FORWARDS);
        EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
        GlStateManager.pushMatrix();
        double x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, mc.timer.renderPartialTicks);
        double y = RenderUtil.interpolate(entity.posY, entity.lastTickPosY, mc.timer.renderPartialTicks);
        double z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, mc.timer.renderPartialTicks);
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate((-RenderManager.playerViewY), 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(-0.1, -0.1, 0.1);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(true);
        RenderUtils.drawBorderedRect(-6f, -20, 6f, -1, 0.125f, 0 , new Color(92, 6, 7).getRGB());
        RenderUtils.drawRect(-6.2f, -20, -6f, -1, new Color(1, 0, 1).getRGB());
        RenderUtils.drawRect(-6.2f, (float) -20 / (entityLivingBase.getMaxHealth() / entityLivingBase.getHealth() / 2) / 2, -6f, -1, new Color(21, 154, 16).getRGB());
//        GlStateManager.enableTexture2D();
        GL11.glDisable(3042);
        glPopMatrix();
    }
}
