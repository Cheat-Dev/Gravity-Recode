package team.gravityrecode.clientbase.impl.module.movement.speed;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.impl.event.player.PlayerMoveEvent;
import team.gravityrecode.clientbase.impl.property.mode.Mode;
import team.gravityrecode.clientbase.impl.util.player.MovementUtil;

public class AstralMCSpeed extends Mode {

    double moveSpeed;
    boolean doSlow;

    public AstralMCSpeed(Module owner, String name) {
        super(owner, name);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        double baseSpeed = 0.2873D;
        int amp = 0;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            amp = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
            baseSpeed *= 1.0D + 0.2D * amp;
        }

        if (MovementUtil.isMoving() && mc.thePlayer.onGround) {
            moveSpeed = amp > 1 ? baseSpeed + 0.9 : baseSpeed * 2.15;
            event.setY(0.42F);
            boolean cond = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer)).getBlock() instanceof BlockSlab ||
                    mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).down()).getBlock() instanceof BlockSlab ||
                    mc.theWorld.getBlockState(new BlockPos(mc.thePlayer)).getBlock() instanceof BlockStairs ||
                    mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).down()).getBlock() instanceof BlockStairs ||
                    mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).offset(mc.thePlayer.getHorizontalFacing()).down()).getBlock() instanceof BlockAir ||
                    !(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).offset(mc.thePlayer.getHorizontalFacing())).getBlock() instanceof BlockAir);
            mc.thePlayer.motionY = cond || mc.thePlayer.movementInput.jump ? 0.42F : -0.0784;
        } else {
            moveSpeed *= 0.9; // max friction according to the ac src I have.
        }
        moveSpeed = Math.max(baseSpeed, moveSpeed);
        MovementUtil.setSpeed(event, moveSpeed);
    }

    @Override
    public void onDisable() {
        moveSpeed = 0;
        super.onDisable();
    }
}
