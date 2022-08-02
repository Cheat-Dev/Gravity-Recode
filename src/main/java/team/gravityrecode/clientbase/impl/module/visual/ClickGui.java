package team.gravityrecode.clientbase.impl.module.visual;

import lombok.AllArgsConstructor;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.property.EnumSetting;
import team.gravityrecode.clientbase.impl.property.interfaces.INameable;

@ModuleInfo(moduleName = "ClickGui", moduleCategory = Module.ModuleCategory.VISUAL)
public class ClickGui extends Module {

    public EnumSetting<ClickGuiMode> mode = new EnumSetting<>(this, "Mode", ClickGuiMode.values());

    @AllArgsConstructor
    public enum ClickGuiMode implements INameable {
        NEW("New"), OLD("Old");

        private final String modeName;

        @Override
        public String getName() {
            return modeName;
        }
    }

    @Override
    public void onEnable() {

        super.onEnable();
    }
}
