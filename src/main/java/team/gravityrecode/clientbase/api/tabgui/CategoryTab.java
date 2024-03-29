package team.gravityrecode.clientbase.api.tabgui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.moduleBase.Module.ModuleCategory;
import team.gravityrecode.clientbase.impl.module.visual.Hud;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.render.ColorUtil;
import team.gravityrecode.clientbase.impl.util.render.RoundedUtil;

import java.awt.*;

@Getter
@Setter
@AllArgsConstructor
public class CategoryTab {

    private String tabName;
    private ModuleCategory category;

    public void drawTab(float x, float y, float width, final float height, int offset, int textOffset, int selectColour) {
        Fonts.INSTANCE.getSourceSansPro().drawString(category.categoryName, x + 2 + textOffset, y + 5, selectColour);
    }
}
