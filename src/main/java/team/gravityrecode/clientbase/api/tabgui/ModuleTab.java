package team.gravityrecode.clientbase.api.tabgui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.Module.ModuleCategory;
import team.gravityrecode.clientbase.impl.module.visual.Hud;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.render.ColorUtil;

import java.awt.*;

@Getter
@Setter
@AllArgsConstructor
public class ModuleTab {

    private Module module;

    public void drawTab(float x, float y, float width, float height, int offset, int textOffset, int selectColour) {
        Hud hud = Client.INSTANCE.getModuleManager().getModule("Hud");
        Gui.drawRect(x, y, x + width, y + offset, new Color(10, 10, 10, 100).getRGB());
        Fonts.INSTANCE.getSourceSansPro().drawString(module.getModuleName(), x + 2 + textOffset, y + 4, selectColour);
    }
}
