package team.gravityrecode.clientbase.impl.module.visual;

import me.jinthium.optimization.ApacheMath;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.render.Render3DEvent;
import team.gravityrecode.clientbase.impl.util.render.RenderUtil;

import java.awt.*;

@ModuleInfo(moduleName = "PlayerESP", moduleCategory = Module.ModuleCategory.VISUAL)
public class PlayerESP extends Module {

    @EventHandler
    public void onRender3D(Render3DEvent event){
        if (mc.thePlayer.ticksExisted <= 5) return;
        for (Entity entityLivingBase : mc.theWorld.getLoadedEntityList()) {
            if (entityLivingBase instanceof EntityPlayer && !entityLivingBase.isInvisible() && (entityLivingBase != mc.thePlayer || mc.gameSettings.thirdPersonView != 0)) {
            drawESP(entityLivingBase, new Color(0, 255, 255).getRGB());
            }
        }
    }

    public void drawESP(Entity entity, int colour){
        GlStateManager.pushMatrix();
//        GlStateManager.translate(posX, posY, posZ);
        GL11.glNormal3f(0.0f, 0.0f, 0.0f);
        final double x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, mc.timer.renderPartialTicks);
        final double y = RenderUtil.interpolate(entity.posY, entity.lastTickPosY, mc.timer.renderPartialTicks);
        final double z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, mc.timer.renderPartialTicks);
                GlStateManager.translate(x, y, z);
        GlStateManager.rotate((-RenderManager.playerViewY), 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(-0.1, -0.1, 0.1);
        GL11.glDisable(2896);
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(true);
        Gui.drawRect(4.0f, -20.0f, 7.0f, -19.0f, colour);
        Gui.drawRect(-7.0f, -20.0f, -4.0f, -19.0f, colour);
        Gui.drawRect(6.0f, -20.0f, 7.0f, -17.5f, colour);
        Gui.drawRect(-7.0f, -20.0f, -6.0f, -17.5f, colour);
        Gui.drawRect(-7.0f, 2.0f, -4.0f, 3.0f, colour);
        Gui.drawRect(4.0f, 2.0f, 7.0f, 3.0f, colour);
        Gui.drawRect(-7.0f, 0.5f, -6.0f, 3.0f, colour);
        Gui.drawRect(6.0f, 0.5f, 7.0f, 3.0f, colour);
        Gui.drawRect(7.0f, -20.0f, 7.3f, -17.5f, -16777216);
        Gui.drawRect(-7.3f, -20.0f, -7.0f, -17.5f, -16777216);
        Gui.drawRect(4.0f, -20.3f, 7.3f, -20.0f, -16777216);
        Gui.drawRect(-7.3f, -20.3f, -4.0f, -20.0f, -16777216);
        Gui.drawRect(-7.0f, 3.0f, -4.0f, 3.3f, -16777216);
        Gui.drawRect(4.0f, 3.0f, 7.0f, 3.3f, -16777216);
        Gui.drawRect(-7.3f, 0.5f, -7.0f, 3.3f, -16777216);
        Gui.drawRect(7.0f, 0.5f, 7.3f, 3.3f, -16777216);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glEnable(2896);
        GlStateManager.popMatrix();
    }
}
