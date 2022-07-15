package me.jinthium.clickgui.theme;


import me.jinthium.clickgui.component.implementations.BooleanComponent;
import me.jinthium.clickgui.component.implementations.ColorPickerComponent;
import me.jinthium.clickgui.component.implementations.EnumComponent;
import me.jinthium.clickgui.component.implementations.SliderComponent;
import me.jinthium.clickgui.panel.implementations.CategoryPanel;
import me.jinthium.clickgui.panel.implementations.ModulePanel;
import me.jinthium.clickgui.panel.implementations.MultiSelectPanel;
import team.gravityrecode.clientbase.api.moduleBase.Module;

public interface Theme {
    void drawCategory(CategoryPanel panel, float x, float y, float width, float height);
    void drawModule(ModulePanel panel, float x, float y, float width, float height);
    void drawMulti(MultiSelectPanel panel, float x, float y, float width, float height);
    void drawBindComponent(Module module, float x, float y, float width, float height, boolean focused);
    void drawBooleanComponent(BooleanComponent component, float x, float y, float width, float height, float settingWidth, float settingHeight, int opacity);
    void drawEnumComponent(EnumComponent component, float x, float y, float width, float height);
    void drawSliderComponent(SliderComponent component, float x, float y, float width, float height, float length);
    void drawColorPickerComponent(ColorPickerComponent component, float x, float y, float width, float height);
}
