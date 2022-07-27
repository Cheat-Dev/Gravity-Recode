package team.gravityrecode.clientbase.impl.mainmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.render.RenderUtil;
import team.gravityrecode.clientbase.impl.util.render.RoundedUtil;
import team.gravityrecode.clientbase.impl.util.render.animations.Animation;
import team.gravityrecode.clientbase.impl.util.render.animations.Direction;
import team.gravityrecode.clientbase.impl.util.render.animations.SmoothStep;

import java.awt.*;

public class CustomButton extends GuiButton {
    private ResourceLocation picture;
    protected boolean hovered;
    private int hoveredSize;
    private Animation animations;

    public CustomButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText,
                             ResourceLocation picture) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.picture = picture;
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
            float offset = animations != null ? Fonts.INSTANCE.getUbuntu_light().getStringWidth(this.displayString) : 0;
            float offset2 = animations != null ? -120 : 0;
//            RoundedUtil.drawSmoothRoundedRect((float) -10, (float) (this.yPosition - 3),
//                    Math.max(this.xPosition + this.width, offset + (animations == null ? this.xPosition + width : (int) ((this.xPosition + width) + animations.getOutput() * 50)) + offset2) + 3,
//                    this.yPosition + this.height + 3, 0, new Color(20, 20, 20).getRGB());

            Client.INSTANCE.getBlurrer().bloom((int) -10, (int) (this.yPosition - 3),
                    (int) (Math.max(this.width, offset + (animations == null ? width : (int) ((width) + animations.getOutput() * 50)) + offset2) + 25),
                    this.height + 3, 15, new Color(20, 20, 20));

            RoundedUtil.drawRoundedOutline((float) -10, (float) (this.yPosition - 3),
                    Math.max(this.xPosition + this.width, offset + (animations == null ? this.xPosition + width : (int) ((this.xPosition + width) + animations.getOutput() * 50)) + offset2) + 3,
                    this.yPosition + this.height + 3, 0, 3.5f, 255);
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

            int currentButtonStuff = animations == null ? this.xPosition : (int) (this.xPosition - animations.getOutput()  * (50) - Fonts.INSTANCE.getUbuntu_light().getStringWidth(this.displayString) / 2);

            if(animations != null && !animations.finished(Direction.BACKWARDS)) {
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtil.scissor((float) this.xPosition - 8, (float) this.yPosition, Math.max(xPosition, (float) ((this.xPosition) + animations.getOutput() * (50) - Fonts.INSTANCE.getUbuntu_light().getStringWidth(this.displayString) / 2)), this.height);
//                RenderUtil.makeCropBox();
                Fonts.INSTANCE.getUbuntu_light().drawString(this.displayString, this.xPosition - 1,
                        this.yPosition + this.height / 2 - Fonts.INSTANCE.getUbuntu_light().getHeight() / 2, Color.WHITE.getRGB());
//                RenderUtil.destroyCropBox();
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }
            RenderUtil.drawImage(this.picture, Math.max(this.xPosition, offset + (animations == null ? this.xPosition : (int) (this.xPosition + animations.getOutput() * 50)) + offset2), this.yPosition, this.width, this.height);
            //RenderUtil.drawImage(this.picture, this.xPosition, animations == null ? this.yPosition : (int) (this.yPosition - (animations.getOutput() * 12.0f)), this.width, this.height);
        }
    }
}
