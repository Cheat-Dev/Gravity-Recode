package team.gravityrecode.clientbase.api.tabgui;

import lombok.Getter;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.Module.ModuleCategory;
import team.gravityrecode.clientbase.impl.event.keyboard.KeyboardPressEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TabGui {

    private List<CategoryTab> tabList = new ArrayList<>();
    private List<ModuleTab> moduleTabList = new ArrayList<>();
    private int tab;
    private int tabOffset, yOffset, moduleOffset;
    private boolean expanded;

    public void init() {
        for (ModuleCategory category : ModuleCategory.values()) {
            if (!Client.INSTANCE.getModuleManager().getModulesInCategory(category).isEmpty())
                tabList.add(new CategoryTab(category.categoryName, category));
        }
        tab = 0;
        tabOffset = 0;
        expanded = false;
    }

    public void unInit() {
        tabList.removeAll(tabList);
        moduleTabList.removeAll(moduleTabList);
        expanded = false;
        tab = 0;
        tabOffset = 0;
    }

    public void renderTabGui(float x, float y, float width, float height, int offset) {
        for (CategoryTab tab : tabList) {
            height += offset;
            Gui.drawRect(x, y + offset + yOffset, x + width, y + offset * 2 + yOffset, new Color(10, 10, 10, 5).getRGB());
            tab.drawTab(x, y + height, width, y, offset);
        }
    }

    public void renderTabGuiModuleTabs(float x, float y, float width, float height, int offset) {
        if (expanded) {
            for (ModuleTab moduleTab : moduleTabList) {
                height += offset;
                Gui.drawRect(x, y + offset + moduleOffset + yOffset, x + width + 4, y + offset * 2 + moduleOffset + yOffset, new Color(10, 10, 10, 5).getRGB());
                moduleTab.drawTab(x, y + height + yOffset, width + 4, y, offset);
            }
        }
    }

    public void getKeyPresses(KeyboardPressEvent event, int add) {
        ModuleCategory category = ModuleCategory.values()[tab];
        List<Module> modules = Client.INSTANCE.getModuleManager().getModulesInCategory(category);
        switch (event.getKeyCode()) {
            case Keyboard.KEY_DOWN:
                if (!expanded) {
                    if (tab == tabList.size() - 1) {
                        tab = 0;
                        yOffset = 0;
                    } else {
                        yOffset += add;
                        tab++;
                    }
                } else {
                    if (category.elementIndex >= modules.size() - 1) {
                        category.elementIndex = 0;
                        moduleOffset = 0;
                    } else {
                        category.elementIndex++;
                        moduleOffset += add;
                    }
                }
                break;
            case Keyboard.KEY_UP:
                if (!expanded) {
                    if (tab == 0) {
                        tab = tabList.size() - 2;
                        yOffset = add * (tabList.size() - 1);
                    } else {
                        tab -= 1;
                        yOffset -= add;
                    }
                }
                break;
            case Keyboard.KEY_RIGHT:
                if (!expanded) {
                    for (Module module : modules) {
                        moduleTabList.add(new ModuleTab(module));
                    }
                    category.elementIndex = 0;
                    moduleOffset = 0;
                    expanded = true;
                } else {
                    modules.get(category.elementIndex).toggle();
                }
                break;
            case Keyboard.KEY_LEFT:
                if (expanded) {
                    moduleTabList.removeAll(moduleTabList);
                    category.elementIndex = 0;
                    moduleOffset = 0;
                    expanded = false;
                }
                break;
        }
    }
}
