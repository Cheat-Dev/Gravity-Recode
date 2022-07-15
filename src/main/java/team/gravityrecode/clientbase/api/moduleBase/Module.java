package team.gravityrecode.clientbase.api.moduleBase;

import lombok.AllArgsConstructor;
import lombok.Getter;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.client.IToggleable;
import team.gravityrecode.clientbase.api.util.MinecraftUtil;
import team.gravityrecode.clientbase.impl.util.util.client.Logger;

@Getter
public class Module implements MinecraftUtil, IToggleable {
    private final ModuleInfo moduleInfo;
    private int keyBind;
    private boolean enabled;

    public Module(){
        Class<?> clazz = this.getClass();
        if (!clazz.isAnnotationPresent(ModuleInfo.class)) {
            throw new RuntimeException("No ModuleInfo found for class " + clazz.getName() + "!");
        }
        this.moduleInfo = clazz.getDeclaredAnnotation(ModuleInfo.class);
        this.keyBind = moduleInfo.moduleKeyBind();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void onEnable(){
        Client.INSTANCE.getPubSubEventBus().subscribe(this);
        Logger.print("Enabled " + getModuleName());
    }

    public void onDisable(){
        Client.INSTANCE.getPubSubEventBus().unsubscribe(this);
        Logger.print("Disabled " + getModuleName());
    }

    public void toggle() {
        if (!this.enabled) {
            this.enabled = true;
            this.onEnable();
        } else {
            this.enabled = false;
            this.onDisable();
        }
    }

    public String getModuleName(){
        return moduleInfo.moduleName();
    }

    public ModuleCategory getModuleCategory(){
        return moduleInfo.moduleCategory();
    }

    @AllArgsConstructor
    public static enum ModuleCategory {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        VISUAL("Render"),
        EXPLOIT("Exploit"),
        MISC("Misc");

        public final String categoryName;

        @Override
        public String toString() {return categoryName;}
    }

}
