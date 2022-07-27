package team.gravityrecode.clientbase.impl.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import team.gravityrecode.clientbase.impl.util.render.animations.Direction;
import team.gravityrecode.clientbase.impl.util.render.animations.SmoothStep;

public class PictureButtonUtil extends GuiButton {
	private ResourceLocation picture;
	protected boolean hovered;
	private int hoveredSize;
	private SmoothStep animations;
	private boolean showText;

	public PictureButtonUtil(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText,
                            ResourceLocation picture, boolean showText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.picture = picture;
		this.showText = showText;
	}

	/**
	 * Draws this button to the screen.
	 */
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width
				&& mouseY < this.yPosition + this.height;
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.blendFunc(770, 771);

		if (this.visible) {
			if (this.hovered) {
				if (animations == null) {
					animations = new SmoothStep(250, 1, Direction.FORWARDS);
				} else {
					animations.setDirection(Direction.FORWARDS);
				}
			} else {
				if (animations != null) {
					animations.setDirection(Direction.BACKWARDS);
				}
			}

			mc.getTextureManager().bindTexture(this.picture);
			Gui.drawScaledCustomSizeModalRect(this.xPosition, animations == null ? this.yPosition : (int) (this.yPosition - (animations.getOutput() * 12.0f)), 0.0F, 0.0F, this.width,
					this.height, this.width, this.height, (float) this.width, (float) this.height);
			if (showText)
				Minecraft.getMinecraft().fontRendererObj.drawString(this.displayString,
						this.xPosition + this.width / 2
								- Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.displayString) / 2 + 1,
						this.yPosition + this.height + 4, -1);
		}
	}
}
