package team.gravityrecode.clientbase.impl.module.movement;

import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.property.NumberSetting;

@ModuleInfo(moduleName = "Timer", moduleCategory = Module.ModuleCategory.MOVEMENT)
public class Timer extends Module {

    private NumberSetting timerSpeed = new NumberSetting(this, "Timer Speed", 2.0, 0.1, 3.0, 0.1);

    @EventHandler
    public void onUpdate(PlayerMotionEvent event){
        mc.timer.timerSpeed = timerSpeed.getValue().floatValue();
    }
}
