package team.gravityrecode.clientbase.impl.module.movement;

import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.event.player.SendSprintStateEvent;
import team.gravityrecode.clientbase.impl.property.BooleanSetting;
import team.gravityrecode.clientbase.impl.util.util.player.MovementUtil;

@ModuleInfo(moduleName = "Sprint", moduleCategory = Module.ModuleCategory.MOVEMENT, moduleKeyBind = Keyboard.KEY_V)
public class Sprint extends Module {

    private BooleanSetting omniSprint = new BooleanSetting(this, "Omni", true);

    @EventHandler
    public void onSendSprintStateEvent(SendSprintStateEvent event) {
        if (MovementUtil.canSprint(omniSprint.getValue())) {
            event.setSprintState(true);
            mc.thePlayer.setSprinting(true);
        }
    }

}
