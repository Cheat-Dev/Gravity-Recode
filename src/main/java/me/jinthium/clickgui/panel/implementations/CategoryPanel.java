package me.jinthium.clickgui.panel.implementations;

import lombok.Getter;
import lombok.Setter;
import me.jinthium.clickgui.panel.Panel;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.impl.util.util.render.RenderUtil;

@Setter
@Getter
public class CategoryPanel extends Panel {

    private float dragX, dragY;
    private Module.ModuleCategory category;
    private boolean dragging;

    public CategoryPanel(Module.ModuleCategory category, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.category = category;
    }

    @Override
    public void reset() {
        origHeight = height;
        super.reset();
    }

    @Override
    public boolean isVisible() {
        return visible || Math.round(origHeight) != Math.round(totalHeight());
    }

    @Override
    public boolean isExtended() {
        return extended || Math.round(origHeight) != Math.round(totalHeight());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (Mouse.hasWheel() && RenderUtil.isHovered(getX(), getY(), getWidth(), Minecraft.getMinecraft().displayHeight, mouseX, mouseY) && origHeight + 2 > Minecraft.getMinecraft().displayHeight / 2 - 100) {
            int wheel = Mouse.getDWheel();
            offset += wheel > 0 ? 25 : wheel < 0 ? -25 : 0;
            if(offset > 0) {
                offset = 0;
            }
            if(offset < -0) {
                if(offset < -Minecraft.getMinecraft().displayHeight / 2)
                    offset = Minecraft.getMinecraft().displayHeight / 2;

            }
        } else {
            offset = 0;
        }
        if (dragging) {
            x = mouseX + dragX;
            y = mouseY + dragY;
            origX = x;
            origY = y;
        }
        RenderUtil.makeCropBox(x - 2, y - 2, x + width + 2, y + origHeight + 2);
        origHeight = RenderUtil.animate(totalHeight(), origHeight, 0.1f);
        if (origHeight < 0) origHeight = 0;
        theme.drawCategory(this, x, y, width, Math.round(origHeight));
        super.drawScreen(mouseX, mouseY);
        RenderUtil.destroyCropBox();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovered(x, y, width, height, mouseX, mouseY)) {
            if (mouseButton == 0) {
                dragging = true;
                dragX = (x - mouseX);
                dragY = (y - mouseY);
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        dragging = false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }
}
