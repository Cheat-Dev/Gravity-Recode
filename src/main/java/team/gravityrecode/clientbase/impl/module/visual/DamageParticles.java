package team.gravityrecode.clientbase.impl.module.visual;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.api.notification.Notification;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.event.render.BenchmarkEvent;
import team.gravityrecode.clientbase.impl.event.render.Render3DEvent;
import team.gravityrecode.clientbase.impl.module.combat.Killaura;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.math.MathUtil;
import team.gravityrecode.clientbase.impl.util.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

//made by jinthium
@ModuleInfo(moduleName = "DamageParticles", moduleCategory = Module.ModuleCategory.VISUAL)
public class DamageParticles extends Module {

    private final List<DamageParticle> damageParticles = new ArrayList<>();
    private float lastHealth;
    private EntityLivingBase lastTarget;

    @EventHandler
    public void onUpdate(PlayerMotionEvent event) {
        EntityLivingBase entity = Client.INSTANCE.getModuleManager().<Killaura>getModule(Killaura.class).getTarget() != null ? Client.INSTANCE.getModuleManager().<Killaura>
                getModule(Killaura.class).getTarget() : mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null ? mc.objectMouseOver.entityHit instanceof
                EntityLivingBase ? (EntityLivingBase) mc.objectMouseOver.entityHit : lastTarget : lastTarget;
        if (entity != null) {
            if (lastTarget == entity && entity.getHealth() != lastHealth || entity.hurtTime > 10) {
                float healthDifference = entity.getHealth() - lastHealth;
                damageParticles.add(new DamageParticle(entity, healthDifference));
                lastHealth = entity.getHealth();
            }
            lastHealth = entity.getHealth();
            lastTarget = entity;
        } else {
            lastHealth = 20;
            lastTarget = null;
        }
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        damageParticles.forEach(damageParticle -> damageParticle.render(event));
        damageParticles.removeIf(damageParticle -> System.currentTimeMillis() - damageParticle.startTime > damageParticle.displayTime);
    }

    @Override
    public void onDisable() {
        damageParticles.clear();
        super.onDisable();
    }

    class DamageParticle {

        private float damage;
        private final float xOff;
        private final float yOff;
        private final float zOff;
        private final EntityLivingBase entity;
        private final long startTime;
        private final long displayTime;

        public DamageParticle(final EntityLivingBase entity, final float damage) {
            startTime = System.currentTimeMillis();
            displayTime = 1500;
            this.entity = entity;
            this.damage = damage;
            xOff = MathUtil.randomFloat(-0.5f, 0.5f);
            yOff = MathUtil.randomFloat(0, 1.5f);
            zOff = MathUtil.randomFloat(-0.5f, 0.5f);
        }

        public void render(Render3DEvent event) {
            glPushMatrix();
            glEnable(GL_BLEND);
            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);
            mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);
            double x = RenderUtil.interpolate(entity.isEntityAlive() ? entity.posX : entity.lastTickPosX, entity.lastTickPosX, event.getPartialTicks()) - mc.getRenderManager().viewerPosX;
            double y = RenderUtil.interpolate(entity.isEntityAlive() ? entity.posY : entity.lastTickPosY, entity.lastTickPosY, event.getPartialTicks()) - mc.getRenderManager().viewerPosY;
            double z = RenderUtil.interpolate(entity.isEntityAlive() ? entity.posZ : entity.lastTickPosZ, entity.lastTickPosZ, event.getPartialTicks()) - mc.getRenderManager().viewerPosZ;
            long timeDifference = System.currentTimeMillis() - startTime;
            double size = MathHelper.clamp_double(timeDifference / (float) displayTime * 2, 0, 1);
            glTranslated(x + xOff, y + yOff, z + zOff);
            glRotated(-mc.getRenderManager().playerViewY, 0, 1, 0);
            glRotated(mc.getRenderManager().playerViewX, (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0, 0);
            glScaled(-0.018 * size, -0.018 * size, 0.018 * size);
            damage = (float) MathUtil.round(damage, 2);
            String damageString = (damage > 0 ? "+" : "") + damage;
            Color color = Color.GREEN;
            if (damage < 0) {
                if (damage < -1) color = Color.YELLOW;
                if (damage < -1.5) color = Color.ORANGE;
                if (damage < -2.5) color = Color.RED;
            }
            Fonts.INSTANCE.getUbuntu_light().drawStringWithShadow(damageString, -Fonts.INSTANCE.getUbuntu_light().getStringWidth(damageString) / 2f, 0, color.getRGB());
            glScaled(1, 1, 1);
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_BLEND);
            glPopMatrix();
        }

        public float damage() {
            return damage;
        }

        public float displayTime() {
            return displayTime;
        }

        public float currentTime() {
            return startTime;
        }
    }
}
