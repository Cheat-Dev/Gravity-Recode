package team.gravityrecode.clientbase.impl.module.visual;

import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.optifine.Log;
import org.checkerframework.checker.units.qual.C;
import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.keyboard.KeyboardPressEvent;
import team.gravityrecode.clientbase.impl.event.render.Render2DEvent;
import team.gravityrecode.clientbase.impl.property.BooleanSetting;
import team.gravityrecode.clientbase.impl.property.ColorSetting;
import team.gravityrecode.clientbase.impl.property.EnumSetting;
import team.gravityrecode.clientbase.impl.property.interfaces.INameable;
import team.gravityrecode.clientbase.impl.util.client.Logger;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.network.BalanceUtil;
import team.gravityrecode.clientbase.impl.util.render.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(moduleName = "Draws_The_FUcking_Hud_To_See_what_bullshit_is_enabled_and_to_know_what_client_youre_on", moduleCategory = Module.ModuleCategory.VISUAL, moduleKeyBind = Keyboard.KEY_U)
public class Hud extends Module {

    private int tab;
    private boolean expanded;
    private EnumSetting<HudMode> mode = new EnumSetting<>(this, "Mode", HudMode.values());
    private BooleanSetting tabGui = new BooleanSetting(this, "TabGui", true, () -> mode.getValue().equals(HudMode.FLAT));
    public ColorSetting color = new ColorSetting(this, "Color", new Color(255, 255, 255));
    public BooleanSetting rainbow = new BooleanSetting(this, "Rainbow", false);
    public Draggable draggable = Client.INSTANCE.getDraggablesManager().createNewDraggable(this, "test", 4, 4, Fonts.INSTANCE.getUbuntu_light().getStringWidth("Gravity"), mc.fontRendererObj.FONT_HEIGHT);
    public List<Module> modules;

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if(mc.gameSettings.showDebugInfo)
            return;
        switch (mode.getValue()) {
            case FLAT:
                renderFlatWatermark(event);
                renderFlatArraylist(event);
                if (tabGui.getValue())
                    drawFlatTabgui();
                break;
            case GHOST:
                renderGhostHud(event);
                break;
            case BLOOM:
                renderBloomWatermark(event);
                renderBloomArraylist(event);
                break;
        }
        Fonts.INSTANCE.getSourceSansPro().drawString("Balance: " + BalanceUtil.INSTANCE.getBalance(), event.getScaledResolution().getScaledWidth() -
                Fonts.INSTANCE.getSourceSansPro().getStringWidth("Balance: " + BalanceUtil.INSTANCE.getBalance()) - 2, event.getScaledResolution().getScaledHeight() -
                Fonts.INSTANCE.getSourceSansPro().getHeight() - 2, rainbow.getValue() ? ColorUtil.rainbow(-8) : rainbow.getValue() ? ColorUtil.rainbow(-8) : color.getValue().getRGB());
    }

    @EventHandler
    public void onKeyPress(KeyboardPressEvent event) {
        if (tabGui.getValue())
            Client.INSTANCE.getTabGui().getKeyPresses(event, 14);
    }

    public void drawFlatTabgui() {
        Client.INSTANCE.getTabGui().renderTabGui(8, 10, 60, 0, 14);
        Client.INSTANCE.getTabGui().renderTabGuiModuleTabs(70, 10, 75, 0, 14);
    }

    public void renderFlatWatermark(Render2DEvent event) {
        Fonts.INSTANCE.getUbuntu_light().drawString(Client.INSTANCE.getClientInfo().getClientName(), draggable.getX() + 3, draggable.getY() + 1, rainbow.getValue() ? ColorUtil.rainbow(-8) : color.getValue().getRGB());
    }

    public void renderGhostHud(Render2DEvent event) {
        Fonts.INSTANCE.getUbuntu_light().drawString(Client.INSTANCE.getClientInfo().getClientName() + " V3",
                event.getScaledResolution().getScaledWidth() - Fonts.INSTANCE.getUbuntu_light().getStringWidth
                        (Client.INSTANCE.getClientInfo().getClientName() + " V3") - 2, 2,
                rainbow.getValue() ? ColorUtil.rainbow(8) : color.getColor());
        int y = 0;
        modules = getEnabledModules();
        for (Module module : modules) {
            int stringWidth = Fonts.INSTANCE.getSourceSansPro().getStringWidth(module.getModuleName());
            Fonts.INSTANCE.getSourceSansPro().drawString(module.getModuleName(), event.getScaledResolution().getScaledWidth() -
                    stringWidth - 2, y + 18, rainbow.getValue() ? ColorUtil.rainbow(y * 8) : color.getValue().getRGB());
            y += 10;
        }
    }

    public void renderFlatArraylist(Render2DEvent event) {
        int y = 0;
        modules = getEnabledModules();
        for (Module module : modules) {
            int stringWidth = Fonts.INSTANCE.getSourceSansPro().getStringWidth(module.getModuleName());
            TranslationUtils translate = module.getTranslate();


            int height = Fonts.INSTANCE.getSourceSansPro().getHeight();
            float posX = event.getScaledResolution().getScaledWidth() - stringWidth - 11;
            Gui.drawRect(posX, y + 5, event.getScaledResolution().getScaledWidth() - 6.2f, y + 17, new Color(10, 10, 10, 103).getRGB());
            Gui.drawRect(posX, y + 5, posX + 1.2f, y + 18, rainbow.getValue() ? ColorUtil.rainbow(y * 8) : color.getValue().getRGB());
            if (modules.indexOf(module) == modules.size() - 1)
                Gui.drawRect(posX, y + 18, posX + stringWidth + 6, y + 17, rainbow.getValue() ? ColorUtil.rainbow(y * 8) :
                        color.getValue().getRGB());
            else {
                final Module nextModule = modules.get(modules.indexOf(module) + 1);
                final float dist = (stringWidth - Fonts.INSTANCE.getSourceSansPro().getStringWidth(nextModule.getModuleName()));
                Gui.drawRect(posX, y + 18, posX + 1.2f + dist, y + 17, rainbow.getValue() ? ColorUtil.rainbow(y * 8) : color.getValue().getRGB());
            }
            if (modules.indexOf(module) == 0) {
                Gui.drawRect(posX, y + 5, posX + stringWidth + 6, y + 6, rainbow.getValue() ? ColorUtil.rainbow(y * 8) :
                        color.getValue().getRGB());
            }
            Gui.drawRect(event.getScaledResolution().getScaledWidth() - 6.2f, y + 5, event.getScaledResolution().getScaledWidth() - 5.2f,
                    y + 17, rainbow.getValue() ? ColorUtil.rainbow(y * 8) : color.getValue().getRGB());

            Fonts.INSTANCE.getSourceSansPro().drawString(module.getModuleName(), event.getScaledResolution().getScaledWidth() - stringWidth - 8, y + 8, rainbow.getValue() ? ColorUtil.rainbow(y * 8) : color.getValue().getRGB());
            y += 12;
        }
    }

    public void renderBloomWatermark(Render2DEvent event) {
        Client.INSTANCE.getBlurrer().bloom((int) draggable.getX() - 2, (int) draggable.getY() - 2, 62, 20, 8, 95);
        Fonts.INSTANCE.getUbuntu_light().drawString(Client.INSTANCE.getClientInfo().getClientName(), draggable.getX() + 3, draggable.getY() + 1, -1);
//        Fonts.INSTANCE.getUbuntu_light().drawString(Client.INSTANCE.getClientInfo().getClientName(), draggable.getX() + 3,
//                Client.INSTANCE.getModuleManager().getModule("TabGui").isEnabled() ? draggable.getY() + 3 : draggable.getY() + 1, -1);
    }

    public void renderBloomArraylist(Render2DEvent event) {
        int y = 0;
        for (Module module : modules) {
            int stringWidth = Fonts.INSTANCE.getSourceSansPro().getStringWidth(module.getModuleName());
            TranslationUtils translate = module.getTranslate();
            float translationFactor = 14.4F / Minecraft.getDebugFPS();
            float translateX = stringWidth - stringWidth - 2.0F;
            double translateY = translate.getY();
            /*
            Jinthium i need you to fix blur before i finish adding animations, as it flickers rn
             */
            if (module.isEnabled()) {
                translate.interpolate(translateX, y, translationFactor);
            } else {
                translate.interpolate(stringWidth, -11 - 1, translationFactor);
            }
            if (module.isEnabled()) {
                int xVal = event.getScaledResolution().getScaledWidth() - stringWidth - 4;
                Client.INSTANCE.getBlurrer().bloom(xVal - 8, (int) (y - Fonts.INSTANCE.getSourceSansPro().getHeight() + 11), stringWidth + 12, 16,
                        10, 95);
                Fonts.INSTANCE.getSourceSansPro().drawString(module.getModuleName(), event.getScaledResolution().getScaledWidth() - stringWidth - 8, y + 9, -1);
                y += 11;
            }
        }
    }

    @AllArgsConstructor
    public enum HudMode implements INameable {
        FLAT("Flat"), GHOST("Ghost"), BLOOM("Bloom");

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

    private final Comparator<Object> SORT_METHOD = Comparator.comparingDouble(m -> {
        Module module = (Module) m;
        String name = module.getModuleName();
        return Fonts.INSTANCE.getSourceSansPro().getStringWidth(name);
    }).reversed();

    public List<Module> getEnabledModules() {
        return Client.INSTANCE.getModuleManager().getModules().stream().filter(Module::isEnabled).sorted(SORT_METHOD).collect(Collectors.toList());
    }
}
