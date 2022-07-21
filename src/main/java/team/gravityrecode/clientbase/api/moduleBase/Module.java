package team.gravityrecode.clientbase.api.moduleBase;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.property.Property;
import team.gravityrecode.clientbase.api.util.MinecraftUtil;
import team.gravityrecode.clientbase.impl.property.ModeSetting;
import team.gravityrecode.clientbase.impl.util.util.client.Logger;

@Getter@Setter
public class Module implements MinecraftUtil {
    private final ModuleInfo moduleInfo;
    private int keyBind;
    private boolean enabled, expanded;

    public Module(){
        Class<?> clazz = this.getClass();
        if (!clazz.isAnnotationPresent(ModuleInfo.class))
            throw new RuntimeException("No ModuleInfo found for class " + clazz.getName() + "!");

        this.moduleInfo = clazz.getDeclaredAnnotation(ModuleInfo.class);
        this.keyBind = moduleInfo.moduleKeyBind();
    }

    public void onEnable(){
        for(Property<?> property : Client.INSTANCE.getPropertyManager().get(this))
            if(property instanceof ModeSetting)
                ((ModeSetting) property).getValue().onEnable();

        Client.INSTANCE.getPubSubEventBus().subscribe(this);
        Logger.print("Enabled " + getModuleName());
    }

    public void onDisable(){
        for(Property<?> property : Client.INSTANCE.getPropertyManager().get(this))
            if(property instanceof ModeSetting)
                ((ModeSetting) property).getValue().onDisable();

        Client.INSTANCE.getPubSubEventBus().unsubscribe(this);
        Logger.print("Disabled " + getModuleName());
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
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
        COMBAT(1,"Combat"),
        MOVEMENT(2, "Movement"),
        PLAYER(3, "Player"),
        VISUAL(4, "Render"),
        EXPLOIT(5, "Exploit"),
        MISC(6, "Misc");

        public int elementIndex;
        public final String categoryName;

        @Override
        public String toString() {return categoryName;}
    }

}
