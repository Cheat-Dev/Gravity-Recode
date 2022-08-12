package team.gravityrecode.clientbase.impl.module.ghost;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.module.combat.Killaura;
import team.gravityrecode.clientbase.impl.property.BooleanSetting;
import team.gravityrecode.clientbase.impl.property.EnumSetting;
import team.gravityrecode.clientbase.impl.property.NumberSetting;
import team.gravityrecode.clientbase.impl.util.client.TimerUtil;
import team.gravityrecode.clientbase.impl.util.entity.EntityValidator;
import team.gravityrecode.clientbase.impl.util.math.MathUtil;
import team.gravityrecode.clientbase.impl.util.player.RotationUtil;

import java.util.List;
import java.util.function.BiFunction;

@ModuleInfo(moduleName = "AimAssist", moduleCategory = Module.ModuleCategory.COMBAT)
public class AimAssist extends Module {

    private NumberSetting aggressivity = new NumberSetting(this, "Smoothness", 15, 1, 120, 0.1);
    private NumberSetting range = new NumberSetting(this, "Range", 4.4, 2.8, 7.0, 0.1);
    private BooleanSetting lockView = new BooleanSetting(this, "Lock View", false);
    private final EnumSetting<RotationUtil.RotationsPoint> rotationsPointProperty = new EnumSetting<>(this, "Focus", RotationUtil.RotationsPoint.values());

    @EventHandler
    public void onUpdate(PlayerMotionEvent event) {
        EntityLivingBase entity = getMouseOverEntity();
        final Vec3 hitOrigin = RotationUtil.getHitOrigin(mc.thePlayer);
        final Vec3 attackHitVec = getAttackHitVec(hitOrigin, entity);
        float[] rotations = RotationUtil.getRotations(
                new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch}, lockView.getValue() ? 0 : aggressivity.getValue().floatValue(), hitOrigin, attackHitVec);
        if (entity != null && rotations != null && mc.gameSettings.keyBindAttack.isKeyDown()) {
            float yaw = rotations[0];
            float pitch = rotations[1];
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }
        if (entity.getHealth() < 0.5f || mc.thePlayer.getDistanceToEntity(entity) > range.getValue() || entity.isInvisible() || entity.isDead || entity.deathTime != 0
                || entity instanceof EntityArmorStand)
            entity = null;
    }

    public EntityLivingBase getMouseOverEntity() {
        EntityLivingBase entityLivingBase = null;
        if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof EntityLivingBase && !(mc.objectMouseOver.entityHit instanceof EntityArmorStand)) {
            entityLivingBase = (EntityLivingBase) mc.objectMouseOver.entityHit;
            if (!check(entityLivingBase)) {
                entityLivingBase = null;
            }
        }
        return entityLivingBase;
    }

    public boolean check(final EntityLivingBase in) {
        return !(in instanceof EntityAmbientCreature) && !(in instanceof EntityAgeable) && !(in instanceof EntityTameable) && !(in instanceof EntityWaterMob);
    }

    public Vec3 getAttackHitVec(final Vec3 hitOrigin, final EntityLivingBase entity) {
        final AxisAlignedBB boundingBox = RotationUtil.getHittableBoundingBox(entity, .1f);
        // Get optimal attack hit vec
        return RotationUtil.getAttackHitVec(mc, hitOrigin, boundingBox,
                this.rotationsPointProperty.getValue().getHitVec(hitOrigin, boundingBox),
                true, 5);
    }
}
