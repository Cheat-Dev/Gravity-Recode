package me.jinthium.clickgui.component.implementations;

import me.jinthium.clickgui.component.SettingComponent;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import team.gravityrecode.clientbase.impl.property.EnumSetting;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.render.RenderUtil;

public class EnumComponent2 extends SettingComponent<EnumSetting<?>> {

    public boolean extended;
    public int count;

    public EnumComponent2(EnumSetting<?> setting, float x, float y, float width, float height) {
        super(setting, x, y, width, height);
    }

    public EnumComponent2(EnumSetting<?> setting, float x, float y, float width, float height, boolean visible) {
        super(setting, x, y, width, height, visible);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (!visible) return;
        theme.drawEnumSetComponent(this, x, y, width, height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
//        if (visible) {
//            if (RenderUtil.inBounds(x, y, x + width, y + height, mouseX, mouseY)) {
//                int index = Arrays.asList(setting.values()).indexOf(setting.getValue());
//                if (mouseButton == 0)
//                    if (index + 1 < setting.values().length) {
//                        setting.setValue(index++);
//                    } else index = 0;
//                else if (index - 1 > 0) {
//                    setting.setValue(index--);
//                } else index = 0;
//                setting.setValue(index);
//            }
//        }
        if(isHovered(mouseX, mouseY)) {
            if(mouseButton == 1) {
                extended = !extended;
            }
        }
        if(extended) {
            for(int i = 0; i < setting.getEnumList().size(); i++){
                if(RenderUtil.isHovered(x, y + (Fonts.INSTANCE.getSourceSansPro().getHeight() + 7) * i + 20, width, Fonts.INSTANCE.getSourceSansPro().getHeight() + 7, mouseX, mouseY)) {
                    setting.setValue(setting.getEnumList().get(i).getName());
                }
            }
            GL11.glPushMatrix();
            GL11.glPopMatrix();
            GlStateManager.color(1, 1, 1, 1);
            RenderUtil.color(-1);
        }
    }

    public boolean isHovered2(double x, double y, double width, double height, int mouseX, int mouseY) {
        return mouseX > x && mouseY > y && mouseX < width && mouseY < height;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}