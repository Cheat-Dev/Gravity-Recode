package me.jinthium.clickgui.panel.implementations;

import lombok.Getter;
import lombok.Setter;
import me.jinthium.clickgui.component.SettingComponent;
import net.minecraft.client.Minecraft;
import team.gravityrecode.clientbase.impl.property.BooleanSetting;
import team.gravityrecode.clientbase.impl.property.MultipleBoolSetting;
import team.gravityrecode.clientbase.impl.util.util.render.RenderUtil;

import java.util.Arrays;

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
        hovered = RenderUtil.isHovered(x, y + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * 3 + count, width, Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, mouseX, mouseY);
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
            for(int i = 0; i < getProperty().getValue().size(); i++){
                if(getProperty().getValue().indexOf(i) % 2 != 0){
                    String enumName = getProperty().getValue().get(i).getName();
                    String enumName2 = getProperty().getValue().get(getProperty().getValue().size() - 1).getName();
                    if(isHovered2(x + 4 + Minecraft.getMinecraft().fontRendererObj.getStringWidth(enumName2) + 11 - 3 + 2,
                            y + (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 4) * (i - 1) + 15 - 3,
                            x + 4 + Minecraft.getMinecraft().fontRendererObj.getStringWidth(enumName2) + 11 +
                                    Minecraft.getMinecraft().fontRendererObj.getStringWidth(enumName) + 3 + 2,
                            y + (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 4)
                                    * (i - 1) + 8 + 15 + 3, mouseX, mouseY)){
                        // if(isHovered2(x, y + (Fonts.moonSmall.getHeight() + 4) * Arrays.asList(getProperty().getValues()).indexOf(e) + 15, x +  Fonts.moonSmall.getStringWidth(e.name()), y + (Fonts.moonSmall.getHeight() + 4) * Arrays.asList(getProperty().getValues()).indexOf(e) + 8 + 15, mouseX, mouseY)) {
                        if(getProperty().isSelected(getProperty().getValue().get(i).getName())) {
                            getProperty().getValue().get(i).setValue(false);
                        } else {
                            getProperty().getValue().get(i).setValue(true);
                        }
                    }
                }else{
                    String enumName = getProperty().getValue().get(i).getName();
                    if(isHovered2(x + 4 + 11 - 3 + 2 - 7, y + (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 4)
                            * i + 15 - 4, x + 4 + 11 + Minecraft.getMinecraft().fontRendererObj.getStringWidth(enumName) + 3 + 2 - 6,
                            y + (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 4) * i + 8 + 15 + 2, mouseX, mouseY)){
                        if(getProperty().isSelected(getProperty().getValue().get(i).getName())) {
                            getProperty().getValue().get(i).setValue(false);
                        } else {
                            getProperty().getValue().get(i).setValue(true);
                        }
                    }
                }
            }
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
