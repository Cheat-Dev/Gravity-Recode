package me.jinthium.clickgui.theme.implementations;

import me.jinthium.clickgui.component.implementations.*;
import me.jinthium.clickgui.panel.implementations.CategoryPanel;
import me.jinthium.clickgui.panel.implementations.ModulePanel;
import me.jinthium.clickgui.panel.implementations.MultiSelectPanel;
import me.jinthium.clickgui.theme.Theme;
import me.jinthium.shader.impl.BoxBlur;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.property.Property;
import team.gravityrecode.clientbase.api.util.MinecraftUtil;
import team.gravityrecode.clientbase.impl.module.visual.Hud;
import team.gravityrecode.clientbase.impl.property.ColorSetting;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.math.MathUtil;
import team.gravityrecode.clientbase.impl.util.render.RenderUtil;
import team.gravityrecode.clientbase.impl.util.render.RoundedUtil;
import team.gravityrecode.clientbase.impl.util.render.StencilUtil;
import team.gravityrecode.clientbase.impl.util.render.animations.Animation;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NewTheme implements Theme, MinecraftUtil {
    @Override
    public void drawCategory(CategoryPanel panel, float x, float y, float width, float height) {
        Hud hud = Client.INSTANCE.getModuleManager().getModule("Hud");
        panel.updateComponents();
        String name = StringUtils.upperSnakeCaseToPascal(panel.getCategory().name());
        RoundedUtil.drawRoundedRect(x, y, x + width, y + height, 4, new Color(25, 25, 25, 255).getRGB());
        Fonts.INSTANCE.getSourceSansPro().drawString(name, x + 4, y + panel.height() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2,
                new Color(220, 220, 220, 255).getRGB());
        Gui.drawRect(0, 0, 0, 0, -1);
    }

    @Override
    public void drawModule(ModulePanel panel, float x, float y, float width, float height) {
        Hud hud = Client.INSTANCE.getModuleManager().getModule("Hud");
        panel.updateComponents();
        Module.ModuleCategory category = panel.getModule().getModuleCategory();
        List<Module> list = Client.INSTANCE.getModuleManager().getModulesInCategory(category);
        Collections.sort(list, (p1, p2) -> String.CASE_INSENSITIVE_ORDER.compare(p1.getModuleName(), p2.getModuleName()));
        if (panel.isExtended()) {
            Gui.drawRect(x + 1, y + 17, x + width - 1, y + panel.totalHeight() - 1, new Color(30, 30, 30, 255).getRGB());
        }
        RoundedUtil.drawRoundedRect(x + 2, y, x + width - 2, y + height - 2, 8, panel.getModule().isEnabled() ? hud.tabGuiColour : new Color(0, 0, 0, 0).getRGB());
        RenderUtil.color(panel.getModule().isEnabled() ? -1 : new Color(220, 220, 220, 255).getRGB());
        Fonts.INSTANCE.getSourceSansPro().drawString(panel.getModule().getModuleName(), x + 4, y + panel.height() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 1.5, -1);
        int counter = 0;
        if (Client.INSTANCE.getPropertyManager().get(panel.getModule()).length != 0) {
            Fonts.INSTANCE.getSourceSansPro().drawString("+", x + width - 10, y + panel.height() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 1.5, -1);
        }
        for (Property property : Client.INSTANCE.getPropertyManager().get(panel.getModule())) {
            if (property instanceof ColorSetting) {
                RenderUtil.drawCGuiCircle(x + width - 12 * counter - 7 - 13, y + height / 2 - 1, 5.5f, new Color(25, 25, 25, 255).getRGB());
                RenderUtil.drawCGuiCircle(x + width - 12 * counter - 7 - 13, y + height / 2 - 1, 4, ((ColorSetting) property).getColor());
                counter++;
            }
        }
    }

    @Override
    public void drawMulti(MultiSelectPanel component, float x, float y, float width, float height) {
        Hud hud = Client.INSTANCE.getModuleManager().getModule("Hud");
        RoundedUtil.drawRoundedRect(x + 2, y, x + width - 2, y + height - 3, 4, new Color(35, 35, 35, 255).getRGB());
        RenderUtil.color(-1);
        if (component.isExtended()) {
            component.count = 2;
            for (int i = 0; i < component.getSetting().getValue().size(); i++) {
                String enumName = component.getSetting().getValue().get(i).getName();
                if (component.getSetting().isSelected(component.getSetting().getValue().get(i).getName()))
                    RoundedUtil.drawRoundedRect(x + 4, y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 1, (x + width) - 4,
                            y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 1 + Fonts.INSTANCE.getSourceSansPro().getHeight() + 4, 8,
                            hud.tabGuiColour);
                Fonts.INSTANCE.getSourceSansPro().drawString(enumName, (float) (x + width / 2.0 - Fonts.INSTANCE.getSourceSansPro()
                        .getStringWidth(enumName) / 2.0), y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 3, -1);
                component.count += Fonts.INSTANCE.getSourceSansPro().getHeight() + 7;
            }
            component.setHeight(RenderUtil.animate(Fonts.INSTANCE.getSourceSansPro().getHeight() + component.count + 16, component.height(), 0.1f));
        } else {
            component.setHeight(RenderUtil.animate(component.origHeight() + 7, component.height(), 0.1f));
        }
        Fonts.INSTANCE.getSourceSansPro().drawString(component.getSetting().getName(), x + 4, y + component.getOrigHeight() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 2, -1);
        Fonts.INSTANCE.getSourceSansPro().drawString("...", x + 4, y + component.getOrigHeight() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 2 + Fonts.INSTANCE.getSourceSansPro().getHeight() + 2, new Color(255, 255, 255, 180).getRGB());
    }

    @Override
    public void drawBindComponent(Module module, float x, float y, float width, float height, boolean focused) {
        RenderUtil.color(-1);
        String text = "Bind: [" + (focused ? " " : Keyboard.getKeyName(module.getKeyBind())) + "]";
        Fonts.INSTANCE.getSourceSansPro().drawString(text, x + 4, y + height / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2, -1);
    }

    @Override
    public void drawBooleanComponent(BooleanComponent component, float x, float y, float width, float height, float settingWidth, float settingHeight, int opacity) {
        Hud hud = Client.INSTANCE.getModuleManager().getModule("Hud");
        String label = component.getSetting().getName();
        Fonts.INSTANCE.getSourceSansPro().drawString(label, x + 4, y + component.height() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2, -1);
        RoundedUtil.drawRoundedRect(x + width - 15, y + height / 2 - 1 - 1, x + width - 4, y + height / 2 + 1, 3,
                component.getSetting().getValue() ? Color.green.darker().getRGB() : Color.red.darker().getRGB());
        if (!component.getSetting().getValue()) {
            RenderUtil.drawCGuiCircle(x + width - 13, y + height / 2 - 1, 3.5f, -1);
        } else {
            RenderUtil.drawCGuiCircle(x + width - 6, y + height / 2 - 1, 3.5f, -1);
        }
    }

    @Override
    public void drawEnumComponent(EnumComponent component, float x, float y, float width, float height) {
        Hud hud = Client.INSTANCE.getModuleManager().getModule("Hud");
        RoundedUtil.drawRoundedRect(x + 2, y, x + width - 2, y + height - 3, 4, new Color(35, 35, 35, 255).getRGB());
        RenderUtil.color(-1);
        if (component.extended) {
            component.count = 2;
            for (int i = 0; i < component.getSetting().getModeList().size(); i++) {
                String enumName = component.getSetting().getModeList().get(i).getName();
                if (component.getSetting().getValue() == component.getSetting().getModeList().get(i)) {
                    RoundedUtil.drawRoundedRect(x + 4, y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 1, (x + width) - 4,
                            y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 1 + Fonts.INSTANCE.getSourceSansPro().getHeight() + 4, 5,
                            hud.tabGuiColour);
                }
                Fonts.INSTANCE.getSourceSansPro().drawString(enumName, (float) (x + width / 2.0 - Fonts.INSTANCE.getSourceSansPro()
                        .getStringWidth(enumName) / 2.0), y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 3, -1);
                component.count += Fonts.INSTANCE.getSourceSansPro().getHeight() + 8;
            }
            component.setHeight(RenderUtil.animate(Fonts.INSTANCE.getSourceSansPro().getHeight() + component.count + 16, component.height(), 0.05f));
        } else {
            component.setHeight(RenderUtil.animate(component.origHeight() + 7, component.height(), 0.05f));
        }

        Fonts.INSTANCE.getSourceSansPro().drawString(component.getSetting().getName(), x + 4, y + component.getOrigHeight() / 2 -
                Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 0.5f - 2, -1);
        Fonts.INSTANCE.getSourceSansPro().drawString(component.getSetting().getValue().getName(), x + 4, y + component.getOrigHeight() / 2 -
                        Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 0.5f - 2 + Fonts.INSTANCE.getSourceSansPro().getHeight() + 2,
                new Color(255, 255, 255, 180).getRGB());
    }

    @Override
    public void drawEnumSetComponent(EnumComponent2 component, float x, float y, float width, float height) {
        Hud hud = Client.INSTANCE.getModuleManager().getModule("Hud");
        RoundedUtil.drawRoundedRect(x + 2, y, x + width - 2, y + height - 5, 4, new Color(35, 35, 35, 255).getRGB());
        RenderUtil.color(-1);
        if (component.extended) {
            component.count = 2;
            for (int i = 0; i < component.getSetting().getEnumList().size(); i++) {
                String enumName = component.getSetting().getEnumList().get(i).getName();
                if (component.getSetting().getValue() == component.getSetting().getEnumList().get(i)) {
                    RoundedUtil.drawRoundedRect(x + 4, y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 1, (x + width) - 4,
                            y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 1 + Fonts.INSTANCE.getSourceSansPro().getHeight() + 4, 5,
                            hud.tabGuiColour);
                }
                Fonts.INSTANCE.getSourceSansPro().drawString(enumName, (float) (x + width / 2.0 - Fonts.INSTANCE.getSourceSansPro()
                        .getStringWidth(enumName) / 2.0), y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 3, -1);
                component.count += Fonts.INSTANCE.getSourceSansPro().getHeight() + 8;
            }
            component.setHeight(RenderUtil.animate(Fonts.INSTANCE.getSourceSansPro().getHeight() + component.count + 16, component.height(), 0.05f));
        } else {
            component.setHeight(RenderUtil.animate(component.origHeight() + 9, component.height(), 0.05f));
        }

        Fonts.INSTANCE.getSourceSansPro().drawString(component.getSetting().getName(), x + 4, y + component.getOrigHeight() / 2 -
                Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 2.5f, -1);
        Fonts.INSTANCE.getSourceSansPro().drawString(component.getSetting().getValue().getName(), x + 4, y + component.getOrigHeight() / 2 -
                Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 2.5f + Fonts.INSTANCE.getSourceSansPro().getHeight() + 2, -1);
    }

    @Override
    public void drawSliderComponent(SliderComponent component, float x, float y, float width, float height, float length) {
        GlStateManager.pushMatrix();
        Hud hud = Client.INSTANCE.getModuleManager().getModule("Hud");
        GlStateManager.translate(0, -2.5f, 0);
        RoundedUtil.drawRoundedRect(x + 5, y + height - 4, x + width - 5, y + height - 1, 2, new Color(-1).getRGB());
        RoundedUtil.drawRoundedRect(x + 5, y + height - 4f, x + length + 5, y + height - 1f, 2f, hud.tabGuiColour);
        // Draw.drawBorderedCircle(x + length, y+ height - 5, 3, -1,-1);
        //1RenderUtil.drawCGuiCircle(x + length+ 5, y + height + 1 - 5.5f + 1.5f, 3.5f, -1);
        RenderUtil.drawCGuiCircle(x + length + 5, y + height + 1 - 5.5f + 1.5f, 3f, hud.tabGuiColour);
        RenderUtil.color(-1);
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        Fonts.INSTANCE.getSourceSansPro().drawString(component.getSetting().getName() + ": " + decimalFormat.format(component.getSetting().getValue()),
                x + 4, y + height / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 2.5f, -1);
        GlStateManager.popMatrix();
    }

    @Override
    public void drawColorPickerComponent(ColorPickerComponent component, float x, float y, float width, float height) {
        if (component.extended) {
            component.setHeight(RenderUtil.animate(17 * 5, component.getHeight(), 0.1f));
            Color color = component.getSetting().getValue();
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            float hue = hsb[0];
            StencilUtil.initStencilToWrite();
            RoundedUtil.drawRoundedRect(x, y, x + width, y + height, 8, 0x90151515);
            StencilUtil.readStencilBuffer(1);
            RenderUtil.drawRect(x, y, x + width, y + height, Color.getHSBColor(hsb[0], 1, 1).getRGB());

            int brightnessMin = RenderUtil.toColorRGB(Color.HSBtoRGB(hue, 0, 1), 0).getRGB();
            int brightnessMax = RenderUtil.toColorRGB(Color.HSBtoRGB(hue, 0, 1), 255).getRGB();
            int saturationMin = RenderUtil.toColorRGB(Color.HSBtoRGB(hue, 1, 0), 0).getRGB();
            int saturationMax = RenderUtil.toColorRGB(Color.HSBtoRGB(hue, 1, 0), 255).getRGB();
            RenderUtil.drawGradientRect(x, y, x + width, y + height, brightnessMin, brightnessMax, true);
            Gui.drawGradientRect(x, y, x + width, y + height, saturationMin, saturationMax);
            StencilUtil.uninitStencilBuffer();
            RoundedUtil.drawRoundedOutline(x, y, x + width, y + height, 8, 3, 0xFF404040);
        } else {
            RenderUtil.color(-1);
            RoundedUtil.drawRoundedRect(x + 3, y, x + width - 3, y + height - 3, 8, 0x702D2D2D);
            RoundedUtil.drawRoundedOutline(x + 3, y, x + width - 3, y + height - 3, 8, 1.5f, 0xFF404040);
            Fonts.INSTANCE.getSourceSansPro().drawString(component.getSetting().getName(), x + 8 + 13, y + component.getHeight() / 2 -
                    Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 0.5f - 1 - 2, -1);
            RoundedUtil.drawRoundedRect(x + 8 + 13, y + component.getHeight() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 -
                            0.5f - 1 - 2 + Fonts.INSTANCE.getSourceSansPro().getHeight() + 3, x + 8 + 13 + mc.fontRendererObj.getStringWidth(component.getSetting().getName()),
                    y + component.getHeight() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 0.5f - 1 - 2 + Fonts.INSTANCE.getSourceSansPro().getHeight() +
                            3 + 2, 2, component.getSetting().getValue().getRGB());
            component.setHeight(RenderUtil.animate(25, component.getHeight(), 0.1f));
        }
    }
}