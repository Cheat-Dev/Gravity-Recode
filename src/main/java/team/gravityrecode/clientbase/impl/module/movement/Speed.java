package team.gravityrecode.clientbase.impl.module.movement;

import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.module.movement.speed.AstralMCSpeed;
import team.gravityrecode.clientbase.impl.module.movement.speed.NCPSpeed;
import team.gravityrecode.clientbase.impl.module.movement.speed.OldNCPSpeed;
import team.gravityrecode.clientbase.impl.module.movement.speed.WatchdogSpeed;
import team.gravityrecode.clientbase.impl.property.ModeSetting;

@ModuleInfo(moduleName = "Speed", moduleCategory = Module.ModuleCategory.MOVEMENT, moduleKeyBind = Keyboard.KEY_F)
public class Speed extends Module {
    public ModeSetting modeSetting = new ModeSetting(this, "Mode", new WatchdogSpeed(this, "Watchdog"), new OldNCPSpeed(this, "Old NCP"), new NCPSpeed(this, "NCP"), new AstralMCSpeed(this, "AstralMC"));
}
