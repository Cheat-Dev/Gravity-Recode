package team.gravityrecode.clientbase.impl.module.movement;

import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;

@ModuleInfo(moduleName = "Sprint", moduleCategory = Module.ModuleCategory.MOVEMENT)
public class Sprint extends Module {

    @EventHandler
    public void onPlayerMotionEvent(PlayerMotionEvent event) {
        if (movementKeybindsPressed() && mc.thePlayer.getFoodStats().getFoodLevel() > 3 && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking())
            mc.thePlayer.setSprinting(true);
    }

    public boolean movementKeybindsPressed() {
        return mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown();
    }
}
