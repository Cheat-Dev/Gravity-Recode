package team.gravityrecode.clientbase.impl.module.visual;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.render.Render2DEvent;
import team.gravityrecode.clientbase.impl.util.util.client.Logger;
import team.gravityrecode.clientbase.impl.util.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.util.render.Draggable;
import team.gravityrecode.clientbase.impl.util.util.render.DraggablesManager;

import java.util.Comparator;
import java.util.List;

@ModuleInfo(moduleName = "Hud", moduleCategory = Module.ModuleCategory.VISUAL, moduleKeyBind = Keyboard.KEY_U)
public class Hud extends Module {

    public Draggable draggable = Client.INSTANCE.getDraggablesManager().createNewDraggable(this, "test", 4, 4, mc.fontRendererObj.getStringWidth("Gravity"), mc.fontRendererObj.FONT_HEIGHT);
    public List<Module> modules;

    @EventHandler
    public void onRender2D(Render2DEvent event) {
//        if(Client.INSTANCE)

        Fonts.INSTANCE.getSourceSansPro().drawString("Test", 5, 5, -1);
        Client.INSTANCE.getBlurrer().bloom((int) draggable.getX() - 2, (int) draggable.getY() - 2, 62, 14, 8, 95);
        mc.fontRendererObj.drawStringWithShadow(Client.INSTANCE.getClientInfo().getClientName(), draggable.getX() + 3,
                Client.INSTANCE.getModuleManager().getModule("TabGui").isEnabled() ? draggable.getY() + 3 : draggable.getY() + 1, -1);
        int y = 0;
        modules = Client.INSTANCE.getModuleManager().getModules();
        modules.sort(SORT_METHOD);
        for (Module module : modules) {
            int stringWidth = mc.fontRendererObj.getStringWidth(module.getModuleName());
            if (module.isEnabled()) {
                int xVal = event.getScaledResolution().getScaledWidth() - stringWidth - 4;
                Client.INSTANCE.getBlurrer().bloom(xVal - 8, y - mc.fontRendererObj.FONT_HEIGHT + 11, stringWidth + 12, 16,
                        10, 95);
                mc.fontRendererObj.drawStringWithShadow(module.getModuleName(), event.getScaledResolution().getScaledWidth() - stringWidth - 8, y + 6, -1);
                y += 11;
            }
        }
    }

    private final Comparator<Object> SORT_METHOD = Comparator.comparingDouble(m -> {
        Module module = (Module) m;
        String name = module.getModuleName();
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(name);
    }).reversed();
}
