package team.gravityrecode.clientbase.impl.manager;

import net.minecraft.client.Minecraft;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.manager.Manager;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.keyboard.KeyboardPressEvent;
import team.gravityrecode.clientbase.impl.module.movement.Sprint;
import team.gravityrecode.clientbase.impl.module.visual.Hud;
import team.gravityrecode.clientbase.impl.module.visual.TabGui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ModuleManager extends Manager<Module> {

    public void init() {
        Client.INSTANCE.getPubSubEventBus().subscribe(this);
        Stream.of(new Sprint(), new Hud(), new TabGui()).sorted((o1, o2) -> {
            Class<?> c1 = o1.getClass();
            Class<?> c2 = o2.getClass();
            ModuleInfo a1 = c1.getDeclaredAnnotation(ModuleInfo.class);
            ModuleInfo a2 = c2.getDeclaredAnnotation(ModuleInfo.class);
            return a1.moduleName().compareTo(a2.moduleName());
        }).forEach(this::add);
    }

    @EventHandler
    public void onKeyboardPress(KeyboardPressEvent event) {
        getModules().forEach(module -> {
            if (module.getKeyBind() == event.getKeyCode()) {
                module.toggle();
            }
        });
    }

    public List<Module> getModules() {
        return new ArrayList<>(this.getObjects().values());
    }

    public <T extends Module> T getModule(Class<? extends Module> clazz) {
        return (T) getModules().stream().filter(object -> object.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public <T extends Module> T getModule(String moduleName) {
        return (T) getModules().stream().filter(object -> object.getModuleInfo().moduleName().equalsIgnoreCase(moduleName)).findFirst().orElse(null);
    }
}
