package team.gravityrecode.clientbase.impl.module.visual;

import com.sun.org.apache.xpath.internal.operations.Mod;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.keyboard.KeyboardPressEvent;
import team.gravityrecode.clientbase.impl.event.render.Render2DEvent;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

@ModuleInfo(moduleName = "TabGui", moduleKeyBind = Keyboard.KEY_Y, moduleCategory = Module.ModuleCategory.VISUAL)
public class TabGui extends Module {

    private int tab;
    private boolean expanded;

    @EventHandler
    public void onRender2D(Render2DEvent event){
    drawTabGui(2, 15, event.getScaledResolution());
    }

    @EventHandler
    public void onKeyPressed(KeyboardPressEvent event){
        int code = event.getKeyCode();
        ModuleCategory category = ModuleCategory.values()[tab];
        List<Module> moduleList = Client.INSTANCE.getModuleManager().getModulesInCategory(category);

        if (code == Keyboard.KEY_UP) {
            if (!expanded) {
                if (tab <= 0) {
                    tab = ModuleCategory.values().length - 1;
                } else
                    tab--;
            } else {
                if (category.elementIndex <= 0) {
                    category.elementIndex = moduleList.size() - 1;
                } else
                    category.elementIndex--;
            }
        }

        if (code == Keyboard.KEY_DOWN) {
            if (!expanded) {
                if (tab > ModuleCategory.values().length - 2) {
                    tab = 0;
                } else
                    tab++;
            } else {
                if (category.elementIndex >= moduleList.size() - 1) {
                    category.elementIndex = 0;
                } else
                    category.elementIndex++;
            }
        }
        if (code == Keyboard.KEY_RIGHT) {
            if (expanded && moduleList.size() != 0) {
                Module module = moduleList.get(category.elementIndex);
                if (expanded && !moduleList.isEmpty() && moduleList.get(category.elementIndex).isExpanded()) {

                } else {
                    module.toggle();
                }
            } else {
                expanded = true;
            }
        }
        if (code == Keyboard.KEY_LEFT) {
            if (expanded && !moduleList.isEmpty() && moduleList.get(category.elementIndex).isExpanded()) {
                moduleList.get(category.elementIndex).setExpanded(false);
            } else {
                expanded = false;
            }
        }
    }

    public void drawTabGui(float x, float y, ScaledResolution scaledResolution){
        Client client = Client.INSTANCE;
        FontRenderer font = mc.fontRendererObj;
        ModuleCategory category = ModuleCategory.values()[tab];
        Client.INSTANCE.getBlurrer().bloom((int) x, (int) y, 62, (int) y + ModuleCategory.values().length * 12, 8, 95);
        Client.INSTANCE.getBlurrer().bloom((int) x, (int) ((int) y + tab * 13.5f) + 2, 62, (int) ((int) y), 8, 65);

        int count = 0;
        for (ModuleCategory c : ModuleCategory.values()) {
                font.drawStringWithShadow(c.categoryName, x + (c.categoryName == category.categoryName ? 8 : 5), y + 6f + count * 13.5f, -1);
            count++;
        }
        if(expanded){
            List<Module> elementList = Client.INSTANCE.getModuleManager().getModulesInCategory(category);
            Module module = elementList.get(category.elementIndex);
            Client.INSTANCE.getBlurrer().bloom((int) x + 61, (int) y + tab * 13 + 2, (int) x + 75, (int) (elementList.size() * 12 + 10), 8, 95);
            Client.INSTANCE.getBlurrer().bloom((int) x + 61, (int) y + category.elementIndex * 13 + (tab * 13 + 2), (int) x + 75, (int) y + 4, 8, 65);
            count = 0;
            for (Module mod : elementList) {
                font.drawStringWithShadow(mod.getModuleName(), x + (mod.getModuleName() == module.getModuleName() ? 67 : 64),
                        y + 6 + count * 13.5f + (tab * 13 + 2), mod.isEnabled() ? Color.lightGray.getRGB() : -1);
                count++;
            }
        }
    }
}
