package team.gravityrecode.clientbase.impl.util.util.player;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.RandomUtils;
import team.gravityrecode.clientbase.api.util.MinecraftUtil;
import team.gravityrecode.clientbase.impl.event.player.PlayerMoveEvent;
import team.gravityrecode.clientbase.impl.util.util.math.MathUtil;
import team.gravityrecode.clientbase.impl.util.util.network.PacketUtil;
import me.jinthium.optimization.ApacheMath;

import java.security.SecureRandom;

@AllArgsConstructor
public class MovementUtil implements MinecraftUtil {

    public static void damage() {
        for (int i = 0; i < 50; i++) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
    }

    public static float getLilypadValue(){
        return 0.015625F;
    }

    public static boolean canSprint(final boolean omni) {
        return (mc.thePlayer.movementInput.moveForward >= 0.8F || (omni && isMoving())) &&
                (mc.thePlayer.getFoodStats().getFoodLevel() > 6.0F || mc.thePlayer.capabilities.allowFlying) &&
                !mc.thePlayer.isPotionActive(Potion.blindness) &&
                !mc.thePlayer.isCollidedHorizontally &&
                !mc.thePlayer.isSneaking();
    }


    public static boolean isMathGround() {
        return mc.thePlayer.posY % 0.015625 == 0;
    }

    public static double getRandomHypixelValues() {
        SecureRandom secureRandom = new SecureRandom();
        double value = secureRandom.nextDouble() * (1.0 / System.currentTimeMillis());
        for (int i = 0; i < MathUtil.randomInt(MathUtil.randomInt(4, 6), MathUtil.randomInt(8, 20)); i++)
            value *= (1.0 / System.currentTimeMillis());
        return value;
    }

    public static float getRandomHypixelValuesFloat() {
        double value = 1;
        for (int i = 0; i < RandomUtils.nextInt(4, 7); i++) {
            value *= ApacheMath.random();
        }
        return (float) value;
    }

    public static boolean isMoving() {
        return mc.thePlayer.movementInput.moveForward != 0.0F || mc.thePlayer.movementInput.moveStrafe != 0.0F;
    }

    public static double getJumpBoostMotion() {
        if (mc.thePlayer.isPotionActive(Potion.jump))
            return (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1;

        return 0;
    }

    public static boolean isOnGround() {
        return mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically;
    }

    public static boolean isMovingOnGround() {
        return isMoving() && isOnGround();
    }

    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, -height, 0)).isEmpty();
    }

    public static void sendPositionAll(double value, boolean ground) {
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + value, mc.thePlayer.posY, mc.thePlayer.posZ + value, ground));
    }

    public static void sendPositionOnlyY(double y, boolean ground) {
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + y, mc.thePlayer.posZ, ground));
    }

    public static float getMovementDirection() {
        return getMovementDirection(mc.thePlayer.rotationYaw);
    }

    public static float getMovementDirection(final float yaw) {
        final float forward = mc.thePlayer.moveForward;
        final float strafe = mc.thePlayer.moveStrafing;
        final boolean forwards = forward > 0;
        final boolean backwards = forward < 0;
        final boolean right = strafe > 0;
        final boolean left = strafe < 0;
        float direction = 0;
        if (backwards)
            direction += 180;
        direction += forwards ? (right ? -45 : left ? 45 : 0) : backwards ? (right ? 45 : left ? -45 : 0) : (right ? -90 : left ? 90 : 0);
        direction += yaw;
        return MathHelper.wrapAngleTo180_float(direction);
    }

    public static double getBaseMoveSpeed() {
        return getBaseMoveSpeed(true);
    }

    public static double[] yawPos(double value) {
        return yawPos(mc.thePlayer.rotationYaw * MathHelper.deg2Rad, value);
    }

    public static double[] yawPos(float yaw, double value) {
        return new double[]{-MathHelper.sin(yaw) * value, MathHelper.cos(yaw) * value};
    }

    public static double[] getXZ(final double moveSpeed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;

        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += ((forward > 0.0) ? -45 : 45);
            } else if (strafe < 0.0) {
                yaw += ((forward > 0.0) ? 45 : -45);
            }
            strafe = 0.0F;
            if (forward > 0.0) {
                forward = 1F;
            } else if (forward < 0.0) {
                forward = -1F;
            }
        }

        if (strafe > 0.0) {
            strafe = 1F;
        } else if (strafe < 0.0) {
            strafe = -1F;
        }
        double mx = ApacheMath.cos(ApacheMath.toRadians((yaw + 90.0F)));
        double mz = ApacheMath.sin(ApacheMath.toRadians((yaw + 90.0F)));
        double x = (forward * moveSpeed * mx + strafe * moveSpeed * mz);
        double z = (forward * moveSpeed * mz - strafe * moveSpeed * mx);
        return new double[]{x, z};
    }


    public static double getBaseMoveSpeed(boolean sprint) {
        double baseSpeed = (sprint) ? 0.2873 : 0.22;
        if ((mc.thePlayer != null && mc.thePlayer.isPotionActive(Potion.moveSpeed)) && sprint) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public static double getJumpHeight(double height) {
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            return height + (amplifier + 1) * 0.1F;
        }
        return height;
    }

    public static float getMaxFallDist() {
        PotionEffect jump = mc.thePlayer.getActivePotionEffect(Potion.jump);
        final int height = jump != null ? jump.getAmplifier() + 1 : 0;
        return (float) (mc.thePlayer.getMaxFallHeight() + height);
    }

    public static void setSpeed(final PlayerMoveEvent event, double speed) {
        EntityPlayerSP player = mc.thePlayer;
        setSpeed(event, speed, player.moveForward, player.moveStrafing, player.rotationYaw);
    }

    public static void setSpeed(PlayerMoveEvent e, double speed, float forward, float strafing, float yaw) {
        if (forward == 0.0F && strafing == 0.0F) return;

        boolean reversed = forward < 0.0f;
        float strafingYaw = 90.0f *
                (forward > 0.0f ? 0.5f : reversed ? -0.5f : 1.0f);

        if (reversed)
            yaw += 180.0f;
        if (strafing > 0.0f)
            yaw -= strafingYaw;
        else if (strafing < 0.0f)
            yaw += strafingYaw;

        double x = ApacheMath.cos(ApacheMath.toRadians(yaw + 90.0f));
        double z = ApacheMath.cos(ApacheMath.toRadians(yaw));

        e.setX(x * speed);
        e.setZ(z * speed);
    }

    public static void setSpeed(double speed) {
        EntityPlayerSP player = mc.thePlayer;
        setSpeed(speed, player.moveForward, player.moveStrafing, player.rotationYaw);
    }

    public static void setSpeed(double speed, float forward, float strafing, float yaw) {
        if (forward == 0.0F && strafing == 0.0F) return;

        boolean reversed = forward < 0.0f;
        float strafingYaw = 90.0f *
                (forward > 0.0f ? 0.5f : reversed ? -0.5f : 1.0f);

        if (reversed)
            yaw += 180.0f;
        if (strafing > 0.0f)
            yaw -= strafingYaw;
        else if (strafing < 0.0f)
            yaw += strafingYaw;

        double x = ApacheMath.cos(ApacheMath.toRadians(yaw + 90.0f));
        double z = ApacheMath.cos(ApacheMath.toRadians(yaw));

        mc.thePlayer.motionX = x * speed;
        mc.thePlayer.motionZ = z * speed;
    }

    public static double getSpeed() {
        return ApacheMath.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static double getLastDistance() {
        return ApacheMath.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ);
    }

    public static double[] getSpeed(double moveSpeed) {
        final double forward = mc.thePlayer.movementInput.moveForward;
        final double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if (forward == 0 && strafe == 0) return new double[]{0, 0};
        final boolean reversed = forward < 0f;
        final float strafingYaw = 90f * (forward > 0f ? 0.5f : reversed ? -0.5f : 1.0f);
        if (reversed) yaw += 180f;
        if (strafe > 0f) yaw -= strafingYaw;
        else if (strafe < 0f) yaw += strafingYaw;
        final double x = ApacheMath.cos(ApacheMath.toRadians(yaw + 90f));
        final double z = ApacheMath.cos(ApacheMath.toRadians(yaw));
        return new double[]{x * moveSpeed, z * moveSpeed};
    }

    public static boolean isInsideBlock() {
        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int y = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY); y < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxY) + 1; y++) {
                for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                    Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block != null && !(block instanceof BlockAir)) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.theWorld, new BlockPos(x, y, z), mc.theWorld.getBlockState(new BlockPos(x, y, z)));
                        if (block instanceof BlockHopper)
                            boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                        if (boundingBox != null && mc.thePlayer.getEntityBoundingBox().intersectsWith(boundingBox))
                            return true;
                    }
                }
            }
        }
        return false;
    }
}
