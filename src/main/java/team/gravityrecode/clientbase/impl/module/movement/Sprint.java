package team.gravityrecode.clientbase.impl.module.movement;

import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.event.player.SendSprintStateEvent;
import team.gravityrecode.clientbase.impl.property.BooleanSetting;
import team.gravityrecode.clientbase.impl.property.MultiBoolean;
import team.gravityrecode.clientbase.impl.property.MultipleBoolSetting;
import team.gravityrecode.clientbase.impl.util.util.client.Logger;
import team.gravityrecode.clientbase.impl.util.util.player.MovementUtil;

import java.util.Arrays;

@ModuleInfo(moduleName = "Sprint", moduleCategory = Module.ModuleCategory.MOVEMENT, moduleKeyBind = Keyboard.KEY_V)
public class Sprint extends Module {

    private final BooleanSetting omniSprint = new BooleanSetting(this, "Omni", true);
    private final MultipleBoolSetting funny = new MultipleBoolSetting(this, "Bum Hoppa", new MultiBoolean(this, "Button", true),
            new MultiBoolean(this, "Button3", true), new MultiBoolean(this, "fontName", false));


    @EventHandler
    public void onSendSprintStateEvent(SendSprintStateEvent event) {
        if(funny.isSelected("fontName")){
            Logger.print("YEHAHH");
        }


        if (MovementUtil.canSprint(omniSprint.getValue())) {
            event.setSprintState(true);
            mc.thePlayer.setSprinting(true);
        }
    }
}
