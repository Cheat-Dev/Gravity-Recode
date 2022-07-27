package team.gravityrecode.clientbase.impl.module.combat;

import me.jinthium.optimization.ApacheMath;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.player.PlayerJumpEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerStrafeEvent;
import team.gravityrecode.clientbase.impl.event.player.UpdateLookEvent;
import team.gravityrecode.clientbase.impl.event.render.Render3DEvent;
import team.gravityrecode.clientbase.impl.module.movement.Speed;
import team.gravityrecode.clientbase.impl.property.*;
import team.gravityrecode.clientbase.impl.property.interfaces.INameable;
import team.gravityrecode.clientbase.impl.util.util.client.TimerUtil;
import team.gravityrecode.clientbase.impl.util.util.entity.EntityValidator;
import team.gravityrecode.clientbase.impl.util.util.entity.impl.AliveCheck;
import team.gravityrecode.clientbase.impl.util.util.entity.impl.ConstantDistanceCheck;
import team.gravityrecode.clientbase.impl.util.util.entity.impl.EntityCheck;
import team.gravityrecode.clientbase.impl.util.util.entity.impl.TeamsCheck;
import team.gravityrecode.clientbase.impl.util.util.math.MathUtil;
import team.gravityrecode.clientbase.impl.util.util.network.PacketUtil;
import team.gravityrecode.clientbase.impl.util.util.player.MovementUtil;
import team.gravityrecode.clientbase.impl.util.util.player.PlayerUtil;
import team.gravityrecode.clientbase.impl.util.util.player.RotationUtil;
import team.gravityrecode.clientbase.impl.util.util.render.RenderUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.util.*;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(moduleName = "KillAura", moduleCategory = Module.ModuleCategory.COMBAT)
public class Killaura extends Module {

    static double[] y1 = {0.104080378093037, 0.105454222033912, 0.102888018147468, 0.099634532004642};

    private final EnumSetting<KillAuraMode> modeProperty = new EnumSetting<>(this, "Mode", KillAuraMode.values());

    private final NumberSetting switchDelayProperty = new NumberSetting(this, "Switch Delay", 50, 1, 1000, 20, () -> modeProperty.getValue() == KillAuraMode.SWITCH);
    private final EnumSetting<SortMode> sortProperty = new EnumSetting<>(this, "Sort by", () -> modeProperty.getValue() == KillAuraMode.SINGLE, SortMode.values());
    public int waitDelay, groundTicks;
    public boolean crits;
    private final NumberSetting minAPSProperty = new NumberSetting(this, "Min APS", 11, 1, 20, 1);
    private final NumberSetting maxAPSProperty = new NumberSetting(this, "Max APS", 14, 1, 20, 1);

    private final EnumSetting<AttackMode> attackModeProperty = new EnumSetting<>(this, "Attack In", AttackMode.values());
    private final EnumSetting<BlockMode> blockModeProperty = new EnumSetting<>(this, "Block Mode", BlockMode.values());
    public final EnumSetting<RotationsMode> rotationsProperty = new EnumSetting<>(this, "Rotations", RotationsMode.values());
    private final BooleanSetting onlyAttackWhenLookingAtEntityLongNameMethod = new BooleanSetting(this, "Attack When Looking", false, () -> rotationsProperty.getValue() != RotationsMode.UNDETECTABLE);
    private final EnumSetting<RotationUtil.RotationsPoint> rotationsPointProperty = new EnumSetting<>(this, "Rotations Point", RotationUtil.RotationsPoint.CLOSEST);

    private final NumberSetting rangeProperty = new NumberSetting(this, "Range", 4.2, 1, 7, 0.1);
    public final NumberSetting maxTargets = new NumberSetting(this, "Max Targets", 4, 1, 10, 1, () -> modeProperty.getValue() == KillAuraMode.MULTI);
    private final NumberSetting blockRangeProperty = new NumberSetting(this, "Block Range", 4.2, 1, 10, 0.1);
    private final NumberSetting fovRangeProperty = new NumberSetting(this, "Fov Range", 180, 1, 180, 10);

    private final MultipleBoolSetting targetsProperty = new MultipleBoolSetting(this, "Targets", new MultiBoolean(this, "Players", true),
            new MultiBoolean(this, "Mobs", false), new MultiBoolean(this, "Animals", false),
            new MultiBoolean(this, "Invisibles", false), new MultiBoolean(this, "Dead", false), new MultiBoolean(this, "Teams", false));

    private final BooleanSetting rayTraceProperty = new BooleanSetting(this, "Ray Trace", false);
    public final BooleanSetting lockViewProperty = new BooleanSetting(this, "Lock View", false);
    private final BooleanSetting autoBlockProperty = new BooleanSetting(this, "Autoblock", true);
    private final BooleanSetting noSwingProperty = new BooleanSetting(this, "No Swing", false);
    private final BooleanSetting keepSprintProperty = new BooleanSetting(this, "Keep Sprint", true);
    private final TimerUtil switchTimer = new TimerUtil();
    private final TimerUtil attackTimer = new TimerUtil();
    private final TimerUtil blockTimer = new TimerUtil();
    public float[] rotationStore;
    public boolean block, blockAnimation, changeTarget;
    private boolean rotating, hasNotThing;
    private int targetIndex, ticks;

    public final List<EntityLivingBase> targets = new ArrayList<>();
    public final List<EntityLivingBase> multiTargets = new ArrayList<>();
    public EntityValidator entityValidator = new EntityValidator(), blockValidator = new EntityValidator();
    public EntityLivingBase target;

    @EventHandler
    public void onPlayerMotion(PlayerMotionEvent event) {
        if (mc.thePlayer.ticksExisted < 5)
            return;
        entityValidator = new EntityValidator();
        blockValidator = new EntityValidator();
        final AliveCheck aliveCheck = new AliveCheck();
        final EntityCheck entityCheck = new EntityCheck(targetsProperty.isSelected("Players"), targetsProperty.isSelected("Animals"),
                targetsProperty.isSelected("Mobs"), targetsProperty.isSelected("Invisibles"));
        final TeamsCheck teamsCheck = new TeamsCheck(targetsProperty.isSelected("Teams"));
        entityValidator.add(aliveCheck);
        entityValidator.add(new ConstantDistanceCheck(blockRangeProperty.getValue().floatValue()));
        entityValidator.add(entityCheck);
        entityValidator.add(teamsCheck);
        blockValidator.add(aliveCheck);
        blockValidator.add(new ConstantDistanceCheck(blockRangeProperty.getValue().floatValue()));
        blockValidator.add(entityCheck);
        blockValidator.add(teamsCheck);
        if (mc.thePlayer.ticksExisted <= 5) {
            if (this.isEnabled()) {
                this.toggle();
            }
            return;
        }

        if (event.isUpdate()) return;

        updateTargets();
        if (event.isPre()) {
            if (target != null) {
                PacketUtil.sendPacketNoEvent(new C0CPacketInput());
            }
            if (target == null) {
                if (block)
                    unblock();

                blockAnimation = false;
                rotating = false;
                multiTargets.clear();
            }

            if (block) {
                unblock();
            }
        }


        //Do mode selecting hsit
        if (modeProperty.getValue() != KillAuraMode.MULTI) {
            target = getTarget();
            if (event.getState() == PlayerMotionEvent.EventState.PRE) {
                if (isEntityNearby()) {
                    final Vec3 hitOrigin = RotationUtil.getHitOrigin(mc.thePlayer);
                    final Vec3 attackHitVec = getAttackHitVec(hitOrigin, target);
                    if (doesRotations() && attackHitVec != null) {
                        final float[] rotations = RotationUtil.getRotations(
                                new float[]{lockViewProperty.getValue() ? mc.thePlayer.rotationYaw : event.getPrevYaw(),
                                        lockViewProperty.getValue() ? mc.thePlayer.rotationPitch : event.getPrevPitch()},
                                rotationsProperty.getValue() == RotationsMode.SNAP ? 0.0F : 17.5f,
                                hitOrigin,
                                attackHitVec);
                        if (!isBypassRotations()) rotationStore = new float[]{event.getPrevYaw(), event.getPrevPitch()};
                        setServerSideRotations(isBypassRotations() ? rotationStore : rotations, event, lockViewProperty.getValue());
                    }
                }
            }
        } else {
            final Vec3 hitOrigin = RotationUtil.getHitOrigin(mc.thePlayer);
            final boolean doRots = doesRotations();
            final List<float[]> rotationsToEntities = new ArrayList<>();
            if (!isEntityNearby())
                target = null;


            targets.stream().limit(maxTargets.getValue().intValue()).forEach(entity -> {
                target = entity;
                final Vec3 attackHitVec = getAttackHitVec(hitOrigin, target);

                if (attackHitVec != null && isEntityNearby() && event.isPre()) {
                    if (doRots) {
                        // Calculate rotations (with max yaw/pitch change & GCD) to best attack hit vec
                        final float[] rotations = RotationUtil.getRotations(
                                new float[]{lockViewProperty.getValue() ? mc.thePlayer.rotationYaw : event.getPrevYaw(),
                                        lockViewProperty.getValue() ? mc.thePlayer.rotationPitch : event.getPrevPitch()},
                                rotationsProperty.getValue() == RotationsMode.SNAP ? 0.0F : 17.5f,
                                hitOrigin,
                                attackHitVec);

                        rotationsToEntities.add(rotations);
                    }
                    multiTargets.add(target);
                }
            });
            if (event.isPre()) {
                if (doesRotations() && isEntityNearby()) {
                    final float[] avgRotations = RotationUtil.calculateAverageRotations(rotationsToEntities);
                    setServerSideRotations(avgRotations, event, lockViewProperty.getValue());
                }
            }
        }


        if (isEntityNearby()) {
            final Vec3 origin = RotationUtil.getHitOrigin(this.mc.thePlayer);
            final MovingObjectPosition intercept = RotationUtil.calculateIntercept(
                    RotationUtil.getHittableBoundingBox(target, 0.1),
                    origin,
                    event.getYaw(),
                    event.getPitch(),
                    rangeProperty.getValue().floatValue());

            if (event.isPre()) {
                if (Client.INSTANCE.getModuleManager().getModule(Criticals.class).isEnabled()
                        && target != null && mc.thePlayer.onGround && !Client.INSTANCE.getModuleManager().getModule(Speed.class).isEnabled()
                        && (isEntityNearbyAttack() && target.hurtResistantTime != 20) && !PlayerUtil.isInLiquid() && !MovementUtil.isInsideBlock()) {
                    event.setPosY(event.getPosY() + 0.003);
                    if (blockTimer.hasElapsed(750)) {
                        event.setPosY(event.getPosY() + 0.001);
                        blockTimer.reset();
                    }
                    event.setGround(false);
                }

                if (isEntityNearbyAttack() && (intercept != null || !onlyAttackWhenLookingAtEntityLongNameMethod.getValue())) {
                    if (attackModeProperty.getValue() == AttackMode.PRE || (attackModeProperty.getValue() == AttackMode.HVH && mc.thePlayer.ticksExisted % 10 == 0)) {
                        attack(target);
                    }
                }
            } else if (event.isPost()) {
                if (isEntityNearbyAttack() && (intercept != null || !onlyAttackWhenLookingAtEntityLongNameMethod.getValue())) {
                    if (attackModeProperty.getValue() == AttackMode.POST || (attackModeProperty.getValue() == AttackMode.HVH && mc.thePlayer.ticksExisted % 10 == 0)) {
                        attack(target);
                    }
                }

                boolean canBlock = autoBlockProperty.getValue() && mc.thePlayer.getHeldItem() != null;
                if (canBlock && !block) {
                    block();
                }
            }
        }
    }

    @EventHandler
    public void onUpdateLook(UpdateLookEvent event) {
        if (isBypassRotations()) {
            // Get the eye-pos of the player
            final Vec3 hitOrigin = RotationUtil.getHitOrigin(this.mc.thePlayer);
            // If was not rotating previously
            if (!this.rotating) {
                // Set rotationStore to the current player angles
                this.rotationStore[0] = this.mc.thePlayer.rotationYaw;
                this.rotationStore[1] = this.mc.thePlayer.rotationPitch;
            }
            // Reset rotating state
            this.rotating = false;
            // Get the current targeted entity
            final EntityLivingBase target = this.target;
            if (target == null) return;
            // Find the optimal attack hit vec to aim at
            final Vec3 attackHitVec = RotationUtil.getCenterPointOnBB(target.getEntityBoundingBox(),
                    0.5 + ApacheMath.random() * 0.1);
            // Calculate the yaw/pitch deltas to target
            float[] rotations = RotationUtil.getRotations(hitOrigin, attackHitVec);
            // Apply GCD fix
            RotationUtil.applyGCD(rotations, this.rotationStore);
            RotationUtil.applySmoothing(this.rotationStore, 15.5f, rotations);
            // Update rotations store
            this.rotationStore[0] = rotations[0];
            this.rotationStore[1] = rotations[1];
            // Is now rotating
            this.rotating = true;
        }
    }

    @Override
    public void onEnable() {
        switchTimer.reset();
        attackTimer.reset();
        blockTimer.reset();
        blockAnimation = false;
        targets.clear();
        multiTargets.clear();
        crits = false;
        hasNotThing = false;
        changeTarget = false;
        ticks = 0;
        block = false;
        this.rotating = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        targetIndex = 0;
        blockAnimation = false;
        target = null;
        targets.clear();
    }

    public Vec3 getAttackHitVec(final Vec3 hitOrigin, final EntityLivingBase entity) {
        final AxisAlignedBB boundingBox = RotationUtil.getHittableBoundingBox(entity, .1f);
        // Get optimal attack hit vec
        return RotationUtil.getAttackHitVec(mc, hitOrigin, boundingBox,
                this.rotationsPointProperty.getValue().getHitVec(hitOrigin, boundingBox),
                true, 5);
    }

    public boolean isMulti() {
        return !multiTargets.isEmpty() && isEnabled();
    }

    public EntityLivingBase getTarget() {
        if (targets.isEmpty()) {
            return null;
        }

        if (modeProperty.getValue() == KillAuraMode.SINGLE) {
            return targets.get(0);
        }

        final int size = targets.size();

        if (size >= targetIndex && changeTarget) {
            targetIndex++;
            changeTarget = false;
        }


        if (switchTimer.hasElapsed(switchDelayProperty.getValue().longValue())) {
            changeTarget = true;
            switchTimer.reset();
        }

        if (size <= targetIndex) {
            targetIndex = 0;
        }

        return targets.get(targetIndex);
    }

    private void updateTargets() {
        targets.clear();

        final List<Entity> entities = mc.theWorld.loadedEntityList;

        for (int i = 0, entitiesSize = entities.size(); i < entitiesSize; i++) {
            final Entity entity = entities.get(i);
            if (entity instanceof EntityLivingBase) {
                final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (entityValidator.validate(entityLivingBase) && mc.theWorld.loadedEntityList.contains(entityLivingBase)) {
                    this.targets.add(entityLivingBase);
                }
            }
        }
    }

    public boolean isEntityNearby() {
        final List<Entity> loadedEntityList = mc.theWorld.loadedEntityList;
        for (int i = 0, loadedEntityListSize = loadedEntityList.size(); i < loadedEntityListSize; i++) {
            final Entity entity = loadedEntityList.get(i);
            if (blockValidator.validate(entity)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEntityNearbyAttack() {
        return mc.thePlayer.getDistanceToEntity(getTarget()) <= rangeProperty.getValue();
    }

    public void setServerSideRotations(float[] rotations,
                                       final PlayerMotionEvent event,
                                       final boolean lockView) {
        // When using undetectable rotations set to stored
        if (isBypassRotations()) {
            rotations = this.rotationStore;
        }

        if (rotations == null) return;

        if (rotations[1] < 0.1 && rotations[1] > 0) {
            rotations[1] = -1;
        }

        // Set event yaw (rotations yaw)
        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);

        // Do lock view aim
        if (lockView) {
            mc.thePlayer.rotationYaw = rotations[0];
            mc.thePlayer.rotationPitch = rotations[1];
        }
        rotating = true;
    }

    private void attack(EntityLivingBase entity) {
        int min = minAPSProperty.getValue().intValue();
        int max = maxAPSProperty.getValue().intValue();
        int cps;

        if (min == max) cps = min;
        else cps = MathUtil.randomInt(min, max + 1);
        if (attackTimer.hasElapsed(1000 / cps)) {
            if (isMulti()) {
                multiTargets.forEach(entityLivingBase -> {
                    // mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0001, mc.thePlayer.posZ);
                    if (ViaMCP.getInstance().getVersion() <= ProtocolCollection.getProtocolById(47).getVersion()) {
                        if (noSwingProperty.getValue()) {
                            PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
                        } else mc.thePlayer.swingItem();
                    }


                    if (keepSprintProperty.getValue()) {
                        PacketUtil.sendPacket(new C02PacketUseEntity(entityLivingBase, C02PacketUseEntity.Action.ATTACK));
                    } else {
                        mc.playerController.attackEntity(mc.thePlayer, entityLivingBase);
                    }


                    if (ViaMCP.getInstance().getVersion() > ProtocolCollection.getProtocolById(47).getVersion()) {
                        if (noSwingProperty.getValue()) {
                            PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
                        } else mc.thePlayer.swingItem();
                    }
                    attackTimer.reset();
                    // blockTimer.reset();
                    ticks++;
                });
            } else {
                // mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0001, mc.thePlayer.posZ);
                if (ViaMCP.getInstance().getVersion() <= ProtocolCollection.getProtocolById(47).getVersion()) {
                    if (noSwingProperty.getValue()) {
                        PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
                    } else mc.thePlayer.swingItem();
                }


                if (keepSprintProperty.getValue()) {
                    PacketUtil.sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                } else {
                    mc.playerController.attackEntity(mc.thePlayer, entity);
                }


                if (ViaMCP.getInstance().getVersion() > ProtocolCollection.getProtocolById(47).getVersion()) {
                    if (noSwingProperty.getValue()) {
                        PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
                    } else mc.thePlayer.swingItem();
                }
                attackTimer.reset();
                // blockTimer.reset();
                ticks++;
            }
        }
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        if(rotating && mc.thePlayer.movementInput.jump && target != null){
            event.setYaw(RotationUtil.calculateYawFromSrcToDst(mc.thePlayer.rotationYaw, mc.thePlayer.posX, mc.thePlayer.posZ, target.posX, target.posZ));
        }
        if (isBypassRotations() && rotating) {
            event.setYaw(this.rotationStore[0]);
        }
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (mc.thePlayer.ticksExisted <= 5) return;
        if (target != null && mc.theWorld != null) {
            drawCircle(target, 0.66, true);
        }
    }

    @EventHandler
    public void onPlayerStrafe(PlayerStrafeEvent event) {
        if(rotating && mc.thePlayer.movementInput.jump && target != null){
            event.setYaw(RotationUtil.calculateYawFromSrcToDst(mc.thePlayer.rotationYaw, mc.thePlayer.posX, mc.thePlayer.posZ, target.posX, target.posZ));
        }

        if (isBypassRotations() && rotating) {
            event.setYaw(this.rotationStore[0]);
        }
    }


    public boolean doesRotations() {
        return this.rotationsProperty.getValue() != RotationsMode.OFF;
    }

    public boolean isBypassRotations() {
        return this.rotationsProperty.getValue() == RotationsMode.UNDETECTABLE;
    }

    private void block() {
        switch (blockModeProperty.getValue()) {
            case WATCHDOG: {
                if (isEntityNearbyAttack() && PlayerUtil.isHoldingSword()) {
                    if (mc.thePlayer.swingProgressInt == -1) {
                        PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(
                                C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    } else if (mc.thePlayer.swingProgressInt == -0.8) {
                        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(
                                new BlockPos(-1, -1, -1), 1, mc.thePlayer.inventory.getCurrentItem(),
                                0.1f, 0.1f, 0.1f
                        ));
                    } else if (mc.thePlayer.swingProgressInt < 0.8 && mc.thePlayer.swingProgressInt > -0.7) {
                        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement
                                (new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(),
                                        0.0081284124F, 0.00004921712F, 0.0081248912F));
                    }
                }
                break;
            }
            case VERUS: {
                if (isEntityNearbyAttack() && PlayerUtil.isHoldingSword()) {
                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0081284124F, 0.00004921712F, 0.0081248912F));
                }
                break;
            }

            case ASTRALMC:
            case VANILLA:
                if (isEntityNearbyAttack() && PlayerUtil.isHoldingSword()) {
                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    break;
                }
        }
        block = true;
        blockAnimation = true;
    }

    private void unblock() {
        switch (blockModeProperty.getValue()) {
            case VERUS:
            case VANILLA: {
                PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                break;
            }
        }
        block = false;
    }

//    private boolean distanceCheck(EntityLivingBase entityLivingBase, boolean block) {
//        return mc.thePlayer != null && entityLivingBase != null && mc.thePlayer.getDistanceToEntity(entityLivingBase) < (block ? ApacheMath.max(rangeProperty.getValue(), blockRangeProperty.getValue()) : rangeProperty.getValue());
//    }

    public float getRange() {
        return rangeProperty.getValue().floatValue();
    }

    private void drawCircle(Entity entity, double rad, boolean shade) {
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_POINT_SMOOTH);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
        glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
        glDepthMask(false);
        GlStateManager.alphaFunc(GL_GREATER, 0);
        if (shade) glShadeModel(GL_SMOOTH);
        GlStateManager.disableCull();
        glBegin(GL_TRIANGLE_STRIP);

        final double x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, mc.timer.renderPartialTicks);
        final double y = (RenderUtil.interpolate(entity.posY, entity.lastTickPosY, mc.timer.renderPartialTicks)) + ApacheMath.sin(System.currentTimeMillis() / 4E+2) + 1;
        final double z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, mc.timer.renderPartialTicks);
        //double x1 = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, mc.timer.renderPartialTicks);


        Color c;
        for (float i = 0; i < ApacheMath.PI * 2; i += ApacheMath.PI * 2 / 64) {
            final double vecX = x + rad * ApacheMath.cos(i);
            final double vecZ = z + rad * ApacheMath.sin(i);
            c = new Color(25, 98, 189, 125);

            if (shade) {
                glColor4f(c.getRed() / 255F,
                        c.getGreen() / 255F,
                        c.getBlue() / 255F,
                        0
                );
                glVertex3d(vecX, y - ApacheMath.cos(System.currentTimeMillis() / 4E+2) / 2, vecZ);
                glColor4f(c.getRed() / 255F,
                        c.getGreen() / 255F,
                        c.getBlue() / 255F,
                        0.85F
                );
            }
            glVertex3d(vecX, y, vecZ);
        }
        glEnd();
        if (shade) glShadeModel(GL_FLAT);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        GlStateManager.alphaFunc(GL_GREATER, 0.1F);
        GlStateManager.enableCull();
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POINT_SMOOTH);
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
        glColor3f(255, 255, 255);
    }

    @AllArgsConstructor
    private enum SortMode implements INameable{
        HEALTH("Health", new HealthSorter()),
        ARMOR("Armor", new ArmorSorter()),
        FOV("FOV", new FovSorter()),
        HURTTIME("Hurttime", new HurtTimeSorter()),
        DISTANCE("Distance", new DistanceSorter());

        private final String modeName;
        private final Comparator<EntityLivingBase> sorter;

        @Override
        public String getName() {
            return modeName;
        }
    }

    @AllArgsConstructor
    private enum KillAuraMode implements INameable {
        SINGLE("Single"),
        SWITCH("Switch"),
        MULTI("Multi");

        private final String modeName;

        @Override
        public String getName() {
            return modeName;
        }
    }

    @AllArgsConstructor
    private enum AttackMode implements INameable {
        PRE("Pre"),
        POST("Post"),
        HVH("HvH");

        private final String modeName;

        @Override
        public String getName() {
            return modeName;
        }
    }

    @AllArgsConstructor
    private enum BlockMode implements INameable {
        WATCHDOG("Watchdog"),
        VERUS("Verus"),
        VANILLA("Vanilla"),
        ASTRALMC("AstralMC"),
        FAKE("Fake");

        private final String modeName;

        @Override
        public String getName() {
            return modeName;
        }
    }


    private final static class HealthSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(PlayerUtil.getEffectiveHealth(o1), PlayerUtil.getEffectiveHealth(o2));
        }
    }

    private final static class ArmorSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(o1.getTotalArmorValue(), o2.getTotalArmorValue());
        }
    }

    private final static class FovSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(o1.getPositionVector().subtract(mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks)).angle(mc.thePlayer.getLookVec()), o2.getPositionVector().subtract(mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks)).angle(mc.thePlayer.getLookVec()));
        }
    }

    private final static class HurtTimeSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(20 - o1.hurtResistantTime, 20 - o2.hurtResistantTime);
        }
    }

    private final static class DistanceSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return -Double.compare(mc.thePlayer.getDistanceToEntity(o1), mc.thePlayer.getDistanceToEntity(o2));
        }
    }
//    public MultiSelectEnumSetting<PlayerUtil.TARGETS> targetsProperty() {
//        return targetsProperty;
//    }

    @AllArgsConstructor
    public enum RotationsMode implements INameable {
        OFF("Off"),
        SNAP("Snap"),
        UNDETECTABLE("Undetectable"),
        SMOOTH("Smooth");

        private final String modeName;

        @Override
        public String getName() {
            return modeName;
        }
    }
}
