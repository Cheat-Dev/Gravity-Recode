package team.gravityrecode.clientbase.api.tabgui;

import com.google.common.io.ByteSource;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.Module.ModuleCategory;
import team.gravityrecode.clientbase.impl.event.keyboard.KeyboardPressEvent;
import team.gravityrecode.clientbase.impl.module.visual.Hud;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.render.ColorUtil;
import team.gravityrecode.clientbase.impl.util.render.RenderUtil;
import team.gravityrecode.clientbase.impl.util.render.RoundedUtil;
import team.gravityrecode.clientbase.impl.util.render.animations.Animation;
import team.gravityrecode.clientbase.impl.util.render.animations.Direction;
import team.gravityrecode.clientbase.impl.util.render.animations.impl.EaseInOutRect;
import team.gravityrecode.clientbase.impl.util.render.secondary.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

@Getter
public class TabGui {

    private List<CategoryTab> tabList = new ArrayList<>();
    private List<ModuleTab> moduleTabList = new ArrayList<>();
    private int tab;
    private int tabOffset, yOffset, moduleOffset, currentOffset;
    private boolean expanded;
    private final Animation upNDownanim = new EaseInOutRect(250, 1), extendedAnim = new EaseInOutRect(250, 1), extendedUpDownAnim = new EaseInOutRect(250, 1);

    public void init() {
        for (ModuleCategory category : ModuleCategory.values()) {
            if (!(category == ModuleCategory.SCRIPT))
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

    public void renderTabGui(float x, float y, float width, float height, int offset, int color) {
        Hud hud = Client.INSTANCE.getModuleManager().getModule("Hud");
        RoundedUtil.drawRoundedRect(x, y + offset, x + width, y + tabList.size() * offset + offset, 8, new Color(25, 25, 25, 255).getRGB());
        RoundedUtil.drawRoundedRect(x, (float) (y + (offset * upNDownanim.getOutput()) + yOffset), x + width, (float)
                        (y + (offset * 2 * upNDownanim.getOutput()) + yOffset), 8, hud.arraylistColour);
        for (CategoryTab tab : tabList) {
            height += offset;
            tab.drawTab(x, y + height, width, y, offset, tabList.get(this.tab) == tab ? (int) (2 * upNDownanim.getOutput()) : 0, -1);
        }
    }

    public void renderTabGuiModuleTabs(float x, float y, float width, float height, int offset, int color) {
        Hud hud = Client.INSTANCE.getModuleManager().getModule("Hud");
        if (expanded) {
            ModuleCategory category = ModuleCategory.values()[tab];
            if (extendedAnim.finished(Direction.BACKWARDS)) {
                moduleTabList.removeAll(moduleTabList);
                category.elementIndex = 0;
                moduleOffset = 0;
                expanded = false;
            }
            RenderUtil.scissor(x, y + offset + yOffset, (float) ((width + 4) * extendedAnim.getOutput()), y + moduleTabList.size() * offset + offset);
            for (ModuleTab moduleTab : moduleTabList) {
                RoundedUtil.drawRoundedRect(x, y + offset + yOffset, x + width, y + moduleTabList.size() * offset + offset + yOffset, 8, new Color(25, 25, 25, 255).getRGB());
            }
            RoundedUtil.drawRoundedRect(x, (float) (y + (offset * extendedUpDownAnim.getOutput()) + moduleOffset + yOffset), (float) (x + ((width) *
                            extendedAnim.getOutput())), (float) (y + (offset * 2 * extendedUpDownAnim.getOutput()) + moduleOffset + yOffset),
                    8, hud.arraylistColour);
//            RoundedUtil.drawRoundedRect(x, (float) (y + (offset * extendedUpDownAnim.getOutput()) + moduleOffset + yOffset), (float) (x + (4 *
//                            extendedAnim.getOutput())), (float) (y + (offset * 2 * extendedUpDownAnim.getOutput()) + moduleOffset + yOffset),
//                    4, hud.arraylistColour);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            glPopMatrix();
            for (ModuleTab moduleTab : moduleTabList) {
                height += offset;
                RenderUtil.scissor(x, y + height + yOffset, (float) ((width + 4) * extendedAnim.getOutput()), y + height);
                moduleTab.drawTab(x + 2, y + height + yOffset, (float) ((width + 4) * extendedAnim.getOutput()), y, offset, moduleTabList.get(ModuleCategory.values()[tab].
                                elementIndex) == moduleTab ? (int) (4 * extendedUpDownAnim.getOutput()) : 0, -1);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                glPopMatrix();
            }
        }
    }

    public void getKeyPresses(KeyboardPressEvent event, int add) {
        ModuleCategory category = ModuleCategory.values()[tab];
        List<Module> modules = Client.INSTANCE.getModuleManager().getModulesInCategory(category);
        switch (event.getKeyCode()) {
            case Keyboard.KEY_DOWN:
                if (!expanded) {


                    if(upNDownanim.getDirection() == Direction.BACKWARDS)
                        upNDownanim.setDirection(Direction.FORWARDS);
                    else
                        upNDownanim.reset();

                    if (tab == tabList.size() - 1) {
                        tab = 0;
                        yOffset = 0;
                    } else {
                        yOffset += add;
                        tab++;
                    }
                } else {
                    extendedUpDownAnim.reset();
                    if (category.elementIndex == modules.size() - 1) {
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
                    if(upNDownanim.getDirection() == Direction.BACKWARDS)
                        upNDownanim.setDirection(Direction.FORWARDS);
                    else
                        upNDownanim.reset();
                    if (tab == 0) {
                        tab = tabList.size() - 1;
                        yOffset = add * (tabList.size() - 1);
                    } else {
                        tab -= 1;
                        yOffset -= add;
                    }
                }
                break;
            case Keyboard.KEY_RIGHT:
                if (!expanded) {
                    if(extendedAnim.getDirection() == Direction.BACKWARDS)
                        extendedAnim.setDirection(Direction.FORWARDS);
                    else
                        extendedAnim.reset();
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
                    extendedAnim.setDirection(Direction.BACKWARDS);
                }
                break;
        }
    }
}
