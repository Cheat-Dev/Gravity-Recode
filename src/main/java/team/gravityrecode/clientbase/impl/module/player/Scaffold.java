package team.gravityrecode.clientbase.impl.module.player;

import lombok.AllArgsConstructor;
import me.jinthium.optimization.ApacheMath;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.player.*;
import team.gravityrecode.clientbase.impl.event.player.windowclick.WindowClickRequest;
import team.gravityrecode.clientbase.impl.event.render.Render2DEvent;
import team.gravityrecode.clientbase.impl.module.movement.Speed;
import team.gravityrecode.clientbase.impl.property.BooleanSetting;
import team.gravityrecode.clientbase.impl.property.EnumSetting;
import team.gravityrecode.clientbase.impl.property.NumberSetting;
import team.gravityrecode.clientbase.impl.property.interfaces.INameable;
import team.gravityrecode.clientbase.impl.util.client.TimerUtil;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.math.MathUtil;
import team.gravityrecode.clientbase.impl.util.network.PacketUtil;
import team.gravityrecode.clientbase.impl.util.player.BlockUtils;
import team.gravityrecode.clientbase.impl.util.player.InventoryUtils;
import team.gravityrecode.clientbase.impl.util.player.MovementUtil;
import team.gravityrecode.clientbase.impl.util.player.RotationUtil;
import team.gravityrecode.clientbase.impl.util.render.RenderUtil;
import team.gravityrecode.clientbase.impl.util.render.RoundedUtil;
import team.gravityrecode.clientbase.impl.util.render.animations.Animation;
import team.gravityrecode.clientbase.impl.util.render.animations.SmoothStep;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glVertex2d;

@ModuleInfo(moduleName = "Scaffold", moduleCategory = Module.ModuleCategory.PLAYER)
public class Scaffold extends Module {
    private static final EnumFacing[] FACINGS = new EnumFacing[]{
            EnumFacing.EAST,
            EnumFacing.WEST,
            EnumFacing.SOUTH,
            EnumFacing.NORTH};

    private double oPosY;
    private final ConcurrentLinkedQueue<Packet<?>> yaBoiC08Queue = new ConcurrentLinkedQueue();
    private final TimerUtil placeTimer = new TimerUtil();
    public int slowTicks;

    private final Animation animation = new SmoothStep(250, 1);

    // Tower
    private final BooleanSetting towerProperty = new BooleanSetting(this, "Tower", true);
    // Placement
    private final BooleanSetting spoofHeldItemProperty = new BooleanSetting(this, "Spoof Held Item", true);
    private final EnumSetting<EventPlaceIn> eventPlaceInEnumSetting = new EnumSetting<EventPlaceIn>(this, "Place In", EventPlaceIn.values());
    private final EnumSetting<HitVecMode> hitVecModeEnumSetting = new EnumSetting<HitVecMode>(this, "HitVec", HitVecMode.values());
    private final EnumSetting<RotationMode> rotationModeEnumSetting = new EnumSetting<RotationMode>(this, "Rotations", RotationMode.values());
    private final EnumSetting<MovementFixMode> movementFixModeEnumSetting = new EnumSetting<MovementFixMode>(this, "Movement Fix", MovementFixMode.values());
    private final NumberSetting blocksToSneak = new NumberSetting(this, "Blocks To Sneak", 0, 0, 50, 1);
    private final NumberSetting distFromEdge = new NumberSetting(this, "Dist from edge", 0.25, 0.01, 0.99, 0.00005, () -> blocksToSneak.getValue() > 0);
    private final NumberSetting placeDelayProperty = new NumberSetting(this, "Place Delay", 0, 0, 5000, 1);
    private final BooleanSetting daVulcanBypassO = new BooleanSetting(this, "SneakPacket Bypass", false);
    private final NumberSetting expandProperty = new NumberSetting(this, "Expand", 0, 0, 10.0, 0.01);
    private final EnumSetting<Swing> swingProperty = new EnumSetting<>(this, "Swing", Swing.values());
    private final BooleanSetting rayTraceCheckProperty = new BooleanSetting(this, "Ray Trace Check", false);
    // Movement
    public final BooleanSetting autoJumpProperty = new BooleanSetting(this, "Auto Jump", false);
    private final EnumSetting<JumpMode> jumpModeEnumSetting = new EnumSetting<JumpMode>(this, "AJ Mode", autoJumpProperty::getValue, JumpMode.values());

    private final EnumSetting<VulcanJumpMode> vulcanJumpModeEnumSetting = new EnumSetting<VulcanJumpMode>(this, "Vulcan JM",  ()
            -> (jumpModeEnumSetting.getVisible().getAsBoolean() && jumpModeEnumSetting.getValue() == JumpMode.VULCAN),
            VulcanJumpMode.values());

    private final EnumSetting<NormalJumpMode> normalJumpModeEnumSetting = new EnumSetting<NormalJumpMode>(this, "Normal JM",
            () -> (jumpModeEnumSetting.getVisible().getAsBoolean() && jumpModeEnumSetting.getValue() == JumpMode.NORMAL),
            NormalJumpMode.values());
    private final BooleanSetting safeWalkProperty = new BooleanSetting(this, "Safe Walk", false);
    private final BooleanSetting keepPosProperty = new BooleanSetting(this, "Keep Y", true);
    public final BooleanSetting noSprintProperty = new BooleanSetting(this, "No Sprint", true);

    // For drawing only...
    private final List<Vec3> breadcrumbs = new ArrayList<>();
    private double fadeInOutProgress;
    private int totalBlockCount;
    // Counters
    private int ticksSinceWindowClick;
    private int ticksSincePlace;
    // Block data
    private BlockData data;
    private BlockData lastPlacement;
    public float[] angles;
    // Tower
    public boolean towering;
    private int placedBlocks, ticksThing, sneakedBlocks;
    // Other...
    private Speed speed;
    private int bestBlockStack;
    private double startPosY;
    private WindowClickRequest lastRequest;

    // Autojump
    private double moveSpeed;
    private double lastDist;
    private boolean wasOnGround;
    private float derp;

    @EventHandler
    public void a(SpoofItemEvent event) {
        if (spoofHeldItemProperty.getValue() && bestBlockStack != -1 && bestBlockStack >= 36)
            event.setCurrentItem(bestBlockStack - InventoryUtils.ONLY_HOT_BAR_BEGIN);
    }

    @EventHandler
    public void b(WindowClickEvent event) {
        ticksSinceWindowClick = 0;
    }

    @EventHandler
    public void a(BlockPlaceEvent event) {
        ticksSincePlace = 0;
    }

    @EventHandler
    public void a(PlayerStrafeEvent event) {
        final double baseMoveSpeed = MovementUtil.getBaseMoveSpeed();
        if(autoJumpProperty.getValue() && jumpModeEnumSetting.getValue() == JumpMode.WATCHDOG){
            if(MovementUtil.isMoving()){
                event.setMotionPartialStrafe((float) (MovementUtil.getBaseMoveSpeed(false) * 0.8f), 0.2375f + MovementUtil.getRandomHypixelValuesFloat());
            }
        }

        if(movementFixModeEnumSetting.getValue() == MovementFixMode.HIDDEN){
            event.setCancelled(true);
            silentRotationStrafe(event, angles[0]);
        }
    };

    @EventHandler
    public void a(PlayerMotionEvent event) {
        if(!MovementUtil.isMoving()) oPosY = event.getPosY();

        if(!event.isUpdate()) {
            if (event.isPre()) {
                if (towering && !MovementUtil.isMoving()) {
                    if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(0, 2, 0)).getBlock() instanceof BlockAir) {
                        mc.thePlayer.cameraPitch = 0.0f;
                        final double[] jumpY = {0.41999998688698, 0.7531999805212};
                        final double divideY = event.getPosY() % 1.0;
                        final double roundY = MathHelper.floor_double(mc.thePlayer.posY);
                        if (divideY > 0.419 && divideY < 0.753) {
                            event.setPosY(roundY + jumpY[0]);
                        } else if (divideY > 0.753) {
                            event.setPosY(roundY + jumpY[1]);
                        } else {
                            event.setPosY(roundY);
                            event.setGround(true);
                        }
                    }
                }
            }
            if (MovementUtil.isOnGround(0.15) && towering) {
//                mc.timer.timerSpeed = 2f;
//                Logger.print("alana is lana");
                mc.thePlayer.motionX *= 0.91;
                mc.thePlayer.motionZ *= 0.91;
                mc.thePlayer.motionY = 0.42F;
            }
        }

        if (event.isPre()) {
            if (autoJumpProperty.getValue()) {
                switch(jumpModeEnumSetting.getValue()){
                    case NORMAL:{
                        switch(normalJumpModeEnumSetting.getValue()){
                            case VANILLA:{
                                if(MovementUtil.isMovingOnGround()){
                                    mc.thePlayer.motionY = 0.42F;
                                }
                                break;
                            }
                            case FULL_STRAFE:{
                                if(MovementUtil.isMoving()){
                                    if(mc.thePlayer.onGround){
                                        mc.thePlayer.motionY = 0.42F;
                                    }
                                    MovementUtil.setSpeed(MovementUtil.getSpeed());
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case VULCAN:{
                        switch(vulcanJumpModeEnumSetting.getValue()){
                            case LOWHOP:{
                                if (MovementUtil.isMovingOnGround()) {
                                    double speed = ApacheMath.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
                                    boolean boost = mc.thePlayer.isPotionActive(1);
                                    switch (ticksThing) {
                                        case 1:
                                            moveSpeed = 0.42f;
                                            speed = boost ? speed + 0.2 : 0.48;
                                            event.setGround(true);
                                            break;
                                        case 2:
                                            speed = boost ? speed * 0.71  : 0.19;
                                            moveSpeed -= 0.0784f;
                                            event.setGround(false);
                                            break;
                                        default:
                                            ticksThing = 0;
                                            speed /= boost ? 0.64 : 0.66;
                                            event.setGround(true);
                                            break;
                                    }
                                    MovementUtil.setSpeed(speed);
                                    ticksThing++;
                                    event.setPosY(event.getPosY() + moveSpeed);
                                } else {
                                    ticksThing = 0;
                                }
                                break;
                            }
                            case NON_STRAFE_HOP:{
                                // PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(null));
                                if(MovementUtil.isMoving()){
                                    if(MovementUtil.isMathGround()){
                                        MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 1.3);
//                                        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(null));
                                        mc.thePlayer.jump();
                                        mc.thePlayer.motionY = 0.012500047683714;
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case WATCHDOG:{
                        if(MovementUtil.isMoving()){
                            // event.setGround(true);
                            if(mc.thePlayer.onGround){
                                mc.thePlayer.motionY = 0.42F;
                                moveSpeed = MovementUtil.getBaseMoveSpeed() * 1.2;
                                mc.timer.timerSpeed = 1.0f;
                            }else{
                                mc.timer.timerSpeed = 1.28f + MovementUtil.getRandomHypixelValuesFloat();
                            }
                            //MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 0.538);
                        }
                        break;
                    }
                }
            }
            if (MovementUtil.isMovingOnGround() && slowTicks <= 3 && !towering && !noSprintProperty.getValue() && !autoJumpProperty.getValue()) {
                final double[] xz = MovementUtil.getXZ(MovementUtil.getBaseMoveSpeed() / 2);
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX - xz[0], mc.thePlayer.posY, mc.thePlayer.posZ - xz[1], true));
                if(mc.thePlayer.ticksExisted % 2 == 0) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX - Math.random() / 500, mc.thePlayer.posY, mc.thePlayer.posZ - Math.random() / 500);
                }
//                if(mc.thePlayer.ticksExisted % 2 != 0){
//                    slowTicks--;
//                }
                if(mc.thePlayer.ticksExisted % 2 != 0) slowTicks--;
            }


            if (autoJumpProperty.getValue()) {
                final double xDist = mc.thePlayer.lastTickPosX - event.getPosX();
                final double zDist = mc.thePlayer.lastTickPosZ - event.getPosZ();
                lastDist = ApacheMath.sqrt(xDist * xDist + zDist * zDist);
            }

            if(MovementUtil.isMoving() && !noSprintProperty.getValue() && mc.thePlayer.isPotionActive(Potion.moveSpeed)){
                mc.thePlayer.motionX *= 0.8;
                mc.thePlayer.motionZ *= 0.8;
            }

            // Increment tick counters
            ticksSinceWindowClick++;
            ticksSincePlace++;

            // Invalidate old data
            data = null;

            if(noSprintProperty.getValue() || autoJumpProperty.getValue()){
                mc.thePlayer.setSprinting(false);
            }

            // Update towering state
            towering = towerProperty.getValue() && mc.gameSettings.keyBindJump.isKeyDown() && !MovementUtil.isOnGround(1 / 64);

            // Look for best block stack in hot bar
            bestBlockStack = getBestBlockStack(InventoryUtils.ONLY_HOT_BAR_BEGIN, InventoryUtils.END);

            calculateTotalBlockCount();
            moveBlocksIntoHotBar();

//            if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && eventPlaceInEnumSetting.getValue() == EventPlaceIn.Pre && mc.thePlayer.onGround) {
//                PacketUtil.sendPacketNoEvent(new C02PacketUseEntity(-1, C02PacketUseEntity.Action.ATTACK));
//                mc.thePlayer.motionY = 0.48F;
//                mc.gameSettings.keyBindJump.pressed = false;
//            }

//            if (towering) {
//                if(MovementUtil.isMoving())
//                    mc.timer.timerSpeed = 1.0f;
//                else
//                    mc.timer.timerSpeed = 1.7f;
//
//                mc.thePlayer.motionY = 0.41998F - MovementUtil.getRandomHypixelValuesFloat() * 3;
//
//                if (MovementUtil.isMoving()) {
//                    mc.thePlayer.motionX *= 0.7;
//                    mc.thePlayer.motionZ *= 0.7;
//                }
//
////                event.setPosX(event.getPosX() + (mc.thePlayer.ticksExisted % 2 != 0 ? -MovementUtil.getRandomHypixelValues() * 2 : MovementUtil.getRandomHypixelValues() * 2));
////                event.setPosZ(event.getPosZ() + (mc.thePlayer.ticksExisted % 2 != 0 ? -MovementUtil.getRandomHypixelValues() * 2 : MovementUtil.getRandomHypixelValues() * 2));
//            }

//            if(mc.thePlayer.onGround)
//                slowTicks = 0;
//            else
//                slowTicks++;

            // If best block stack is in hot bar
            if (bestBlockStack >= InventoryUtils.ONLY_HOT_BAR_BEGIN) {
                final BlockPos blockUnder = getBlockUnder();
                data = getBlockData(blockUnder);

                if (data == null) data = getBlockData(blockUnder.offset(EnumFacing.DOWN));

                if (data != null) {
                    Vec3 hitVec = getHitVec(event, data);
                    if(validateReplaceable(data) && hitVec != null){
                        switch(rotationModeEnumSetting.getValue()){
                            case NORMAL:{
                                angles = RotationUtil.getRotations(new float[]{event.getPrevYaw(), event.getPrevPitch()},
                                        15.5f, RotationUtil.getHitOrigin(mc.thePlayer), hitVec);
                                break;
                            }
                            case ALWAYS_BACKWARDS:{
                                angles = new float[] {MovementUtil.getMovementDirection() - 180.f, 0};
                                break;
                            }
                        }
                    }else{
                        data = null;
                    }
                }


                // If has not set angles or has not yet placed a block
                if (angles == null || lastPlacement == null) {
                    // Get the last rotations (EntityPlayerSP#rotationYaw/rotationPitch)
                    final float[] lastAngles = this.angles != null ? this.angles : new float[]{event.getPrevYaw(), event.getPrevPitch()};
                    // Get the opposite direct that you are moving
                    final float moveDir = MovementUtil.getMovementDirection();
                    // Desired rotations
                    final float[] dstRotations = new float[]{moveDir + 180.f, 84.f};
                    //   Smooth to opposite
                    RotationUtil.applySmoothing(lastAngles, 19.5f, dstRotations);
                    // Apply GCD fix (just for fun)
                    RotationUtil.applyGCD(dstRotations, lastAngles);

                    switch(rotationModeEnumSetting.getValue()){
                        case NORMAL:{
                            angles = dstRotations;
                            break;
                        }
                        case ALWAYS_BACKWARDS:{
                            angles = new float[] {moveDir - 180, 0};
                            break;
                        }
                    }
                }

                if(angles[1] < 0.1 && angles[1] > 0){
                    angles[1] = 0;
                }


                // Set rotations to persistent rotations

                if(rotationModeEnumSetting.getValue() != RotationMode.OFF){
                    event.setYaw(angles[0]);
                    event.setPitch(angles[1]);
                }

                if(!isOnEdge(0.1)){
                    placeTimer.reset();
                }

                double[] dist = MovementUtil.getXZ(distFromEdge.getValue().floatValue());
                boolean onEdge = BlockUtils.getBlock(mc.thePlayer.posX + dist[0], mc.thePlayer.posY - 0.5, mc.thePlayer.posZ + dist[1]) instanceof BlockAir;

                if(onEdge && blocksToSneak.getValue() > 0 && sneakedBlocks < blocksToSneak.getValue()){
                    mc.gameSettings.keyBindSneak.pressed = true;
                }else if(!onEdge){
                    mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
                }

                if(isToweringOnHyp() || eventPlaceInEnumSetting.getValue() == EventPlaceIn.PRE){
                    doPlace(event);
                }
            }
        }else {
            if(!isToweringOnHyp()){
                if(event.isUpdate() && eventPlaceInEnumSetting.getValue() == EventPlaceIn.UPDATE)
                    doPlace(event);
                else if(event.isPost() && eventPlaceInEnumSetting.getValue() == EventPlaceIn.POST)
                    doPlace(event);
            }
        }
    };

    public boolean isToweringOnHyp(){
        return towering;
    }

    private boolean isOnEdge(final double verbose) {
        final WorldClient world = mc.theWorld;
        final EntityPlayerSP player = mc.thePlayer;
        double[] gars = new double[] {0, verbose, -verbose};
        for (double x : gars) {
            for (double z : gars) {
                final BlockPos belowBlockPos = new BlockPos(player.posX + x, getBlockUnder().getY(), player.posZ + z);
                if (!(world.getBlockState(belowBlockPos).getBlock() instanceof BlockAir))
                    return false;
            }
        }
        return true;
    }

    private int down(double n) {
        int n2 = (int)n;
        try {
            if (n < (double)n2) {
                return n2 - 1;
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return n2;
    }

    public double[] getExpandCoords(double y) {
        BlockPos underPos = new BlockPos(mc.thePlayer.posX, y, mc.thePlayer.posZ);
        Block underBlock = mc.theWorld.getBlockState(underPos).getBlock();
        MovementInput movementInput = mc.thePlayer.movementInput;
        float forward = movementInput.moveForward, strafe = movementInput.moveStrafe, yaw = mc.thePlayer.rotationYaw;
        double xCalc = -999, zCalc = -999, dist = 0, expandDist = expandProperty.getValue().floatValue();

        while (!isAirBlock(underBlock)) {
            xCalc = mc.thePlayer.posX;
            zCalc = mc.thePlayer.posZ;
            dist++;
            if (dist > expandDist) dist = expandDist;
            xCalc += (forward * 0.45 * MathHelper.cos((float) ApacheMath.toRadians(yaw + 90.0f)) + strafe * 0.45 * MathHelper.sin((float) ApacheMath.toRadians(yaw + 90.0f))) * dist;
            zCalc += (forward * 0.45 * MathHelper.sin((float) ApacheMath.toRadians(yaw + 90.0f)) - strafe * 0.45 * MathHelper.cos((float) ApacheMath.toRadians(yaw + 90.0f))) * dist;
            if (dist == expandDist) break;
            underPos = new BlockPos(xCalc, y, zCalc);
            underBlock = mc.theWorld.getBlockState(underPos).getBlock();
        }

        return new double[]{xCalc, zCalc};
    }

    public boolean isAirBlock(Block block) {
        if (block.getMaterial().isReplaceable()) {
            return !(block instanceof BlockSnow) || !(block.getBlockBoundsMaxY() > 0.125);
        }

        return false;
    }


    private void doPlace(final PlayerMotionEvent event) {
        if (bestBlockStack < 36 || data == null || !placeTimer.hasElapsed(placeDelayProperty.getValue().longValue()) && placeDelayProperty.getValue() > 0)
            return;

        final Vec3 hitVec = getHitVec(event, data);

        final ItemStack heldItem;

        if (spoofHeldItemProperty.getValue()) {
            heldItem = mc.thePlayer.inventoryContainer.getSlot(bestBlockStack).getStack();
        } else {
            // Switch item client side
            mc.thePlayer.inventory.currentItem = bestBlockStack - InventoryUtils.ONLY_HOT_BAR_BEGIN;
            heldItem = mc.thePlayer.getCurrentEquippedItem();
        }

        if (heldItem == null || hitVec == null) return;


        if(ViaMCP.getInstance().getVersion() > ProtocolCollection.getProtocolById(47).getVersion()){
            switch (swingProperty.getValue()) {
                case CLIENT:
                    mc.thePlayer.swingItem();
                    break;
                case SILENT:
                    mc.thePlayer.sendQueue.sendPacketNoEvent(new C0APacketAnimation());
                    break;
            }
        }


        // Attempt place using ray trace hit vec
//        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, angles[0], angles[1],  / 64)));
        // PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(null));
//        if(MovementUtil.isOnGround(1/ 64)) {
//            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, angles[0], angles[1],  true));
//        }
        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, heldItem, data.pos, data.face, hitVec)) {
//            if(event.isPre())
//                Logger.print("pre");
//            else
//                Logger.print("post");
            lastPlacement = data;
            if(blocksToSneak.getValue() > 0)
                sneakedBlocks++;

            placeTimer.reset();
            placedBlocks++;
            derp = 1;
            slowTicks = 3;

            if(ViaMCP.getInstance().getVersion() <= ProtocolCollection.getProtocolById(47).getVersion()) {
                switch (swingProperty.getValue()) {
                    case CLIENT:
                        mc.thePlayer.swingItem();
                        break;
                    case SILENT:
                        mc.thePlayer.sendQueue.sendPacketNoEvent(new C0APacketAnimation());
                        break;
                }
            }


            if(daVulcanBypassO.getValue()){
                if(placedBlocks % 6 == 0){
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                    if(ticksSincePlace > 0) {
                        PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                    }
                }
            }
        }
    }


    @EventHandler
    public void a(Render2DEvent event) {
        final ScaledResolution sr = event.getScaledResolution();
        final float mx = sr.getScaledWidth() / 2.0f;
        final float my = sr.getScaledHeight() / 2.0f;

        Gui.drawRect(0, 0, 0, 0, 0);
        final EntityPlayerSP player = mc.thePlayer;

        final double minFadeInProgress = 0.7;

        // Block counter
        if (bestBlockStack != -1) {
            // Get the "best" block itemStack from the inventory (computed every tick)
            final ItemStack stack = player.inventoryContainer.getSlot(bestBlockStack).getStack();
            // Check the stack in slot has not changed since last update
            if (stack != null) {
                if (fadeInOutProgress < 1.0)
                    fadeInOutProgress += 1.0 / Minecraft.getDebugFPS() * 2;

                final String blockCount = String.format(totalBlockCount == 1 ? "1 block" : "%s blocks", totalBlockCount);
                final FontRenderer fontRenderer = mc.fontRendererObj;

                final double width = 40 + (int) ApacheMath.ceil(fontRenderer.getStringWidth(blockCount)) / 2.7;
                final double height = 40 + (int) ApacheMath.ceil(fontRenderer.getStringWidth(blockCount)) / 2.7;

                final double left = mx - width / 2.0;
                final double top = my + 20 + 10; // middle + arrow spacing + size

                // Background
                glPushMatrix();
                //RoundedUtil.drawRound((float) left, (float) top, (float) width, (float) height, 8, true, new Color(30, 30, 30, 120));


                final int itemStackSize = 16;

                final int textWidth = itemStackSize + 2 + (int) ApacheMath.ceil(fontRenderer.getStringWidth(blockCount));

                final int iconRenderPosX = (int) (left + width / 2 - textWidth / 2);

                final int iconRenderPosY = (int) (top + (height - itemStackSize) / 2);

                // Setup for item render with proper lighting
                RenderUtil.scaleStart((float) (left + width / 2), (float) (top + height / 2), (float) animation.getOutput());
                final boolean restore = RenderUtil.glEnableBlend();
                GlStateManager.enableRescaleNormal();
                RenderHelper.enableGUIStandardItemLighting();
                RoundedUtil.drawRoundedRect((float) iconRenderPosX, (float) top + (float)(height / 3), (float) ((float) iconRenderPosX + width), (float) (top + height), 8, new Color(0,0,0, 100).getRGB());

                // Draw block icon
                glPushMatrix();
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, (float) (iconRenderPosX + width / 2 - 8), iconRenderPosY + 3);
                glPopMatrix();
                Fonts.INSTANCE.getSourceSansPro().drawCenteredString(blockCount, (float) (iconRenderPosX + width / 2) - 1,
                        (float) (top + height - Fonts.INSTANCE.getSourceSansPro().getHeight() - 5),
                        0xFFFFFFFF);
                // Restore after item render
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                glEnable(GL_ALPHA_TEST);
                RenderUtil.glRestoreBlend(restore);
                RenderUtil.scaleEnd();
                glPopMatrix();

            }
        }
    };

    private static void addTriangleVertices(final double size) {
        glVertex2d(0, -size / 2);
        glVertex2d(-size / 2, size / 2);
        glVertex2d(size / 2, size / 2);
    }

    @Override
    public void onEnable() {
        lastPlacement = null;
        derp = 0;
        animation.reset();
        ticksThing = 0;
        yaBoiC08Queue.clear();
        towering = false;
        slowTicks = 3;
        placeTimer.reset();
        sneakedBlocks = 0;
        placedBlocks = 0;
//        PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
//        PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.te]));
        if(mc.thePlayer != null) oPosY = mc.thePlayer.posY;
        if (speed == null) {
            speed = Client.INSTANCE.getModuleManager().getModule(Speed.class);
        }
        if (autoJumpProperty.getValue()) {
            if (speed.isEnabled()) {
                speed.toggle();
            }
            moveSpeed = 0;
            lastDist = 0.0;
        }
        if (mc.thePlayer != null) startPosY = mc.thePlayer.posY;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        angles = null;
        breadcrumbs.clear();
        EntityPlayer.enableCameraYOffset = false;
//        PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        towering = false;
        EntityPlayer.cameraYPosition = mc.thePlayer.posY;
        super.onDisable();
    }

    private BlockData getBlockData(final BlockPos pos) {
        final EnumFacing[] facings = FACINGS;

        // 1 of the 4 directions around player
        for (EnumFacing facing : facings) {
            final BlockPos blockPos = pos.add(facing.getOpposite().getDirectionVec());
            if (InventoryUtils.validateBlock(mc.theWorld.getBlockState(blockPos).getBlock(), InventoryUtils.BlockAction.PLACE_ON)) {
                final BlockData data = new BlockData(blockPos, facing);
                if (validateBlockRange(data))
                    return data;
            }
        }

        // 2 Blocks Under e.g. When jumping
        final BlockPos posBelow = pos.add(0, -1, 0);
        if (InventoryUtils.validateBlock(mc.theWorld.getBlockState(posBelow).getBlock(), InventoryUtils.BlockAction.PLACE_ON)) {
            final BlockData data = new BlockData(posBelow, EnumFacing.UP);
            if (validateBlockRange(data))
                return data;
        }

        // 2 Block extension & diagonal
        for (EnumFacing facing : facings) {
            final BlockPos blockPos = pos.add(facing.getOpposite().getDirectionVec());
            for (EnumFacing facing1 : facings) {
                final BlockPos blockPos1 = blockPos.add(facing1.getOpposite().getDirectionVec());
                if (InventoryUtils.validateBlock(mc.theWorld.getBlockState(blockPos1).getBlock(), InventoryUtils.BlockAction.PLACE_ON)) {
                    final BlockData data = new BlockData(blockPos1, facing1);
                    if (validateBlockRange(data))
                        return data;
                }
            }
        }

        return null;

    }

    private boolean validateBlockRange(final BlockData data) {
        final Vec3 pos = getHitVec(mc.thePlayer.currentEvent, data);

        if (pos == null)
            return false;

        final EntityPlayerSP player = mc.thePlayer;

        final double x = (pos.xCoord - player.posX);
        final double y = (pos.yCoord - (player.posY + mc.thePlayer.getEyeHeight()));
        final double z = (pos.zCoord - player.posZ);

        final float reach = mc.playerController.getBlockReachDistance() + expandProperty.getValue().floatValue();

        return ApacheMath.sqrt(x * x + y * y + z * z) <= reach;
    }

    private boolean validateReplaceable(final BlockData data) {
        final BlockPos pos = data.pos.offset(data.face);
        return mc.theWorld.getBlockState(pos)
                .getBlock()
                .isReplaceable(mc.theWorld, pos);
    }

    private BlockPos getBlockUnder() {
        if (keepPosProperty.getValue() && !Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            double[] expand = getExpandCoords(ApacheMath.min(startPosY, mc.thePlayer.posY) - 1);
            boolean air = isAirBlock(BlockUtils.getBlock(mc.thePlayer.posX, ApacheMath.min(startPosY, mc.thePlayer.posY) - 1, mc.thePlayer.posZ));
            return new BlockPos(air ? mc.thePlayer.posX : expand[0], ApacheMath.min(startPosY, mc.thePlayer.posY) - 1, air ? mc.thePlayer.posZ : expand[1]);
        } else {
            startPosY = mc.thePlayer.posY;
            double[] expand2 = getExpandCoords(mc.thePlayer.posY - 1);
            boolean air1 = isAirBlock(BlockUtils.getBlock(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ));
            return new BlockPos(air1 ? mc.thePlayer.posX : expand2[0], mc.thePlayer.posY - 1, air1 ? mc.thePlayer.posZ : expand2[1]);
        }
    }
    private void moveBlocksIntoHotBar() {
        // If no blocks in hot bar
        if (ticksSinceWindowClick > 3) {
            // Look for best block stack in inventory
            final int bestStackInInv = getBestBlockStack(InventoryUtils.EXCLUDE_ARMOR_BEGIN, InventoryUtils.ONLY_HOT_BAR_BEGIN);
            // If you have no blocks return
            if (bestStackInInv == -1) return;

            boolean foundEmptySlot = false;

            for (int i = InventoryUtils.END - 1; i >= InventoryUtils.ONLY_HOT_BAR_BEGIN; i--) {
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

                if (stack == null) {
                    if (lastRequest == null || lastRequest.isCompleted()) {
                        final int slotID = i;
                        InventoryUtils.queueClickRequest(lastRequest = new WindowClickRequest() {
                            @Override
                            public void performRequest() {
                                // Move blocks from inventory into free slot
                                InventoryUtils.windowClick(mc, bestStackInInv,
                                        slotID - InventoryUtils.ONLY_HOT_BAR_BEGIN,
                                        InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
                            }
                        });
                    }

                    foundEmptySlot = true;
                }
            }

            if (!foundEmptySlot) {
                if (lastRequest == null || lastRequest.isCompleted()) {
                    InventoryUtils.queueClickRequest(lastRequest = new WindowClickRequest() {
                        @Override
                        public void performRequest() {
                            final int overrideSlot = 9;
                            // Swap with item in last slot of hot bar
                            InventoryUtils.windowClick(mc, bestStackInInv, overrideSlot,
                                    InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
                        }
                    });
                }
            }
        }
    }

    private int getBestBlockStack(final int start, final int end) {
        int bestSlot = -1, bestSlotStackSize = 0;

        for (int i = start; i < end; i++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (stack != null &&
                    stack.stackSize > bestSlotStackSize &&
                    stack.getItem() instanceof ItemBlock &&
                    InventoryUtils.isStackValidToPlace(stack)) {

                bestSlot = i;
                bestSlotStackSize = stack.stackSize;
            }
        }

        return bestSlot;
    }

    private void calculateTotalBlockCount() {
        totalBlockCount = 0;

        for (int i = InventoryUtils.EXCLUDE_ARMOR_BEGIN; i < InventoryUtils.END; i++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (stack != null &&
                    stack.stackSize >= 1 &&
                    stack.getItem() instanceof ItemBlock &&
                    InventoryUtils.isStackValidToPlace(stack)) {

                totalBlockCount += stack.stackSize;
            }
        }
    }

    private static class BlockData {

        private final BlockPos pos;
        private final EnumFacing face;
        private final Vec3 hitVec1;

        public BlockData(BlockPos pos, EnumFacing face) {
            this.pos = pos;
            this.face = face;
            this.hitVec1 = calculateBlockData();
        }

        private Vec3 calculateBlockData() {
            final Vec3i directionVec = face.getDirectionVec();
            final Minecraft mc = Minecraft.getMinecraft();
            double x;
            double z;
//            boolean air = (scaffold.keepPosProperty.getValue() && !Keyboard.isKeyDown(Keyboard.KEY_SPACE)) ?
//                    scaffold.isAirBlock(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, ApacheMath.min(scaffold.startPosY, mc.thePlayer.posY) - 1, mc.thePlayer.posZ)).getBlock())
//                    : scaffold.isAirBlock(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock());

            switch (face.getAxis()) {
                case Z:
                    final double absX = ApacheMath.abs(mc.thePlayer.posX);
                    double xOffset = absX - (int) absX;

                    if (mc.thePlayer.posX < 0) {
                        xOffset = 1.0F - xOffset;
                    }

                    x = directionVec.getX() * xOffset;
                    z = directionVec.getZ() * xOffset;
                    break;
                case X:
                    final double absZ = ApacheMath.abs(mc.thePlayer.posZ);
                    double zOffset = absZ - (int) absZ;

                    if (mc.thePlayer.posZ < 0) {
                        zOffset = 1.0F - zOffset;
                    }

                    x = directionVec.getX() * zOffset;
                    z = directionVec.getZ() * zOffset;
                    break;
                default:
                    x = 0.25;
                    z = 0.25;
                    break;
            }

            if (face.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
                x = -x;
                z = -z;
            }

            final Vec3 hitVec = new Vec3(pos).addVector(x + z, directionVec.getY() * 0.5, x + z);

            final Vec3 src = mc.thePlayer.getPositionEyes(1.0F);
            final MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(src,
                    hitVec,
                    false,
                    false,
                    true);

            if (obj == null || obj.hitVec == null || obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
                return null;

            switch (face.getAxis()) {
                case Z:
                    obj.hitVec = new Vec3(obj.hitVec.xCoord, obj.hitVec.yCoord, ApacheMath.round(obj.hitVec.zCoord));
                    break;
                case X:
                    obj.hitVec = new Vec3(ApacheMath.round(obj.hitVec.xCoord), obj.hitVec.yCoord, obj.hitVec.zCoord);
                    break;
            }

            if (face != EnumFacing.DOWN && face != EnumFacing.UP) {
                final IBlockState blockState = mc.theWorld.getBlockState(obj.getBlockPos());
                final Block blockAtPos = blockState.getBlock();

                double blockFaceOffset;

                blockFaceOffset = RandomUtils.nextDouble(0.1, 0.3);

                if (blockAtPos instanceof BlockSlab && !((BlockSlab) blockAtPos).isDouble()) {
                    final BlockSlab.EnumBlockHalf half = blockState.getValue(BlockSlab.HALF);

                    if (half != BlockSlab.EnumBlockHalf.TOP) {
                        blockFaceOffset += 0.5;
                    }
                }

                obj.hitVec = obj.hitVec.addVector(0.0D, -blockFaceOffset, 0.0D);
            }

            return obj.hitVec;
        }
    }

    @AllArgsConstructor
    private enum Swing implements INameable {
        CLIENT("Client"),
        SILENT("Silent"),
        NO_SWING("No Swing");

        private final String modeName;

        @Override
        public String getName() {return modeName;}
    }

    @AllArgsConstructor
    private enum EventPlaceIn implements INameable{
        UPDATE("Update"),
        PRE("Pre"),
        POST("Post");

        private final String modeName;

        @Override
        public String getName() {return modeName;}
    }

    @AllArgsConstructor
    private enum JumpMode implements INameable{
        VULCAN("Vulcan"),
        NORMAL("Normal"),
        WATCHDOG("Watchdog");

        private final String modeName;

        @Override
        public String getName() {return modeName;}
    }

    @AllArgsConstructor
    private enum VulcanJumpMode implements INameable{
        NON_STRAFE_HOP("Non Strafe"),
        LOWHOP("LowHop");

        private final String modeName;

        @Override
        public String getName() {return modeName;}
    }

    @AllArgsConstructor
    private enum NormalJumpMode implements INameable{
        VANILLA("Vanilla"),
        FULL_STRAFE("Full Strafe");

        private final String modeName;

        @Override
        public String getName() {return modeName;}
    }

    @AllArgsConstructor
    private enum HitVecMode implements INameable{
        CUSTOM("Custom"),
        BLOCKPOS("Block Position"),
        RANDOMIZED("Ranzomized"),
        RAYTRACE("Raytrace"),
        OLD_RANDOM("Old Random");

        private final String modeName;

        @Override
        public String getName() {return modeName;}
    }

    public Vec3 getHitVec(PlayerMotionEvent event, BlockData blockData) {
        switch(hitVecModeEnumSetting.getValue()){
            case CUSTOM:{
                return blockData.hitVec1;
            }
            case BLOCKPOS:{
                return new Vec3(blockData.pos);
            }
            case RANDOMIZED:{
                return new Vec3(blockData.pos.getX() + ApacheMath.random(),
                        blockData.pos.getY() + ApacheMath.random(), blockData.pos.getZ() + ApacheMath.random());
            }
            case RAYTRACE:{
                // Perform ray trace with current angle stepped rotations
                final MovingObjectPosition rayTraceResult = RotationUtil.rayTraceBlocks(mc,
                        event.isPre() ? event.getPrevYaw() : event.getYaw(),
                        event.isPre() ? event.getPrevPitch() : event.getPitch());
                // If nothing is hit return
                if (rayTraceResult == null) return null;
                // If did not hit block return
                if (rayTraceResult.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return null;
                // If side hit does not match block data return
                if (rayTraceResult.sideHit != blockData.face) return null;
                // If block pos does not match block data return
                final BlockPos dstPos = blockData.pos;
                final BlockPos rayDstPos = rayTraceResult.getBlockPos();
                if (rayDstPos.getX() != dstPos.getX() ||
                        rayDstPos.getY() != dstPos.getY() ||
                        rayDstPos.getZ() != dstPos.getZ()) return null;

                return rayTraceResult.hitVec;
            }
            case OLD_RANDOM:{
                return getVec3(blockData.pos, blockData.face);
            }
        }
        return null;
    }

    private Vec3 getVec3(final BlockPos pos, final EnumFacing face) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        x += (double) face.getFrontOffsetX() / 2;
        z += (double) face.getFrontOffsetZ() / 2;
        y += (double) face.getFrontOffsetY() / 2;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += MathUtil.randomDouble(-0.3, 0.3);
            z += MathUtil.randomDouble(-0.3, 0.3);
        } else {
            y += MathUtil.randomDouble(0.4, 0.5);
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += MathUtil.randomDouble(-0.3, 0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += MathUtil.randomDouble(-0.3, 0.3);
        }
        return new Vec3(x, y, z);
    }

    @AllArgsConstructor
    private enum MovementFixMode implements INameable{
        OFF("Off"),
        HIDDEN("Hidden");

        private final String modeName;

        @Override
        public String getName() {return modeName;}
    }

    public static void silentRotationStrafe(PlayerStrafeEvent event, float yaw) {
        final int dif = (int) ((MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - yaw - 23.5F - 135.0F) + 180.0F) / 45.0F);
        final float strafe = event.getStrafe();
        final float forward = event.getForward();
        final float friction = event.getFriction();
        float calcForward = 0.0F;
        float calcStrafe = 0.0F;
        switch (dif) {
            case 0: {
                calcForward = forward;
                calcStrafe = strafe;
                break;
            }

            case 1: {
                calcForward += forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe += strafe;
                break;
            }

            case 2: {
                calcForward = strafe;
                calcStrafe = -forward;
                break;
            }

            case 3: {
                calcForward -= forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe -= strafe;
                break;
            }

            case 4: {
                calcForward = -forward;
                calcStrafe = -strafe;
                break;
            }

            case 5: {
                calcForward -= forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe -= strafe;
                break;
            }

            case 6: {
                calcForward = -strafe;
                calcStrafe = forward;
                break;
            }

            case 7: {
                calcForward += forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe += strafe;
                break;
            }
        }

        if (calcForward > 1.0F || (calcForward < 0.9F && calcForward > 0.3F) || calcForward < -1.0F || (calcForward > -0.9F && calcForward < -0.3F))
            calcForward *= 0.5F;

        if (calcStrafe > 1.0F || (calcStrafe < 0.9F && calcStrafe > 0.3F) || calcStrafe < -1.0F || (calcStrafe > -0.9F && calcStrafe < -0.3F))
            calcStrafe *= 0.5F;

        float d;
        if ((d = calcStrafe * calcStrafe + calcForward * calcForward) >= 1.0E-4F) {
            if ((d = MathHelper.sqrt_float(d)) < 1.0F) {
                d = 1.0F;
            }
            d = friction / d;
            final float yawSin = MathHelper.sin((float) (yaw * ApacheMath.PI / 180.0));
            final float yawCos = MathHelper.cos((float) (yaw * ApacheMath.PI / 180.0));
            event.setFriction(0);
            mc.thePlayer.motionX += (calcStrafe *= d) * yawCos - (calcForward *= d) * yawSin;
            mc.thePlayer.motionZ += calcForward * yawCos + calcStrafe * yawSin;
        }
    }

    public boolean doSafeWalk(){
        return this.isEnabled() && safeWalkProperty.getValue() && !autoJumpProperty.getValue();
    }

    @AllArgsConstructor
    private enum RotationMode implements INameable{
        OFF("Off"),
        NORMAL("Normal"),
        ALWAYS_BACKWARDS("Vulcan Funny");

        private final String modeName;

        @Override
        public String getName() {return modeName;}
    }
}