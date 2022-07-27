package team.gravityrecode.clientbase.impl.module.movement.speed;

import me.jinthium.optimization.ApacheMath;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerMoveEvent;
import team.gravityrecode.clientbase.impl.property.mode.Mode;
import team.gravityrecode.clientbase.impl.util.player.MovementUtil;

public class WatchdogSpeed extends Mode {


    private double moveSpeed, lastDistance;
    private boolean shouldBoost;

    public WatchdogSpeed(Module owner, String name) {
        super(owner, name);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        moveSpeed = 0;
        lastDistance = 0;
        shouldBoost = false;
    }

    @EventHandler
    public void e(PlayerMotionEvent event){
        if (event.isPre() || event.isUpdate()) {
            this.lastDistance = ApacheMath.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ);
        }
    }

    @EventHandler
    public void yayayyayayayayayayayayayayayaya(PlayerMoveEvent event){
        if (MovementUtil.isMoving()) {
            if (mc.thePlayer.onGround) {
                event.setY(mc.thePlayer.motionY = MovementUtil.getJumpHeight(0.42F));
                this.moveSpeed = (MovementUtil.getBaseMoveSpeed() * 1.95);
                this.shouldBoost = true;
            } else if (this.shouldBoost) {
                this.moveSpeed = this.lastDistance - 0.66F * (this.lastDistance - MovementUtil.getBaseMoveSpeed());
                this.shouldBoost = false;
            } else {
                this.moveSpeed = this.lastDistance * 0.91f;
              //  this.moveSpeed += mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.045f : 0.038f;
                if (mc.thePlayer.moveStrafing > 0) {
                    double multi = (MovementUtil.getSpeed() - this.lastDistance) * MovementUtil.getBaseMoveSpeed();

                    this.moveSpeed += multi;
                    this.moveSpeed -= 0.015f;
                }
            }

            MovementUtil.setSpeed(event, (float) Math.max(this.moveSpeed, MovementUtil.getBaseMoveSpeed()));
        }
    }
}
