package me.jinthium.clickgui.component.implementations;

import me.jinthium.clickgui.component.SettingComponent;
import team.gravityrecode.clientbase.impl.property.NumberSetting;
import team.gravityrecode.clientbase.impl.util.util.math.MathUtil;
import team.gravityrecode.clientbase.impl.util.util.render.RenderUtil;

public class SliderComponent extends SettingComponent<NumberSetting> {

    private boolean sliding;

    public SliderComponent(NumberSetting setting, float x, float y, float width, float height) {
        super(setting, x, y, width, height);
    }

    public SliderComponent(NumberSetting setting, float x, float y, float width, float height, boolean visible) {
        super(setting, x, y, width, height, visible);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (!visible) return;
        double deltaMaxMin = setting.getMax() - setting.getMin();
        double startX = x;
        double length = ((setting.getValue() - setting.getMin()) / deltaMaxMin * (width - 10));
        if (sliding) {
            setting.setValue(MathUtil.round(setting.getMin() + (mouseX - startX) / width * deltaMaxMin, setting.getIncrement()));
        }
        theme.drawSliderComponent(this, (float)startX, y, width, height, (float)length);
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (visible && !sliding && mouseButton == 0 && RenderUtil.isHovered(x, y, width, height, mouseX, mouseY))
            sliding = true;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        sliding = false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}
