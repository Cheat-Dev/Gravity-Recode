package team.gravityrecode.clientbase.impl.mainmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.render.secondary.RenderUtils;

import java.awt.*;

public class TestButton extends GuiButton {
    protected boolean hovered;

    public TestButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText,
                      ResourceLocation picture) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.displayString = buttonText;
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < (this.xPosition + this.width)
                && mouseY < this.yPosition + this.height;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        if (this.visible) {
            int color = hovered ? 35 : 40;
            Client.INSTANCE.getBlurrer().bloom(hovered ? xPosition - 4 : xPosition - 2, hovered ? yPosition - 4 : yPosition - 2, hovered ? width + 8 : width + 6,
                    hovered ? height + 8 : height + 6, 6, 255);
                    RenderUtils.drawRoundedRectWithShadow(hovered ? xPosition - 2 : xPosition, hovered ? yPosition - 2 : yPosition, hovered ? width + 2 : width,
                            hovered ? height + 2 : height, 10, new Color(color, color, color, 30).getRGB());
            Fonts.INSTANCE.getUbuntu_light_small().drawCenteredString(this.displayString, this.xPosition + this.width / 2,
                    this.yPosition + this.height / 2 - Fonts.INSTANCE.getUbuntu_light_small().getHeight() / 2, Color.WHITE.getRGB());
        }
    }
}
