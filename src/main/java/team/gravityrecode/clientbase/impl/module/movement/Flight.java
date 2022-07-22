package team.gravityrecode.clientbase.impl.module.movement;

import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.module.movement.flight.BlockDropFlight;
import team.gravityrecode.clientbase.impl.module.movement.flight.OldNCPFlight;
import team.gravityrecode.clientbase.impl.property.ModeSetting;

@ModuleInfo(moduleName = "Flight", moduleKeyBind = Keyboard.KEY_G, moduleCategory = Module.ModuleCategory.MOVEMENT)
public class Flight extends Module {
    private final ModeSetting mode = new ModeSetting(this, "Mode", new OldNCPFlight(this, "OldNCP"), new BlockDropFlight(this, "BlockDrop"));
}
