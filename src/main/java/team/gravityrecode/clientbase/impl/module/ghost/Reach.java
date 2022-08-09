package team.gravityrecode.clientbase.impl.module.ghost;

import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.property.NumberSetting;

@ModuleInfo(moduleName = "Reach", moduleCategory = Module.ModuleCategory.GHOST)
public class Reach extends Module {

    public NumberSetting reach = new NumberSetting(this, "Reach", 3.7, 5.0, 3.0, 0.1);
}
