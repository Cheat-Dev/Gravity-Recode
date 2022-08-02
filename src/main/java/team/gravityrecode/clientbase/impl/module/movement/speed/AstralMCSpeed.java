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
import team.gravityrecode.clientbase.impl.util.math.MathUtil;
import team.gravityrecode.clientbase.impl.util.player.MovementUtil;

public class AstralMCSpeed extends Mode {

    double moveSpeed;
    boolean doSlow;

    public AstralMCSpeed(Module owner, String name) {
        super(owner, name);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
//        double baseSpeed = 0.2873D;
//        int amp = 0;
//        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
//            amp = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
//            baseSpeed *= 1.0D + 0.2D * amp;
//        }
//
//        if (MovementUtil.isMoving() && mc.thePlayer.onGround) {
//            if(!doSlow) {
//                moveSpeed = baseSpeed * 1.95;
//                doSlow = true;
//            }else{
//                moveSpeed += 0.04D;
//            }
//
//            event.setY(0.42F);
//            mc.thePlayer.motionY = -0.0784;
//        } else {
//            moveSpeed -= moveSpeed / 99; // max friction according to the ac src I have.
//        }
//        moveSpeed = Math.max(baseSpeed, moveSpeed);
//
//        if(moveSpeed > 1.1) {
//            moveSpeed = baseSpeed * 1.95;
//            doSlow = false;
//        }
//
//        MovementUtil.setSpeed(event, moveSpeed);
        MovementUtil.setSpeed(event, mc.thePlayer.ticksExisted % 2 != 0 ? 0 : 1.2);
    }

    @Override
    public void onDisable() {
        moveSpeed = 0;
        doSlow = false;
        super.onDisable();
    }
}
