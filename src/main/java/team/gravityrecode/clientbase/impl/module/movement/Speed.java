package team.gravityrecode.clientbase.impl.module.movement;

import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.player.PlayerMoveEvent;
import team.gravityrecode.clientbase.impl.util.util.player.MovementUtil;

@ModuleInfo(moduleName = "Speed", moduleCategory = Module.ModuleCategory.MOVEMENT, moduleKeyBind = Keyboard.KEY_F)
public class Speed extends Module {

    private double moveSpeed;
    private boolean doSlow;

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (MovementUtil.isMovingOnGround()) {
            moveSpeed = MovementUtil.getBaseMoveSpeed() * 1.8;
            event.setY(mc.thePlayer.motionY = 0.42F);
            doSlow = true;
        } else {
            if (doSlow) {
                moveSpeed -= 0.72 * (moveSpeed - MovementUtil.getBaseMoveSpeed());
                doSlow = false;
            } else {
                moveSpeed -= moveSpeed / 159;
            }
        }
        MovementUtil.setSpeed(event, moveSpeed);
    }

    @Override
    public void onDisable() {
        moveSpeed = 0;
        super.onDisable();
    }
}
