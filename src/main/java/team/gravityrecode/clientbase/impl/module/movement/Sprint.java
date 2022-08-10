package team.gravityrecode.clientbase.impl.module.movement;

import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.player.SendSprintStateEvent;
import team.gravityrecode.clientbase.impl.property.BooleanSetting;
import team.gravityrecode.clientbase.impl.util.player.MovementUtil;

@ModuleInfo(moduleName = "_sprints_for_your_lazy_ass_so_you_dont_have_to_start_manually_breathing", moduleCategory = Module.ModuleCategory.MOVEMENT, moduleKeyBind = Keyboard.KEY_V)
public class Sprint extends Module {

    private final BooleanSetting omniSprint = new BooleanSetting(this, "Omni", true);

    @EventHandler
    public void onSendSprintStateEvent(SendSprintStateEvent event) {
        if (MovementUtil.canSprint(omniSprint.getValue())) {
            event.setSprintState(true);
            mc.thePlayer.setSprinting(true);
        }
    }
}
