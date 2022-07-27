package me.jinthium.clickgui.panel.implementations;

import lombok.Getter;
import lombok.Setter;
import me.jinthium.clickgui.component.SettingComponent;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import team.gravityrecode.clientbase.impl.property.MultipleBoolSetting;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.render.RenderUtil;

@Getter
@Setter
public class MultiSelectPanel extends SettingComponent<MultipleBoolSetting> {

    private MultipleBoolSetting property;
    private boolean hovered;
    private boolean extended;
    int current;
    public int count = 0;

    public MultiSelectPanel(MultipleBoolSetting property, float x, float y, float width, float height, boolean visible) {
        super(property, x, y, width, height, visible);
        this.property = property;
//        Arrays.stream(property.getConstants()).forEach(constant -> {
//            Property<Boolean> setting = new Property<>(StringUtils.upperSnakeCaseToPascal(constant.toString()), property.isSelected(constant));
//            setting.addValueChange(((oldValue, value) -> {
//                int index = 0;
//                for (Enum constants : property.getConstants()) {
//                    if (constants == constant)
//                        property.setValue(index, value);
//                    index++;
//                }
//            }));
//            components.add(new BooleanComponent(setting, x, y, width, height));
//        });
    }

    public boolean isaVisible() {
        return property.getVisible().getAsBoolean();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (visible) {
            theme.drawMulti(this, x, y, width, height);
        }
        //super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isHovered(mouseX, mouseY)) {
            if(mouseButton == 1) {
                extended = !extended;
            }
        }
        if(extended) {
            for(int i = 0; i < setting.getValue().size(); i++){
                if(RenderUtil.isHovered(x, y + (Fonts.INSTANCE.getSourceSansPro().getHeight() + 7) * i + 20, width, Fonts.INSTANCE.getSourceSansPro().getHeight() + 7, mouseX, mouseY)) {
                    if(setting.isSelected(setting.getValue().get(i).getName())){
                        setting.getValue().get(i).setValue(false);
                    } else {
                        setting.getValue().get(i).setValue(true);
                    }
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
