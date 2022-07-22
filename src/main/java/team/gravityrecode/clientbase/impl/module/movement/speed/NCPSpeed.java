package team.gravityrecode.clientbase.impl.module.movement.speed;

import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.impl.event.player.PlayerMoveEvent;
import team.gravityrecode.clientbase.impl.property.mode.Mode;
import team.gravityrecode.clientbase.impl.util.util.player.MovementUtil;

public class NCPSpeed extends Mode {

    double moveSpeed;
    boolean doSlow;

    public NCPSpeed(Module owner, String name) {
        super(owner, name);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (MovementUtil.isMovingOnGround()) {
            moveSpeed = MovementUtil.getBaseMoveSpeed() * 1.925;
            event.setY(mc.thePlayer.motionY = 0.42F);
            doSlow = true;
        } else  if (doSlow) {
                moveSpeed -= 0.72 * (moveSpeed - MovementUtil.getBaseMoveSpeed());
                doSlow = false;
            } else {
                moveSpeed *= 0.98;
            }
        MovementUtil.setSpeed(event, moveSpeed);
    }

    @Override
    public void onDisable() {
        moveSpeed = 0;
        super.onDisable();
    }
}
