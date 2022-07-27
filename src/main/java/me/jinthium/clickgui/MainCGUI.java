package me.jinthium.clickgui;
import lombok.Getter;
import me.jinthium.clickgui.component.Component;
import me.jinthium.clickgui.component.implementations.*;
import me.jinthium.clickgui.panel.implementations.CategoryPanel;
import me.jinthium.clickgui.panel.implementations.ModulePanel;
import me.jinthium.clickgui.panel.implementations.MultiSelectPanel;
import me.jinthium.clickgui.theme.Theme;
import me.jinthium.clickgui.theme.implementations.MainTheme;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.property.Property;
import team.gravityrecode.clientbase.impl.property.*;
import team.gravityrecode.clientbase.impl.util.render.RenderUtil;

import java.io.IOException;
import java.util.*;

@Getter
public class MainCGUI extends GuiScreen {

    private final Theme currentTheme;

    private final List<Component> objects = new ArrayList<>();

    private final float componentWidth = 110;
    private final float componentHeight = 17;
    private float wantedX;

    double y = 0;
    float offset = 0;
    //private final BlurShader blurShader;

    public MainCGUI() {

        currentTheme = new MainTheme();

        //blurShader = new BlurShader(clickGUIModule.blurIntensityProperty().getValue().intValue());
        float posX = 6;
        float posY = 4;
        for (Module.ModuleCategory category : Module.ModuleCategory.values()) {
            objects.add(new CategoryPanel(category, posX, posY, componentWidth, componentHeight) {
                @Override
                public void init() {
                    List<Module> list = Client.INSTANCE.getModuleManager().getModulesInCategory(category);
                    Collections.sort(list, new Comparator<Module>() {
                        @Override
                        public int compare(Module p1, Module p2) {
                            return String.CASE_INSENSITIVE_ORDER.compare(p1.getModuleName(), p2.getModuleName());
                        }
                    });


                    for (Module module : list) {
                        getComponents().add(new ModulePanel(module, x, y, componentWidth, componentHeight) {
                            @Override
                            public void init() {
                                components.add(new BindComponent(module, x, y, componentWidth, componentHeight));
                                for (Property<?> property : Client.INSTANCE.getPropertyManager().get(module)) {
                                    Arrays.stream(Client.INSTANCE.getPropertyManager().get(module)).sorted();
                                    if(property instanceof EnumSetting<?>)
                                        components.add(new EnumComponent2((EnumSetting<?>) property, x, y, componentWidth, componentHeight, property.getVisible().getAsBoolean()));
                                    if (property instanceof BooleanSetting)
                                        components.add(new BooleanComponent((BooleanSetting)property, x, y, componentWidth, componentHeight, property.getVisible().getAsBoolean()));
                                    if (property instanceof ModeSetting)
                                        components.add(new EnumComponent((ModeSetting) property, x, y, componentWidth, componentHeight, property.getVisible().getAsBoolean()));
                                    if (property instanceof NumberSetting)
                                        components.add(new SliderComponent((NumberSetting) property, x, y, componentWidth, componentHeight, property.getVisible().getAsBoolean()));
                                    if (property instanceof MultipleBoolSetting)
                                        components.add(new MultiSelectPanel((MultipleBoolSetting) property, x, y, componentWidth, componentHeight, property.getVisible().getAsBoolean()));
                                    if (property instanceof ColorSetting)
                                        components.add(new ColorPickerComponent((ColorSetting) property, x, y, componentWidth, componentHeight * 5, property.getVisible().getAsBoolean()));
                                    updateComponents();
                                }
                                updateComponents();
                            }
                        });
                    }
                }
            });
            //objects.add(panel = new ConfigPanel(40, Display.getHeight() - 40, 47, 15));
            posX += componentWidth + 3;
        }
    }

    @Override
    public void initGui() {
        // this.panel.initGui();
        ScaledResolution scaledresolution = new ScaledResolution(mc);
        this.buttonList.add(new GuiButton(1, 20, scaledresolution.getScaledHeight() - 150, 100, 10, "Load"));
        objects.forEach(Component::reset);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }

    @Override
    public void onGuiClosed() {
        objects.forEach(panel -> {
            if (panel.isVisible()) panel.mouseReleased(0, 0, 0);
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //this.textField.drawTextBox();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        //RenderUtil.makeCropBox(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        objects.forEach(panel -> {
            panel.y(RenderUtil.animate(panel.origY() + panel.getOffset(), panel.getY(), 0.1f));
            if (panel.isVisible()) panel.drawScreen(mouseX, mouseY);
        });

        // RenderUtil.destroyCropBox();

        if(isHovered2(width - 100, height / 2 - 100, width, height / 2 + 100, mouseX, mouseY)) {
            wantedX = RenderUtil.animate(-100, wantedX, 0.1f);
        } else {
            wantedX = RenderUtil.animate(0, wantedX, 0.1f);
        }
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1,1,1,0.8f);
        RenderUtil.drawImage(new ResourceLocation("pulsabo/images/configmenu.png"), width - 50 + wantedX, (height /2f) - 205 / 2, 271, 205, true);

        //  ShaderRound.drawRound(15, scaledResolution.getScaledHeight() - 30, 20, 20, 10, new Color(12,12,12,200));
        //   ShaderRound.drawRound(15 + 22, scaledResolution.getScaledHeight() - 30, Fonts.moon.getStringWidth(Pulsive.INSTANCE.getClientDirConfigs().toString()) + 4, 20, 5, new Color(12,12,12,200));
        //  Fonts.moon.drawString(Pulsive.INSTANCE.getClientDirConfigs().toAbsolutePath().toString(),15 + 24, scaledResolution.getScaledHeight() - 23,-1);

        // Fonts.undefeated.drawString("v",20, scaledResolution.getScaledHeight() - 22,-1);
        Gui.drawRect(0,0,0,0,0);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        objects.forEach(panel -> {
            if (panel.isVisible()) panel.mouseClicked(mouseX, mouseY, mouseButton);
        });
        ScaledResolution scaledResolution = new ScaledResolution(mc);
//        if(isHovered2(width - 100, height / 2 - 100, width, height / 2 + 100, mouseX, mouseY)) {
//            mc.displayGuiScreen(new ConfigPanel(40, Display.getHeight() - 40, 47, 15));
//        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        objects.forEach(panel -> {
            if (panel.isVisible()) panel.mouseReleased(mouseX, mouseY, state);
        });
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        boolean focused = false;
        for (Component panel : objects)
            if (panel.isVisible() && panel.focused())
                focused = true;
        if (!focused) {
            super.keyTyped(typedChar, keyCode);
        }
        objects.forEach(panel -> {
            if (panel.isVisible()) panel.keyTyped(typedChar, keyCode);
        });
    }

    public void mouseClicked(int mouseX, int mouseY) {

    }

    public boolean isHovered2(double x, double y, double width, double height, int mouseX, int mouseY) {
        return mouseX > x && mouseY > y && mouseX < width && mouseY < height;
    }
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}