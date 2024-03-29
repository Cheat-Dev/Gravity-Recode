package team.gravityrecode.clientbase.impl.manager;

import me.jinthium.clickgui.MainCGUI;
import me.jinthium.scripting.Script;
import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.manager.Manager;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.keyboard.KeyboardPressEvent;
import team.gravityrecode.clientbase.impl.module.combat.Criticals;
import team.gravityrecode.clientbase.impl.module.combat.Killaura;
import team.gravityrecode.clientbase.impl.module.combat.Killsults;
import team.gravityrecode.clientbase.impl.module.combat.Volecity;
import team.gravityrecode.clientbase.impl.module.exploit.Disabler;
import team.gravityrecode.clientbase.impl.module.exploit.PacketModifier;
import team.gravityrecode.clientbase.impl.module.ghost.*;
import team.gravityrecode.clientbase.impl.module.movement.*;
import team.gravityrecode.clientbase.impl.module.player.AutoArmor;
import team.gravityrecode.clientbase.impl.module.player.ChetStaler;
import team.gravityrecode.clientbase.impl.module.player.InventoryManager;
import team.gravityrecode.clientbase.impl.module.player.Scaffold;
import team.gravityrecode.clientbase.impl.module.visual.*;
import team.gravityrecode.clientbase.impl.util.client.Logger;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModuleManager extends Manager<Module> {

    public void init() {
        Client.INSTANCE.getPubSubEventBus().subscribe(this);
        Stream.of(new AutoArmor(), new PacketModifier(), new Disabler(), new InventoryManager(), new Scaffold(), new Volecity(), new Benchmark(), new Sprint(),
                new Hud(), new Criticals(), new Speed(), new Flight(), new Timer(), new Killaura(), new Killsults(), new Notifications(),
                new ESP(), new ChetStaler(), new DamageParticles(), new NoHurtCam(), new ItemPhysics(), new NoSlow(), new ClickGui(),
                new AutoClicker(), new Hitbox(), new Reach(), new AimAssist(), new WTap()).sorted((o1, o2) -> {
            Class<?> c1 = o1.getClass();
            Class<?> c2 = o2.getClass();
            ModuleInfo a1 = c1.getDeclaredAnnotation(ModuleInfo.class);
            ModuleInfo a2 = c2.getDeclaredAnnotation(ModuleInfo.class);
            return a1.moduleName().compareTo(a2.moduleName());
        }).forEach(this::add);
        getModules().sort(SORT_METHOD);
    }

    private final Comparator<Object> SORT_METHOD = Comparator.comparingDouble(m -> {
        Module module = (Module) m;
        String name = module.getModuleName();
        return Fonts.INSTANCE.getSourceSansPro().getStringWidth(name);
    }).reversed();

    public void addScript(Script script) {
        this.add(script);
    }

    public void clearScripts() {
        this.removeIf(m -> m instanceof Script);
    }

    @EventHandler
    public void onKeyboardPress(KeyboardPressEvent event) {
        if(event.getKeyCode() == Keyboard.KEY_RSHIFT){
//            Logger.print(Client.INSTANCE.getPropertyManager().get(Client.INSTANCE.getModuleManager().getModule(ClickGui.class), "Mode"));
            mc.displayGuiScreen(Client.INSTANCE.getMainCGUI());
        }

        getModules().forEach(module -> {
            if (module.getKeyBind() == event.getKeyCode()) {
                module.toggle();
            }
        });
    }

    public List<Module> getModules() {
        return new ArrayList<>(this.getObjects().values());
    }

    public List<Module> getModulesInCategory(Module.ModuleCategory c) {
        return getModules().stream().filter(m -> m.getModuleCategory() == c).collect(Collectors.toList());
    }

    public <T extends Module> T getModule(Class<? extends Module> clazz) {
        return (T) getModules().stream().filter(object -> object.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public <T extends Module> T getModule(String moduleName) {
        return (T) getModules().stream().filter(object -> object.getModuleInfo().moduleName().equalsIgnoreCase(moduleName)).findFirst().orElse(null);
    }
}
