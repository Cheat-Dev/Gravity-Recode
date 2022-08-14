package team.gravityrecode.clientbase.impl.module.visual;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.render.Render2DEvent;
import team.gravityrecode.clientbase.impl.event.render.RenderNametagEvent;
import team.gravityrecode.clientbase.impl.property.*;
import team.gravityrecode.clientbase.impl.property.interfaces.INameable;
import team.gravityrecode.clientbase.impl.util.foint.Fonts;
import team.gravityrecode.clientbase.impl.util.player.PlayerUtil;
import team.gravityrecode.clientbase.impl.util.render.RenderUtil;
import team.gravityrecode.clientbase.impl.util.world.WorldUtil;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(moduleName = "ESP", moduleDescription = "See things through walls.", moduleCategory = Module.ModuleCategory.VISUAL)
public class ESP extends Module {

    private final MultipleBoolSetting targetsProperty = new MultipleBoolSetting(this, "Targets", PlayerUtil.TARGETS(this));
    private final MultipleBoolSetting elementsProperty = new MultipleBoolSetting(this, "Elements", Element());
    private final NumberSetting boxThicknessProperty = new NumberSetting(this, "Box Thickness", 0.5, 0.5, 10, 0.1,
            () -> elementsProperty.isSelected("Box"));
    private final EnumSetting<BoxMode> boxModeProperty = new EnumSetting<>(this, "Box Mode", () -> elementsProperty.isSelected("Box"), BoxMode.values());
    private final BooleanSetting oppositeCornersProperty = new BooleanSetting(this, "Opposite", false,
            () -> elementsProperty.isSelected("Box") && boxModeProperty.getValue() == BoxMode.HALF_CORNERS);
    private final EnumSetting<RenderUtil.ColorMode> boxColorModeProperty = new EnumSetting<>(this, "Color Mode",
            () -> elementsProperty.isSelected("Box"), RenderUtil.ColorMode.values());
    private final ColorSetting boxColorProperty = new ColorSetting(this, "Color", new Color(209, 50, 50), () -> boxColorModeProperty.getValue() == RenderUtil.ColorMode.CUSTOM);
    private final BooleanSetting boxFadeProperty = new BooleanSetting(this, "Box Fade", true, () -> elementsProperty.isSelected("Box"));

    public int getBoxColor(int index) {
        return boxColorProperty.getValue().getRGB();
    }

    @EventHandler
    public void a(RenderNametagEvent event) {
        if (event.getEntity() instanceof EntityPlayer && elementsProperty.isSelected("Nametags"))
            event.setCancelled(true);
    }

    @EventHandler
    public void b(Render2DEvent event) {
        Gui.drawRect(0, 0, 0, 0,0);
        final java.util.List<EntityLivingBase> livingEntities = WorldUtil.getLivingEntities(Predicates.and(entity -> PlayerUtil.isValid(entity, targetsProperty)));
        for (EntityLivingBase entity : livingEntities) {
            if (!RenderUtil.isInViewFrustrum(entity)) continue;
            final double diffX = entity.posX - entity.lastTickPosX;
            final double diffY = entity.posY - entity.lastTickPosY;
            final double diffZ = entity.posZ - entity.lastTickPosZ;
            final double deltaX = mc.thePlayer.posX - entity.posX;
            final double deltaY = mc.thePlayer.posY - entity.posY;
            final double deltaZ = mc.thePlayer.posZ - entity.posZ;
            final float partialTicks = event.getPartialTicks();
            final AxisAlignedBB interpolatedBB = new AxisAlignedBB(
                    entity.lastTickPosX - entity.width / 2 + diffX * partialTicks,
                    entity.lastTickPosY + diffY * partialTicks,
                    entity.lastTickPosZ - entity.width / 2 + diffZ * partialTicks,
                    entity.lastTickPosX + entity.width / 2 + diffX * partialTicks,
                    entity.lastTickPosY + entity.height + diffY * partialTicks,
                    entity.lastTickPosZ + entity.width / 2 + diffZ * partialTicks);
            final double[][] vectors = new double[8][2];
            final float[] coords = new float[4];
            convertTo2D(interpolatedBB, vectors, coords);
            float minX = coords[0], minY = coords[1], maxX = coords[2], maxY = coords[3];
            float opacity = 255;
            Color color = boxFadeProperty.getValue() ? RenderUtil.toColorRGB(getBoxColor(0), opacity) : new Color(getBoxColor(0));
            for (MultiBoolean element : elementsProperty.getValue()) {
                if (elementsProperty.isSelected(element.getName())) {
                    switch (element.getName()) {
                        case "Box": {
                            switch (boxModeProperty.getValue()) {
                                case BOX: {
                                    RenderUtil.pre3D();
                                    glLineWidth(boxThicknessProperty.getValue().floatValue() * 4f);
                                    glBegin(GL_LINE_LOOP);
                                    glColor4f(0, 0, 0, opacity / 255f);
                                    glVertex2f(minX, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(minX, maxY);
                                    glEnd();

                                    glLineWidth(boxThicknessProperty.getValue().floatValue());
                                    RenderUtil.color(color.getRGB());
                                    glBegin(GL_LINE_LOOP);
                                    glVertex2f(minX, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(minX, maxY);
                                    glEnd();
                                    RenderUtil.post3D();
                                    break;
                                }
                                case FILL: {
                                    RenderUtil.drawRect(minX, minY, maxX, maxY, color.getRGB());
                                    break;
                                }
                                case BLUR_FILL: {
                                    float finalMinX = minX;
                                    float finalMinY = minY;
                                    float finalMaxX = maxX;
                                    float finalMaxY = maxY;
                                    RenderUtil.drawRect(finalMinX, finalMinY, finalMaxX, finalMaxY, color.getRGB());
                                    break;
                                }
                                case HORIZ_SIDES: {
                                    RenderUtil.pre3D();
                                    float lineLength = (maxX - minX) / 3;
                                    glLineWidth(boxThicknessProperty.getValue().floatValue() * 4f);
                                    glColor4f(0, 0, 0, opacity / 255f);
                                    glBegin(GL_LINES);
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX + lineLength, minY);
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX + lineLength, maxY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX - lineLength, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX - lineLength, maxY);
                                    glEnd();

                                    glLineWidth(boxThicknessProperty.getValue().floatValue());
                                    glBegin(GL_LINES);
                                    RenderUtil.color(color.getRGB());
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX + lineLength, minY);
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX + lineLength, maxY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX - lineLength, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX - lineLength, maxY);
                                    glEnd();
                                    RenderUtil.post3D();
                                    break;
                                }
                                case VERT_SIDES: {
                                    RenderUtil.pre3D();
                                    float lineLength = (maxX - minX) / 3;
                                    glLineWidth(boxThicknessProperty.getValue().floatValue() * 4f);
                                    glColor4f(0, 0, 0, opacity / 255f);
                                    glBegin(GL_LINES);
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX, minY + lineLength);
                                    glVertex2f(minX, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, minY + lineLength);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX, maxY - lineLength);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY - lineLength);
                                    glEnd();
                                    glLineWidth(boxThicknessProperty.getValue().floatValue());
                                    RenderUtil.color(color.getRGB());
                                    glBegin(GL_LINES);
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX, minY + lineLength);
                                    glVertex2f(minX, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, minY + lineLength);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX, maxY - lineLength);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY - lineLength);
                                    glEnd();
                                    RenderUtil.post3D();
                                    break;
                                }
                                case CORNERS: {
                                    RenderUtil.pre3D();
                                    glLineWidth(boxThicknessProperty.getValue().floatValue() * 4f);
                                    glBegin(GL_LINES);
                                    glColor4f(0, 0, 0, opacity / 255f);
                                    float lineLength = (maxX - minX) / 3;
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX + lineLength, minY);
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX, minY + lineLength);

                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX - lineLength, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, minY + lineLength);

                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX + lineLength, maxY);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX, maxY - lineLength);

                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX - lineLength, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY - lineLength);
                                    glEnd();

                                    glLineWidth(boxThicknessProperty.getValue().floatValue());
                                    glBegin(GL_LINES);
                                    RenderUtil.color(color.getRGB());
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX + lineLength, minY);
                                    glVertex2f(minX, minY);
                                    glVertex2f(minX, minY + lineLength);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX - lineLength, minY);
                                    glVertex2f(maxX, minY);
                                    glVertex2f(maxX, minY + lineLength);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX + lineLength, maxY);
                                    glVertex2f(minX, maxY);
                                    glVertex2f(minX, maxY - lineLength);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX - lineLength, maxY);
                                    glVertex2f(maxX, maxY);
                                    glVertex2f(maxX, maxY - lineLength);
                                    glEnd();
                                    RenderUtil.post3D();
                                    break;
                                }
                                case HALF_CORNERS: {
                                    RenderUtil.pre3D();
                                    glLineWidth(boxThicknessProperty.getValue().floatValue() * 4f);
                                    glBegin(GL_LINES);
                                    glColor4f(0, 0, 0, opacity / 255f);
                                    float lineLength = (maxX - minX) / 3;
                                    if (oppositeCornersProperty.getValue()) {
                                        glVertex2f(maxX, minY);
                                        glVertex2f(maxX - lineLength, minY);
                                        glVertex2f(maxX, minY);
                                        glVertex2f(maxX, minY + lineLength);

                                        glVertex2f(minX, maxY);
                                        glVertex2f(minX + lineLength, maxY);
                                        glVertex2f(minX, maxY);
                                        glVertex2f(minX, maxY - lineLength);
                                    } else {
                                        glVertex2f(minX, minY);
                                        glVertex2f(minX + lineLength, minY);
                                        glVertex2f(minX, minY);
                                        glVertex2f(minX, minY + lineLength);

                                        glVertex2f(maxX, maxY);
                                        glVertex2f(maxX - lineLength, maxY);
                                        glVertex2f(maxX, maxY);
                                        glVertex2f(maxX, maxY - lineLength);
                                    }
                                    glEnd();
                                    glLineWidth(boxThicknessProperty.getValue().floatValue());
                                    glBegin(GL_LINES);
                                    RenderUtil.color(color.getRGB());
                                    if (oppositeCornersProperty.getValue()) {
                                        glVertex2f(maxX, minY);
                                        glVertex2f(maxX - lineLength, minY);
                                        glVertex2f(maxX, minY);
                                        glVertex2f(maxX, minY + lineLength);

                                        glVertex2f(minX, maxY);
                                        glVertex2f(minX + lineLength, maxY);
                                        glVertex2f(minX, maxY);
                                        glVertex2f(minX, maxY - lineLength);
                                    } else {
                                        glVertex2f(minX, minY);
                                        glVertex2f(minX + lineLength, minY);
                                        glVertex2f(minX, minY);
                                        glVertex2f(minX, minY + lineLength);

                                        glVertex2f(maxX, maxY);
                                        glVertex2f(maxX - lineLength, maxY);
                                        glVertex2f(maxX, maxY);
                                        glVertex2f(maxX, maxY - lineLength);
                                    }
                                    glEnd();
                                    RenderUtil.post3D();
                                    break;
                                }
                            }
                            break;
                        }
                        case "Nametags": {
                            GlStateManager.enableAlpha();
                            GlStateManager.enableBlend();
                            float scale = 0.6f;
                            float leftoverScale = 1 / scale;
                            minX *= leftoverScale;
                            minY *= leftoverScale;
                            maxX *= leftoverScale;
                            maxY *= leftoverScale;
                            glScalef(scale, scale, 1);
                            String name = "";

                            name = entity.getDisplayName().getFormattedText();


                            GlStateManager.bindTexture(0);
                            RenderUtil.drawRect(minX + (maxX - minX) / 2 - Fonts.INSTANCE.getSourceSansPro().getStringWidth(EnumChatFormatting.WHITE + name + EnumChatFormatting.GREEN + " | " + entity.getHealth() * 100 / 100) / 2f - 3, minY - Fonts.INSTANCE.getSourceSansPro().getHeight() - 5 - 3, minX + (maxX - minX) / 2 + Fonts.INSTANCE.getSourceSansPro().getStringWidth(EnumChatFormatting.WHITE + name + EnumChatFormatting.GREEN + " | " + entity.getHealth() * 100 / 100) / 2f + 3, minY - Fonts.INSTANCE.getSourceSansPro().getHeight() + 5, 0x900f0f0f);
                            Fonts.INSTANCE.getSourceSansPro().drawString(EnumChatFormatting.WHITE + name + EnumChatFormatting.GREEN + " | " + entity.getHealth() * 100 / 100, minX + (maxX - minX) / 2 - Fonts.INSTANCE.getSourceSansPro().getStringWidth(EnumChatFormatting.WHITE + name + EnumChatFormatting.GREEN + " | " + entity.getHealth() * 100 / 100) / 2f, minY - Fonts.INSTANCE.getSourceSansPro().getHeight() - 5, new Color(255, 255, 255, MathHelper.floor_float(opacity)).getRGB());
                            glScalef(leftoverScale, leftoverScale, 1);
                            minX *= scale;
                            minY *= scale;
                            maxX *= scale;
                            maxY *= scale;
                            GlStateManager.disableAlpha();
                            GlStateManager.disableBlend();
                            break;
                        }
                        case "Armor":
                            if(entity instanceof EntityPlayer) {
                                float amp = 1;
                                switch (mc.gameSettings.guiScale) {
                                    case 0:
                                        amp = 0.5F;
                                        break;
                                    case 1:
                                        amp = 2.0F;
                                        break;
                                    case 3:
                                        amp = 0.6666666666666667F;
                                }
                                double[] positions4 = getScaledMouseCoordinates(minX, minY);
                                double[] positionsEnd = getScaledMouseCoordinates(maxX, maxY);
                                double[] scaledPositions = new double[]{positions4[0] * 2, positions4[1] * 2, positionsEnd[0] * 2, positionsEnd[1] * 2};
                                int i = 0;
                                float posy = 0;

                                List<ItemStack> armorInventory = new ArrayList<>();

                                for (ItemStack stack : ((EntityPlayer)entity).inventory.armorInventory) {
                                    if (stack != null) {
                                        armorInventory.add(stack);
                                    }
                                }

                                Collections.reverse(armorInventory);
                                GlStateManager.disableAlpha();
                                GlStateManager.clear(256);
                                GlStateManager.enableBlend();
                                mc.getRenderItem().zLevel = -150.0F;
                                glScalef(0.5f * amp, 0.5f * amp, 0.5f * amp);
                                for (ItemStack item : armorInventory) {
                                    if (mc.theWorld != null) {
                                        RenderHelper.enableGUIStandardItemLighting();
                                    }
                                    mc.getRenderItem().renderItemAndEffectIntoGUI(item, (int)(scaledPositions[2] + 2), (int) (scaledPositions[1] + posy - mc.thePlayer.getDistanceToEntity(entity) * 0.053f));
                                    posy += Math.abs(scaledPositions[3] - scaledPositions[1]) / 4;
                                }
                                GlStateManager.disableDepth();
                                GlStateManager.disableLighting();
                                GlStateManager.enableDepth();
                                GlStateManager.scale(1 / (0.5f * amp), 1 / (0.5f * amp), 1 / (0.5f * amp));
                                mc.getRenderItem().zLevel = 0.0F;
                                GlStateManager.enableBlend();
                                GlStateManager.enableAlpha();
                                float armorPercentage = entity.getTotalArmorValue() / 20.0F;
                                float armorBarWidth = (float) ((maxY - minY) * armorPercentage);
//
                            }
                            break;
                        case "Hand": {
                            if (entity.getHeldItem() != null) {
                                float scale = 0.5f;
                                float leftoverScale = 1 / scale;
                                minX *= leftoverScale;
                                minY *= leftoverScale;
                                maxX *= leftoverScale;
                                maxY *= leftoverScale;
                                glScalef(scale, scale, 1);
                                String text = entity.getHeldItem().getDisplayName();
                                mc.fontRendererObj.drawStringWithShadow(text, minX + (maxX - minX) / 2 - mc.fontRendererObj.getStringWidth(text) / 2f, boxModeProperty.getValue() == BoxMode.BOX || boxModeProperty.getValue() == BoxMode.FILL ? maxY + mc.fontRendererObj.FONT_HEIGHT - 3 : maxY - mc.fontRendererObj.FONT_HEIGHT / 2f, new Color(255, 255, 255, MathHelper.floor_float(opacity)).getRGB());
                                glScalef(leftoverScale, leftoverScale, 1);
                                minX *= scale;
                                minY *= scale;
                                maxX *= scale;
                                maxY *= scale;
                            }
                            break;
                        }
                        case "Heath": {
                            minX -= 3;
                            maxX -= 3;
                            RenderUtil.pre3D();
                            glLineWidth(boxThicknessProperty.getValue().floatValue() * 4f);
                            glBegin(GL_LINES);
                            glColor4f(0, 0, 0, opacity / 255f);
                            glVertex2f(minX, minY);
                            glVertex2f(minX, maxY);
                            glEnd();
                            glLineWidth(boxThicknessProperty.getValue().floatValue());
                            glBegin(GL_LINES);
                            Color healthColor = Color.GREEN;
                            if (entity.getHealth() < entity.getMaxHealth() / 2) healthColor = Color.YELLOW;
                            if (entity.getHealth() < entity.getMaxHealth() / 3) healthColor = Color.ORANGE;
                            if (entity.getHealth() < entity.getMaxHealth() / 4) healthColor = Color.RED;
                            RenderUtil.color(healthColor, MathHelper.floor_float(opacity));
                            glVertex2f(minX, minY + (maxY - minY));
                            glVertex2f(minX, maxY - (maxY - minY) * (entity.getHealth() / entity.getMaxHealth()));
                            glEnd();
                            RenderUtil.post3D();
                            minX += 3;
                            maxX += 3;
                            break;
                        }
                    }
                }
            }
        }
    }

    public double[] getScaledMouseCoordinates(double mouseX, double mouseY) {
        double x = mouseX;
        double y = mouseY;
        switch (mc.gameSettings.guiScale) {
            case 0:
                x *= 2;
                y *= 2;
                break;
            case 1:
                x *= 0.5;
                y *= 0.5;
                break;
            case 3:
                x *= 1.4999999999999999998;
                y *= 1.4999999999999999998;
        }
        return new double[]{x, y};
    }

    private void convertTo2D(AxisAlignedBB interpolatedBB, double[][] vectors, float[] coords) {
        if (coords == null || vectors == null || interpolatedBB == null) return;
        double x = mc.getRenderManager().viewerPosX;
        double y = mc.getRenderManager().viewerPosY;
        double z = mc.getRenderManager().viewerPosZ;

        vectors[0] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.minY - y,
                interpolatedBB.minZ - z);
        vectors[1] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.minY - y,
                interpolatedBB.maxZ - z);
        vectors[2] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.maxY - y,
                interpolatedBB.minZ - z);
        vectors[3] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.minY - y,
                interpolatedBB.minZ - z);
        vectors[4] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.maxY - y,
                interpolatedBB.minZ - z);
        vectors[5] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.minY - y,
                interpolatedBB.maxZ - z);
        vectors[6] = RenderUtil.project2D(interpolatedBB.minX - x, interpolatedBB.maxY - y,
                interpolatedBB.maxZ - z);
        vectors[7] = RenderUtil.project2D(interpolatedBB.maxX - x, interpolatedBB.maxY - y,
                interpolatedBB.maxZ - z);

        float minW = (float) Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> pos[2])).orElse(new double[]{0.5})[2];
        float maxW = (float) Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> pos[2])).orElse(new double[]{0.5})[2];
        if (maxW > 1 || minW < 0) return;
        float minX = (float) Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> pos[0])).orElse(new double[]{0})[0];
        float maxX = (float) Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> pos[0])).orElse(new double[]{0})[0];
        final float top = (mc.displayHeight / (float) new ScaledResolution(mc).getScaleFactor());
        float minY = (float) (top - Arrays.stream(vectors).min(Comparator.comparingDouble(pos -> top - pos[1])).orElse(new double[]{0})[1]);
        float maxY = (float) (top - Arrays.stream(vectors).max(Comparator.comparingDouble(pos -> top - pos[1])).orElse(new double[]{0})[1]);
        coords[0] = minX;
        coords[1] = minY;
        coords[2] = maxX;
        coords[3] = maxY;
    }

    public ArrayList<MultiBoolean> Element() {
        return Lists.newArrayList(
                new MultiBoolean(this, "Box", true),
                new MultiBoolean(this, "Nametags", true),
                new MultiBoolean(this, "Health", true),
                new MultiBoolean(this, "Armor", true),
                new MultiBoolean(this, "Hand", true)
        );
    }

    @AllArgsConstructor
    public enum BoxMode implements INameable {
        BOX("Box"),
        FILL("Fill"),
        BLUR_FILL("Blur Fill"),
        HORIZ_SIDES("Horizontal Sides"),
        VERT_SIDES("Vertical Sides"),
        CORNERS("Corners"),
        HALF_CORNERS("Half Corners");

        private final String addonName;

        @Override
        public String getName() {return addonName;}
    }
}