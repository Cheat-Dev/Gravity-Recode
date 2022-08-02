package team.gravityrecode.clientbase.api.tabgui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;
import team.gravityrecode.clientbase.api.moduleBase.Module.ModuleCategory;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;

import java.awt.*;

@Getter
@Setter
@AllArgsConstructor
public class CategoryTab {

    private String tabName;
    private ModuleCategory category;

    public void drawTab(float x, float y, float width, float height, int offset) {
        Gui.drawRect(x, y, x + width, y + offset, new Color(10, 10, 10, 100).getRGB());
        Fonts.INSTANCE.getSourceSansPro().drawString(category.categoryName, x + 2, y + 4, -1);
    }
}
