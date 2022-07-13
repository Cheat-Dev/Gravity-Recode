package me.jinthium.clientbase.api.moduleBase;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.jinthium.clientbase.Client;
import me.jinthium.clientbase.api.util.MinecraftUtil;

@Getter
public class Module implements MinecraftUtil {
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

    public void onEnable(){
        Client.INSTANCE.getPubSubEventBus().subscribe(this);
    }

    public void onDisable(){
        Client.INSTANCE.getPubSubEventBus().unsubscribe(this);
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

        private final String categoryName;

        @Override
        public String toString() {return categoryName;}
    }

}
