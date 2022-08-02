package me.jinthium.clickgui.theme.implementations;

import me.jinthium.clickgui.component.implementations.*;
import me.jinthium.clickgui.panel.implementations.CategoryPanel;
import me.jinthium.clickgui.panel.implementations.ModulePanel;
import me.jinthium.clickgui.panel.implementations.MultiSelectPanel;
import me.jinthium.clickgui.theme.Theme;
import me.jinthium.shader.impl.BoxBlur;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.property.Property;
import team.gravityrecode.clientbase.api.util.MinecraftUtil;
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
        panel.updateComponents();
        String name = StringUtils.upperSnakeCaseToPascal(panel.getCategory().name());
        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRoundedRect(x, y,  x + width,  y + height -1, 5,new Color(0,0,0,100).getRGB());
        StencilUtil.readStencilBuffer(1);
        Client.INSTANCE.getShaderManager().getShader(BoxBlur.class).drawBlur();
        StencilUtil.uninitStencilBuffer();
        RoundedUtil.drawRoundedRect(x, y,  x + width,  y + 16, 2,new Color(10,10,10,95).getRGB());

        RoundedUtil.drawRoundedRect(x, y,  x + width,  y + height - 1, 5,new Color(30,30,30,135).getRGB());
//        String categoryIcon = panel.getCategory() == Module.ModuleCategory.VISUAL ? "t" : panel.getCategory() == Module.ModuleCategory.COMBAT ? "s" :
//                panel.getCategory() == Module.ModuleCategory.MOVEMENT ? "u" :
//                panel.getCategory() == Module.ModuleCategory.PLAYER ? "o" : panel.getCategory() == Module.ModuleCategory.EXPLOIT ? "r" :
//                        panel.getCategory() == Module.ModuleCategory.MISC ? "v" : "e";
        Fonts.INSTANCE.getSourceSansPro().drawString(name, x + 4, y + panel.height() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2, new Color(220,220,220,255).getRGB());
//        Fonts.undefeated.drawString(categoryIcon, x + 4 + Fonts.googleMedium.getStringWidth(name) + 3, y + panel.height() / 2 - Fonts.moontitle.getHeight() / 2 + 2, -1);
        if(panel.isExtended())
            RenderUtil.drawGradientRect(x, y + 16 - 1, x + width, y + 16 + 1 - 2, new Color(30,30,30,50).getRGB(), new Color(30,30,30,30).getRGB(), false);
        Gui.drawRect(0, 0, 0, 0, -1);
//        Client.INSTANCE.getBlurrer().bloom(Math.round(x) - 3, Math.round(y) - 3, Math.round(width) + 3, Math.round(height) + 3, 8, 100);
//        RoundedUtil.drawRoundedOutline(x, y,  x + width,  y + height - 1, 5, 2, new Color(55, 55, 55).getRGB());

    }

    @Override
    public void drawModule(ModulePanel panel, float x, float y, float width, float height) {
        panel.updateComponents();
        Module.ModuleCategory category = panel.getModule().getModuleCategory();
        panel.setHeight(15);
        //ShaderRound.drawRound(x, y, width, height, 0,new Color(0xff212120));
        Animation hoverAnimation = panel.getAnimation();
        int count = 0;
        List<Module> list = Client.INSTANCE.getModuleManager().getModulesInCategory(category);
        Collections.sort(list, new Comparator<Module>() {
            @Override
            public int compare(Module p1, Module p2) {
                return String.CASE_INSENSITIVE_ORDER.compare(p1.getModuleName(), p2.getModuleName());
            }
        });
        count = list.indexOf(panel.getModule());
        Color main = RenderUtil.applyOpacity(new Color(25, 67, 169), (float) hoverAnimation.getOutput());
        Color second = RenderUtil.applyOpacity(new Color(25, 98, 158), (float) hoverAnimation.getOutput());
        if(panel.isExtended()) {
            Gui.drawRect(x + 1, y + 14, x + width - 1, y + panel.totalHeight() - 1, new Color(10, 10, 10, 45).getRGB());
        }

        RoundedUtil.drawRoundedRect(x, y - 1,  x + width,  y + height - 1, 0,panel.getModule().isEnabled() ? new Color(10,10,10,45).getRGB() : new Color(0,0,0,0).getRGB());
        RenderUtil.color(panel.getModule().isEnabled() ? -1 : new Color(220,220,220,255).getRGB());
        Fonts.INSTANCE.getSourceSansPro().drawString(panel.getModule().getModuleName(), x + 4, y + panel.height() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 0.5f, -1);
//        if(panel.getModule().getKeyBind() != 0) {
//            Fonts.icons315.drawString("L", x + 4 + Fonts.google.getStringWidth(panel.getModule().getName()) + 3, y + panel.height() / 2 - Fonts.googleSmall2.getHeight() / 2 - 0.5f + 1, -1);
//
//        }
//        if(Module.propertyRepository().propertiesBy(panel.getModule().getClass()).size() != 0) {
//            Fonts.icons315.drawString("K", x + width - 12, y + panel.height() / 2 - Fonts.googleSmall2.getHeight() / 2 - 0.5f + 1, new Color(255,255,255,100).getRGB());
//
//        }
        int counter = 0;
        for (Property property : Client.INSTANCE.getPropertyManager().get(panel.getModule())) {
            if(property instanceof ColorSetting) {
                RoundedUtil.drawRoundedRect(x + width - 12 * counter - 7 - 15 - 2, y + height / 2 - 3 - 2, x + width - 12 * counter - 7 + 6 - 15 + 2 + 12 * counter, y + height / 2 + 3 + 2, 8, 0xFF2D2D2D);

                RoundedUtil.drawRoundedOutline(x + width - 12 * counter - 7 - 15 - 2, y + height / 2 - 3 - 2, x + width - 12 * counter - 7 + 6 - 15 + 2 + 12 * counter, y + height / 2 + 3 + 2, 8, 2, 0xFF404040 );
                //RoundedUtil.drawSmoothRoundedRect(x + width - 12 * counter - 7 - 15, y + height / 2 - 3,  x + width - 12 * counter - 7 + 6 - 15,  y + height / 2 + 3, 6, ((ColorProperty)property).getValue().getRGB());
                counter++;
            }
        }
        int counter2 = 0;
        //Color
        for (Property property : Client.INSTANCE.getPropertyManager().get(panel.getModule())) {
            if(property instanceof ColorSetting) {
                //RoundedUtil.drawRoundedRect(x + width - 12 * counter2 - 7 - 15 - 2, y + height / 2 - 3 - 2, x + width - 12 * counter2 - 7 + 6 - 15 + 2, y + height / 2 + 3 + 2, 5, 0xFF2D2D2D);

                //RoundedUtil.drawRoundedOutline(x + width - 12 * counter - 7 - 15 - 2, y + height / 2 - 3 - 2, x + width - 12 * counter - 7 + 6 - 15 + 2, y + height / 2 + 3 + 2, 5, 2, 0xFF404040 );
                RoundedUtil.drawSmoothRoundedRect(x + width - 12 * counter2 - 7 - 15, y + height / 2 - 3,  x + width - 12 * counter2 - 7 + 6 - 15,  y + height / 2 + 3, 6, ((ColorSetting) property).getColor());
                counter2++;
            }
        }

    }

    @Override
    public void drawMulti(MultiSelectPanel component, float x, float y, float width, float height) {
        RoundedUtil.drawSmoothRoundedRect(x + 2, y, x + width - 2, y + height - 3, 5, new Color(50, 50, 50, 150).getRGB());
//        RenderUtil.color(-1);
        if (component.isExtended()) {
            component.count = 2;
            for (int i = 0; i < component.getSetting().getValue().size(); i++) {
                String enumName = component.getSetting().getValue().get(i).getName();
                if (component.getSetting().isSelected(component.getSetting().getValue().get(i).getName()))
                    RoundedUtil.drawSmoothRoundedRect(x + 4, y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 1, (x + width) - 4,
                            y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 1 + Fonts.INSTANCE.getSourceSansPro().getHeight() + 4, 5,
                            new Color(50, 50, 50, 155).darker().getRGB());
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
        String text = "Bind: [" + (focused ? " " : Keyboard.getKeyName(module.getKeyBind())) + "]";
        Fonts.INSTANCE.getSourceSansPro().drawString(text, x + 4, y + height / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2, -1);
    }

    @Override
    public void drawBooleanComponent(BooleanComponent component, float x, float y, float width, float height, float settingWidth, float settingHeight, int opacity) {
        String label = component.getSetting().getName();
        Fonts.INSTANCE.getSourceSansPro().drawString(label, x + 4, y + component.height() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2, -1);
        RoundedUtil.drawSmoothRoundedRect(x + width - 15, y + height / 2 -1 - 1, x + width - 5, y + height / 2 +1, 3,
                component.getSetting().getValue() ? Color.green.darker().getRGB() : Color.red.darker().getRGB());
        if(!component.getSetting().getValue()) {
            RenderUtil.drawCGuiCircle(x + width - 13, y + height / 2 - 1, 4f, -1);
        } else {
            RenderUtil.drawCGuiCircle(x + width - 6, y + height / 2 - 1, 4f, -1);
        }
    }

    @Override
    public void drawEnumComponent(EnumComponent component, float x, float y, float width, float height) {
        RoundedUtil.drawSmoothRoundedRect(x + 2, y, x + width - 2, y + height - 3, 5, 0x702D2D2D);
        RenderUtil.color(-1);
        if (component.extended) {
            component.count = 2;
            for (int i = 0; i < component.getSetting().getModeList().size(); i++) {
                String enumName = component.getSetting().getModeList().get(i).getName();
                if(component.getSetting().getValue() == component.getSetting().getModeList().get(i)){
                    RoundedUtil.drawSmoothRoundedRect(x + 4, y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 1, (x + width) - 4,
                            y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 1 + Fonts.INSTANCE.getSourceSansPro().getHeight() + 4, 5,
                            new Color(50, 50, 50, 155).darker().getRGB());
                }
                Fonts.INSTANCE.getSourceSansPro().drawString(enumName, (float) (x + width / 2.0 - Fonts.INSTANCE.getSourceSansPro()
                        .getStringWidth(enumName) / 2.0), y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 3, -1);
                component.count += Fonts.INSTANCE.getSourceSansPro().getHeight() + 8;
            }
            component.setHeight(RenderUtil.animate(Fonts.INSTANCE.getSourceSansPro().getHeight() + component.count + 16, component.height(), 0.1f));
        } else {
            component.setHeight(RenderUtil.animate(component.origHeight() + 7, component.height(), 0.1f));
        }

        Fonts.INSTANCE.getSourceSansPro().drawString(component.getSetting().getName(), x + 4, y + component.getOrigHeight() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 0.5f - 1, -1);
        Fonts.INSTANCE.getSourceSansPro().drawString(component.getSetting().getValue().getName(), x + 4, y + component.getOrigHeight() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 0.5f - 1 + Fonts.INSTANCE.getSourceSansPro().getHeight() + 2, new Color(255, 255, 255, 180).getRGB());
    }

    @Override
    public void drawEnumSetComponent(EnumComponent2 component, float x, float y, float width, float height) {
        RoundedUtil.drawSmoothRoundedRect(x + 2, y, x + width - 2, y + height - 3, 5, 0x702D2D2D);
        RenderUtil.color(-1);
        if (component.extended) {
            component.count = 2;
            for (int i = 0; i < component.getSetting().getEnumList().size(); i++) {
                String enumName = component.getSetting().getEnumList().get(i).getName();
                if(component.getSetting().getValue() == component.getSetting().getEnumList().get(i)){
                    RoundedUtil.drawSmoothRoundedRect(x + 4, y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 1, (x + width) - 4,
                            y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 1 + Fonts.INSTANCE.getSourceSansPro().getHeight() + 4, 5,
                            new Color(50, 50, 50, 155).darker().getRGB());
                }
                Fonts.INSTANCE.getSourceSansPro().drawString(enumName, (float) (x + width / 2.0 - Fonts.INSTANCE.getSourceSansPro()
                        .getStringWidth(enumName) / 2.0), y + Fonts.INSTANCE.getSourceSansPro().getHeight() * 3 + component.count + 3, -1);
                component.count += Fonts.INSTANCE.getSourceSansPro().getHeight() + 8;
            }
            component.setHeight(RenderUtil.animate(Fonts.INSTANCE.getSourceSansPro().getHeight() + component.count + 16, component.height(), 0.1f));
        } else {
            component.setHeight(RenderUtil.animate(component.origHeight() + 8, component.height(), 0.1f));
        }

        Fonts.INSTANCE.getSourceSansPro().drawString(component.getSetting().getName(), x + 4, y + component.getOrigHeight() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 0.5f - 1, -1);
        Fonts.INSTANCE.getSourceSansPro().drawString(component.getSetting().getValue().getName(), x + 4, y + component.getOrigHeight() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 0.5f - 1 + Fonts.INSTANCE.getSourceSansPro().getHeight() + 2, new Color(255, 255, 255, 180).getRGB());
    }

    @Override
    public void drawSliderComponent(SliderComponent component, float x, float y, float width, float height, float length) {
        GlStateManager.pushMatrix();

        GlStateManager.translate(0, -2, 0);
        RoundedUtil.drawSmoothRoundedRect(x + 5, y+ height- 3, x + width - 5, y + height - 2, 2, new Color(0xFF404040).getRGB());

        RoundedUtil.drawSmoothRoundedRect(x + 5, y+ height- 3.5f, x + length + 5, y + height - 1.5f, 2f, new Color(100, 100, 100).darker().getRGB());

        // Draw.drawBorderedCircle(x + length, y+ height - 5, 3, -1,-1);
        //1RenderUtil.drawCGuiCircle(x + length+ 5, y + height + 1 - 5.5f + 1.5f, 3.5f, -1);
        RenderUtil.drawCGuiCircle(x + length+ 5, y + height + 1 - 5.5f + 1.5f, 3f, -1);
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        Fonts.INSTANCE.getSourceSansPro().drawString(component.getSetting().getName() + ": " + decimalFormat.format(component.getSetting().getValue()), x + 4, y + height / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 2, new Color(230,230,230,255).getRGB());
        GlStateManager.popMatrix();
    }

    @Override
    public void drawColorPickerComponent(ColorPickerComponent component, float x, float y, float width, float height) {
        if(component.extended) {
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
            //mc.fontRendererObj.drawCenteredString(component.getSetting().getLabel(), x + width / 2, y - 6, new Color(240,240,240,250).getRGB());
            // Fonts.icons315.drawCenteredString("CD", x + width - 10, y + 4, new Color(240,240,240,250).getRGB());
        } else {
            RenderUtil.color(-1);
            RoundedUtil.drawSmoothRoundedRect(x + 3, y, x + width - 3, y + height - 3, 8, 0x702D2D2D);
            RoundedUtil.drawRoundedOutline(x + 3, y, x + width - 3, y + height - 3, 8, 1.5f, 0xFF404040);
            //Fonts.icons4.drawString("A", x + 9, y + height / 2 - 3, -1);
            // Fonts.icons4.drawString("C", x + width - 14, y + 5, -1);

            Fonts.INSTANCE.getSourceSansPro().drawString(component.getSetting().getName(), x + 8 + 13, y + component.getHeight() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 0.5f - 1 - 2, -1);
            RoundedUtil.drawSmoothRoundedRect(x + 8 + 13,  y + component.getHeight() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 0.5f - 1 - 2 + Fonts.INSTANCE.getSourceSansPro().getHeight() + 3, x + 8 + 13 + mc.fontRendererObj.getStringWidth(component.getSetting().getName()), y + component.getHeight() / 2 - Fonts.INSTANCE.getSourceSansPro().getHeight() / 2 - 0.5f - 1 - 2 + Fonts.INSTANCE.getSourceSansPro().getHeight() + 3 + 2,  2, component.getSetting().getValue().getRGB());

            component.setHeight(RenderUtil.animate(25, component.getHeight(), 0.1f));
        }
    }

}