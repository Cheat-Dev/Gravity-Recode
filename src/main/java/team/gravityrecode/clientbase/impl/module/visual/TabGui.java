package team.gravityrecode.clientbase.impl.module.visual;

import com.sun.org.apache.xpath.internal.operations.Mod;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.keyboard.KeyboardPressEvent;
import team.gravityrecode.clientbase.impl.event.render.Render2DEvent;

import java.text.DecimalFormat;

@ModuleInfo(moduleName = "TabGui", moduleKeyBind = Keyboard.KEY_Y, moduleCategory = Module.ModuleCategory.VISUAL)
public class TabGui extends Module {

    private int tab;
    private boolean expanded;

    @EventHandler
    public void onRender2D(Render2DEvent event){
    drawTabGui(2, 25, event.getScaledResolution());
    }

    @EventHandler
    public void onKeyPressed(KeyboardPressEvent event){

    }

    public void drawTabGui(float x, float y, ScaledResolution scaledResolution){
        Client client = Client.INSTANCE;
        FontRenderer font = mc.fontRendererObj;
        ModuleCategory category = ModuleCategory.values()[tab];
    Client.INSTANCE.getBlurrer().bloom((int) x, (int) y, (int) x + 54, (int) y + ModuleCategory.values().length * 13, 8, 155);

        int count = 0;
        for (ModuleCategory c : ModuleCategory.values()) {
            if (c.categoryName == category.categoryName) {
                font.drawStringWithShadow(c.categoryName, x + 4, y + 2f + count * 13, -1);
            } else {
                font.drawStringWithShadow(c.categoryName, x + 1, y + 2f + count * 13, -1);
            }
            count++;
        }
    }
}
