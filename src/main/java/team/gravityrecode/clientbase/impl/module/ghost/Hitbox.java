package team.gravityrecode.clientbase.impl.module.ghost;

import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.property.NumberSetting;

@ModuleInfo(moduleName = "Hitbox", moduleCategory = Module.ModuleCategory.COMBAT)
public class Hitbox extends Module {

    public NumberSetting hitbox = new NumberSetting(this, "Hitbox", 0.2, 0.05, 1, 0.05);
}
