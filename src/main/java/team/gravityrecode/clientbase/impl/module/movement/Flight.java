package team.gravityrecode.clientbase.impl.module.movement;

import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.player.PlayerMoveEvent;
import team.gravityrecode.clientbase.impl.util.util.player.MovementUtil;

@ModuleInfo(moduleName = "Flight", moduleKeyBind = Keyboard.KEY_G, moduleCategory = Module.ModuleCategory.MOVEMENT)
public class Flight extends Module {

    private double moveSpeed;
    private boolean doSlow;

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (MovementUtil.isMovingOnGround()) {
            moveSpeed = MovementUtil.getBaseMoveSpeed() * 2.1;
            event.setY(mc.thePlayer.motionY = 0.42F);
            doSlow = true;
        } else {
            if (doSlow) {
                moveSpeed += 0.26 * (moveSpeed - MovementUtil.getBaseMoveSpeed());
                doSlow = false;
            } else {
                if (moveSpeed > MovementUtil.getBaseMoveSpeed())
                    moveSpeed -= moveSpeed / 159;
            }
        }
        MovementUtil.setSpeed(event, moveSpeed);
        event.setY(mc.thePlayer.motionY = mc.thePlayer.ticksExisted % 2 == 0 ? 0.0001 : -0.0001);
    }

    @Override
    public void onDisable() {
        moveSpeed = 0;
        super.onDisable();
    }
}
