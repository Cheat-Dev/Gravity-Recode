package team.gravityrecode.clientbase.impl.module.visual;

import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.optifine.Log;
import net.optifine.util.MathUtils;
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

import team.gravityrecode.clientbase.impl.util.render.ColorUtil.ColorType;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(moduleName = "Hud", moduleCategory = Module.ModuleCategory.VISUAL, moduleKeyBind = Keyboard.KEY_U)
public class Hud extends Module {

    private int tab;
    private boolean expanded;
    private EnumSetting<HudMode> mode = new EnumSetting<>(this, "Mode", HudMode.values());
    public EnumSetting<ColorMode> colourMode = new EnumSetting<>(this, "Colour Mode", ColorMode.values());
    private BooleanSetting tabGui = new BooleanSetting(this, "TabGui", true, () -> mode.getValue().equals(HudMode.FLAT));
    public ColorSetting color = new ColorSetting(this, "Color", new Color(255, 255, 255));
    public Draggable draggable = Client.INSTANCE.getDraggablesManager().createNewDraggable(this, "test", 4, 4, Fonts.INSTANCE.getUbuntu_light().getStringWidth("Gravity"), mc.fontRendererObj.FONT_HEIGHT);
    public List<Module> modules;
    public int hudColour, arraylistColour;

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (mc.gameSettings.showDebugInfo)
            return;
        float y = 0;
        switch (colourMode.getValue()) {
            case CUSTOM:
                arraylistColour = hudColour = color.getValue().getRGB();
                break;
            case RAINBOW:
                hudColour = ColorUtil.rainbow(8);
                break;
            case GRADIENT:
                hudColour = ColorUtil.getGradientOffset(color.getValue(), color.getValue().darker(), ((Math.abs(((System.currentTimeMillis()) / 8)) / 100D)));
                break;
        }

        switch (mode.getValue()) {
            case FLAT:
                renderFlatWatermark(event);
                renderFlatArraylist(event, (int) y);
                if (tabGui.getValue())
                    drawFlatTabgui();
                break;
            case FLAT2:
                renderFlatWatermark(event);
                renderFlat2Arraylist(event, (int) y);
                if (tabGui.getValue())
                    drawFlatTabgui();
                break;
            case GHOST:
                renderGhostHud(event, (int) y);
                break;
            case BLOOM:
                renderBloomWatermark(event);
                renderBloomArraylist(event, (int) y);
                break;
        }
        double x = MathUtils.getDifference(mc.thePlayer.lastTickPosX, mc.thePlayer.posX);
        double z = MathUtils.getDifference(mc.thePlayer.lastTickPosZ, mc.thePlayer.posZ);
        Fonts.INSTANCE.getSourceSansPro().drawString("Balance: " + BalanceUtil.INSTANCE.getBalance(), event.getScaledResolution().getScaledWidth() -
                Fonts.INSTANCE.getSourceSansPro().getStringWidth("Balance: " + BalanceUtil.INSTANCE.getBalance()) - 2, event.getScaledResolution().getScaledHeight() -
                Fonts.INSTANCE.getSourceSansPro().getHeight() - 2, hudColour);
        DecimalFormat df = new DecimalFormat("#"), df2 = new DecimalFormat("#.#");
        String line = "X: " + df.format(mc.thePlayer.getEntityBoundingBox().maxX) + ", Y: " + df.format(mc.thePlayer.getEntityBoundingBox().maxY)
                + ", Z: " + df.format(mc.thePlayer.getEntityBoundingBox().maxZ);
        String line2 = "X: " + df.format(mc.thePlayer.getEntityBoundingBox().maxX) + ", Y: " + df.format(mc.thePlayer.getEntityBoundingBox().maxY)
                + ", Z: " + df.format(mc.thePlayer.getEntityBoundingBox().maxZ) + ", FPS: " + Minecraft.getDebugFPS() + ", bp/s " +
                df2.format((Math.sqrt(x * x + z * z) * 20) * mc.timer.timerSpeed);
        Fonts.INSTANCE.getSourceSansPro().drawString((mc.currentScreen instanceof GuiChat ? line2 : line),
                4, event.getScaledResolution().getScaledHeight() - Fonts.INSTANCE.getSourceSansPro().getHeight() - (mc.currentScreen instanceof GuiChat ? 16 : 4), hudColour);
        if(!(mc.currentScreen instanceof GuiChat))
        Fonts.INSTANCE.getSourceSansPro().drawString("FPS: " + Minecraft.getDebugFPS() + ", bp/s " + df2.format((Math.sqrt(x * x + z * z) * 20) * mc.timer.timerSpeed),
                4, event.getScaledResolution().getScaledHeight() - Fonts.INSTANCE.getSourceSansPro().getHeight() - 14, hudColour);
    }

    @EventHandler
    public void onKeyPress(KeyboardPressEvent event) {
        if (tabGui.getValue())
            Client.INSTANCE.getTabGui().getKeyPresses(event, 16);
    }

    public void drawFlatTabgui() {
        Client.INSTANCE.getTabGui().renderTabGui(8, 8, 60, 0, 16, hudColour);
        Client.INSTANCE.getTabGui().renderTabGuiModuleTabs(70, 8, 75, 0, 16, hudColour);
    }

    public void renderFlatWatermark(Render2DEvent event) {
        Fonts.INSTANCE.getUbuntu_light().drawString(Client.INSTANCE.getClientInfo().getClientName(), draggable.getX() + 3, draggable.getY() + 1, hudColour);
    }

    public void renderGhostHud(Render2DEvent event, int y) {
        Fonts.INSTANCE.getUbuntu_light().drawString(Client.INSTANCE.getClientInfo().getClientName() + " V3",
                event.getScaledResolution().getScaledWidth() - Fonts.INSTANCE.getUbuntu_light().getStringWidth
                        (Client.INSTANCE.getClientInfo().getClientName() + " V3") - 2, 2, hudColour);
        modules = getEnabledModules();
        for (Module module : modules) {
            int stringWidth = Fonts.INSTANCE.getSourceSansPro().getStringWidth(module.getModuleName());
            Fonts.INSTANCE.getSourceSansPro().drawString(module.getModuleName(), event.getScaledResolution().getScaledWidth() -
                    stringWidth - 2, y + 18, arraylistColour);
            y += 10;
            switch (colourMode.getValue()) {
                case RAINBOW:
                    arraylistColour = ColorUtil.rainbow(y * 8);
                    break;
                case GRADIENT:
                    arraylistColour = ColorUtil.getGradientOffset(color.getValue(), color.getValue().darker(), ((Math.abs(((System.currentTimeMillis()) / 8)) / 100D) + (y / (20)) - 123));
                    break;
            }
        }
    }

    public void renderFlatArraylist(Render2DEvent event, int y) {
        modules = getEnabledModules();
        for (Module module : modules) {
            int stringWidth = Fonts.INSTANCE.getSourceSansPro().getStringWidth(module.getModuleName());
            float posX = event.getScaledResolution().getScaledWidth() - stringWidth - 11;
            Gui.drawRect(posX, y + 5, event.getScaledResolution().getScaledWidth() - 6.2f, y + 16, new Color(10, 10, 10, 103).getRGB());
            Gui.drawRect(posX, y + 5, posX + 1.2f, y + 17, arraylistColour);
            if (modules.indexOf(module) == modules.size() - 1)
                Gui.drawRect(posX, y + 17, posX + stringWidth + 6, y + 16, arraylistColour);
            else {
                final Module nextModule = modules.get(modules.indexOf(module) + 1);
                final float dist = (stringWidth - Fonts.INSTANCE.getSourceSansPro().getStringWidth(nextModule.getModuleName()));
                Gui.drawRect(posX, y + 17, posX + 1.2f + dist, y + 16, arraylistColour);
            }
            if (modules.indexOf(module) == 0) {
                Gui.drawRect(posX, y + 5, posX + stringWidth + 6, y + 6, arraylistColour);
            }
            Gui.drawRect(event.getScaledResolution().getScaledWidth() - 6.2f, y + 5, event.getScaledResolution().getScaledWidth() - 5.2f,
                    y + 17, arraylistColour);

            Fonts.INSTANCE.getSourceSansPro().drawString(module.getModuleName(), event.getScaledResolution().getScaledWidth() -
                    stringWidth - 8, y + 7.5, arraylistColour);
            y += 11;
            switch (colourMode.getValue()) {
                case RAINBOW:
                    arraylistColour = ColorUtil.rainbow(y * 8);
                    break;
                case GRADIENT:
                    arraylistColour = ColorUtil.getGradientOffset(color.getValue(), color.getValue().darker(), ((Math.abs(((System.currentTimeMillis()) / 8)) / 100D) + (y / (20)) - 123));
                    break;
            }
        }
    }

    public void renderFlat2Arraylist(Render2DEvent event, int y) {
        modules = getEnabledModules();
        for (Module module : modules) {
            int stringWidth = Fonts.INSTANCE.getSourceSansPro().getStringWidth(module.getModuleName());
            float posX = event.getScaledResolution().getScaledWidth() - stringWidth - 4;
            Gui.drawRect(posX, y, event.getScaledResolution().getScaledWidth(), y + 11, new Color(10, 10, 10, 155).getRGB());
            Fonts.INSTANCE.getSourceSansPro().drawString(module.getModuleName(), event.getScaledResolution().getScaledWidth() -
                    stringWidth - 1.5f, y + 2.5, arraylistColour);
            y += 11;
            switch (colourMode.getValue()) {
                case RAINBOW:
                    arraylistColour = ColorUtil.rainbow(y * 8);
                    break;
                case GRADIENT:
                    arraylistColour = ColorUtil.getGradientOffset(color.getValue(), color.getValue().darker(), ((Math.abs(((System.currentTimeMillis()) / 8)) / 100D) + (y / (20)) - 123));
                    break;
            }
        }
    }

    public void renderBloomWatermark(Render2DEvent event) {
        Client.INSTANCE.getBlurrer().bloom((int) draggable.getX() - 2, (int) draggable.getY() - 2, 62, 20, 8, 95);
        Fonts.INSTANCE.getUbuntu_light().drawString(Client.INSTANCE.getClientInfo().getClientName(), draggable.getX() + 3, draggable.getY() + 1, hudColour);
    }

    public void renderBloomArraylist(Render2DEvent event, int y) {
        switch (colourMode.getValue()) {
            case RAINBOW:
                arraylistColour = ColorUtil.rainbow(y * 8);
                break;
            case GRADIENT:
                arraylistColour = ColorUtil.getGradientOffset(color.getValue(), color.getValue().darker(), ((Math.abs(((System.currentTimeMillis()) / 8)) / 100D) + (y / (20)) - 123));
                break;
        }
        for (Module module : modules) {
            int stringWidth = Fonts.INSTANCE.getSourceSansPro().getStringWidth(module.getModuleName());
            if (module.isEnabled()) {
                int xVal = event.getScaledResolution().getScaledWidth() - stringWidth - 4;
                Client.INSTANCE.getBlurrer().bloom(xVal - 8, (int) (y - Fonts.INSTANCE.getSourceSansPro().getHeight() + 11), stringWidth + 12, 16,
                        10, 95);
                Fonts.INSTANCE.getSourceSansPro().drawString(module.getModuleName(), event.getScaledResolution().getScaledWidth() - stringWidth - 8, y + 9, arraylistColour);
                y += 11;
                switch (colourMode.getValue()) {
                    case RAINBOW:
                        arraylistColour = ColorUtil.rainbow(y * 8);
                        break;
                    case GRADIENT:
                        arraylistColour = ColorUtil.getGradientOffset(color.getValue(), color.getValue().darker(), ((Math.abs(((System.currentTimeMillis()) / 8)) / 100D) + (y / (20)) - 123));
                        break;
                }
            }
        }
    }

    @AllArgsConstructor
    public enum HudMode implements INameable {
        FLAT("Flat"),
        GHOST("Ghost"),
        BLOOM("Bloom"),
        FLAT2("Flat2");

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

    @AllArgsConstructor
    public enum ColorMode implements INameable{
        CUSTOM("Custom"),
        RAINBOW("Reignbow"),
        GRADIENT("Gradient");

        private final String modeName;

        @Override
        public String getName() {
            return modeName;
        }
    }
}
