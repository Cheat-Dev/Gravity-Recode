package team.gravityrecode.clientbase.impl.mainmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import team.gravityrecode.clientbase.impl.util.util.client.Logger;
import team.gravityrecode.clientbase.impl.util.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.util.render.RenderUtil;
import team.gravityrecode.clientbase.impl.util.util.render.RoundedUtil;
import team.gravityrecode.clientbase.impl.util.util.render.animations.Animation;
import team.gravityrecode.clientbase.impl.util.util.render.animations.Direction;
import team.gravityrecode.clientbase.impl.util.util.render.animations.SmoothStep;

import java.awt.*;

public class CustomButton extends GuiButton {
    private ResourceLocation picture;
    public String buttonText;
    protected boolean hovered;
    private int hoveredSize;
    private Animation animations;

    public CustomButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText,
                             ResourceLocation picture) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.picture = picture;
        this.buttonText = buttonText;
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if(animations != null)
            Logger.printSysLog("Anim: " + animations.getOutput());

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < (this.xPosition + this.width)
                && mouseY < this.yPosition + this.height;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        if (this.visible) {
            RoundedUtil.drawSmoothRoundedRect((float) -10, (float) (this.yPosition - 3),
                    Math.max(this.xPosition + this.width, (float) (((this.xPosition + this.width) + (animations == null ? 0 : animations.getOutput() * (50) - Fonts.INSTANCE.getUbuntu_light().getStringWidth(this.buttonText) / 2)))) + 3,
                    this.yPosition + this.height + 3, 0, new Color(40, 40, 40).getRGB());
            RoundedUtil.drawRoundedOutline((float) -10, (float) (this.yPosition - 3),
                    Math.max(this.xPosition + this.width, (float) (((this.xPosition + this.width) + (animations == null ? 0 : animations.getOutput() * (50) - Fonts.INSTANCE.getUbuntu_light().getStringWidth(this.buttonText) / 2)))) + 3,
                    this.yPosition + this.height + 3, 0, 1.5f, new Color(25, 67, 169).getRGB());
            if (this.hovered) {
                if (animations == null) {
                    animations = new SmoothStep(300, 2.6, Direction.FORWARDS);
                } else {
                    animations.setDirection(Direction.FORWARDS);
                }
            } else {
                if (animations != null) {
                    animations.setDirection(Direction.BACKWARDS);

                    if(animations.finished(Direction.BACKWARDS))
                    animations = null;
                }
            }

            int currentButtonStuff = animations == null ? this.xPosition : (int) (this.xPosition - animations.getOutput()  * (50) - Fonts.INSTANCE.getUbuntu_light().getStringWidth(this.buttonText) / 2);

            if(animations != null && !animations.finished(Direction.BACKWARDS)) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtil.scissor((float) this.xPosition - 8, (float) this.yPosition, Math.max(xPosition, (float) ((this.xPosition) + animations.getOutput() * (50) - Fonts.INSTANCE.getUbuntu_light().getStringWidth(this.buttonText) / 2)), this.height);
//                RenderUtil.makeCropBox();
                Fonts.INSTANCE.getUbuntu_light().drawString(this.buttonText, this.xPosition - 1,
                        this.yPosition + this.height / 2 - Fonts.INSTANCE.getUbuntu_light().getHeight() / 2, Color.WHITE.getRGB());
//                RenderUtil.destroyCropBox();
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }

            RenderUtil.drawImage(this.picture, Math.max(xPosition, animations == null ? this.xPosition : (int) (this.xPosition + animations.getOutput()  * (50) - Fonts.INSTANCE.getUbuntu_light().getStringWidth(this.buttonText) / 2)), this.yPosition, this.width, this.height);
            //RenderUtil.drawImage(this.picture, this.xPosition, animations == null ? this.yPosition : (int) (this.yPosition - (animations.getOutput() * 12.0f)), this.width, this.height);
        }
    }
}