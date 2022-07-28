package team.gravityrecode.clientbase.impl.util.render;

import lombok.AllArgsConstructor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.util.MinecraftUtil;
import team.gravityrecode.clientbase.impl.util.render.secondary.RenderUtils;
import me.jinthium.optimization.ApacheMath;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;

@AllArgsConstructor
public class RenderUtil implements MinecraftUtil {

    private static final Frustum frustrum = new Frustum();
    private static final FloatBuffer WND_POS_BUFFER = GLAllocation.createDirectFloatBuffer(4);
    private static final IntBuffer VIEWPORT_BUFFER = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer MODEL_MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer PROJECTION_MATRIX_BUFFER = GLAllocation.createDirectFloatBuffer(16);
    private static final IntBuffer SCISSOR_BUFFER = GLAllocation.createDirectIntBuffer(16);
    
    public static enum ColorMode{
        Sync,
        Custom
    }


    public static double fpsMultiplier() {
        return (Client.INSTANCE.getRenderDeltaTime() / 60.0) * 3;
    }

    public static void scissor(double x, double y, double width, double height) {
        final ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        glColor4f(red, green, blue, alpha);
    }

    public static void drawImage(ResourceLocation image, float x, float y, float width, float height, boolean ez) {
        glPushMatrix();
        glDisable(GL_DEPTH_TEST);
        //glEnable(GL_BLEND);
        glDepthMask(false);
        mc.getTextureManager().bindTexture(image);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GlStateManager.color(1, 1, 1, 1);
        glDepthMask(true);
        //glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glPopMatrix();
    }

    public static boolean worldToScreen(double[] in, double[] out, double scaling) {
        glGetFloat(GL_MODELVIEW_MATRIX, MODEL_MATRIX_BUFFER);
        glGetFloat(GL_PROJECTION_MATRIX, PROJECTION_MATRIX_BUFFER);
        glGetInteger(GL_VIEWPORT, VIEWPORT_BUFFER);

        if (GLU.gluProject((float) in[0], (float) in[1], (float) in[2],
                MODEL_MATRIX_BUFFER, PROJECTION_MATRIX_BUFFER,
                VIEWPORT_BUFFER, WND_POS_BUFFER)) {
            final float zCoordinate = WND_POS_BUFFER.get(2);
            // Check z coordinate is within bounds 0-<1.0
            if (zCoordinate < 0.0F || zCoordinate > 1.0F) return false;

            out[0] = WND_POS_BUFFER.get(0) / scaling; // window pos (x) / scaled resolution scale (normal = 2)
            // GL handles the 'y' window coordinate inverted to Minecraft
            // subtract window pos y from bottom of screen and divide by scaled res scale
            out[1] = (Display.getHeight() - WND_POS_BUFFER.get(1)) / scaling;
            return true;
        }

        return false;
    }

    public static double[] worldToScreen(final double[] positionVector,
                                         final AxisAlignedBB boundingBox,
                                         final double[] projection,
                                         final double[] projectionBuffer) {
        final double[][] bounds = {
                {boundingBox.minX, boundingBox.minY, boundingBox.minZ},
                {boundingBox.minX, boundingBox.maxY, boundingBox.minZ},
                {boundingBox.minX, boundingBox.maxY, boundingBox.maxZ},
                {boundingBox.minX, boundingBox.minY, boundingBox.maxZ},
                {boundingBox.maxX, boundingBox.minY, boundingBox.minZ},
                {boundingBox.maxX, boundingBox.maxY, boundingBox.minZ},
                {boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ},
                {boundingBox.maxX, boundingBox.minY, boundingBox.maxZ}
        };

        final double[] position;

        // null when chests (don't need pos vector proj. for chests)
        if (positionVector != null) {
            if (!worldToScreen(positionVector, projectionBuffer, projection[2]))
                return null;

            position = new double[]{
                    projection[0], projection[1], // screen max width/height
                    -1.f, -1.f, // negative placeholder values for > comparison
                    projectionBuffer[0], projectionBuffer[1] // player position vector x/y
            };
        } else {
            position = new double[]{
                    projection[0], projection[1], // screen max width/height
                    -1.f, -1.f, // negative placeholder values for > comparison
            };
        }

        for (final double[] vector : bounds) {
            if (worldToScreen(vector, projectionBuffer, projection[2])) {
                final double projected_x = projectionBuffer[0];
                final double projected_y = projectionBuffer[1];

                position[0] = Math.min(position[0], projected_x);
                position[1] = Math.min(position[1], projected_y);
                position[2] = Math.max(position[2], projected_x);
                position[3] = Math.max(position[3], projected_y);
            }
        }

        return position;
    }

    public static void drawImage(boolean t, ResourceLocation image, float x, float y, float width, float height) {
        glPushMatrix();
        glDisable(GL_DEPTH_TEST);
        //glEnable(GL_BLEND);
        glDepthMask(false);

        mc.getTextureManager().bindTexture(image);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GlStateManager.color(1, 1, 1, 1);
        glDepthMask(true);
        //glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glPopMatrix();
    }

    public static int removeAlphaComponent(final int color) {
        final int red = color >> 16 & 0xFF;
        final int green = color >> 8 & 0xFF;
        final int blue = color & 0xFF;

        return ((red & 0xFF) << 16) |
                ((green & 0xFF) << 8) |
                (blue & 0xFF);
    }

    public static void glDrawGradientLine(final double x,
                                          final double y,
                                          final double x1,
                                          final double y1,
                                          final float lineWidth,
                                          final int color) {
        // Enable blending (required for anti-aliasing)
        final boolean restore = glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Set line width
        glLineWidth(lineWidth);
        // Enable line anti-aliasing
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        glShadeModel(GL_SMOOTH);

        final int noAlpha = removeAlphaComponent(color);

        glDisable(GL_ALPHA_TEST);

        // Begin line
        glBegin(GL_LINE_STRIP);
        {
            // Start
            color(noAlpha);
            glVertex2d(x, y);
            // Middle
            final double dif = x1 - x;

            color(color);
            glVertex2d(x + dif * 0.4, y);

            glVertex2d(x + dif * 0.6, y);
            // End
            color(noAlpha);
            glVertex2d(x1, y1);
        }
        // Draw the line
        glEnd();

        glEnable(GL_ALPHA_TEST);

        glShadeModel(GL_FLAT);

        // Restore blend
        glRestoreBlend(restore);
        // Disable line anti-aliasing
        glDisable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawCGuiCircle(double x, double y, float radius, int color) {
        color(color);
        RenderUtils.setup2DRendering(() -> {
            glEnable(GL_POINT_SMOOTH);
            glHint(GL_POINT_SMOOTH_HINT, GL_NICEST);
            glPointSize(radius * (2 * mc.gameSettings.guiScale));
            RenderUtils.render(GL_POINTS, () -> glVertex2d(x, y));
        });
    }

    public static void draw3DLine(double x, double y, double z, double x1, double y1, double z1, final float red, final float green,
                                  final float blue, final float alpha, final float lineWdith) {

        x = x - RenderManager.renderPosX;
        x1 = x1 - RenderManager.renderPosX;
        y = y - RenderManager.renderPosY;
        y1 = y1 - RenderManager.renderPosY;
        z = z - RenderManager.renderPosZ;
        z1 = z1 - RenderManager.renderPosZ;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(lineWdith);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(2);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void pre3D() {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glDisable(GL_LIGHTING);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
    }

    public static void post3D() {
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_FLAT);
        glDisable(GL_BLEND);
        glPopMatrix();
        glColor4f(1, 1, 1, 1);
    }

    public static double[] project2D(final double x, final double y, final double z) {
        FloatBuffer objectPosition = ActiveRenderInfo.objectCoords();
        ScaledResolution sc = new ScaledResolution(mc);
        if (GLU.gluProject((float)x, (float)y, (float)z, ActiveRenderInfo.modelview(), ActiveRenderInfo.projection(), ActiveRenderInfo.viewport(), objectPosition))
            return new double[]{ objectPosition.get(0) / sc.getScaleFactor(), objectPosition.get(1) / sc.getScaleFactor(),
                    objectPosition.get(2) };
        return null;
    }
    
    public static void bindTexture(int textureID){
        glBindTexture(GL_TEXTURE_2D, textureID);
    }
    
    public static boolean isHovered(float x, float y, float w, float h, int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= (x + w) && mouseY >= y && mouseY <= (y + h));
    }

    public static boolean isInViewFrustrum(final Entity entity) {
        frustrum.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        return frustrum.isBoundingBoxInFrustum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    private boolean isInViewFrustrum(final AxisAlignedBB bb) {
        final Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static void drawFilledCircleNoGL(final int x, final int y, final double r, final int c, final int quality) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;

        glColor4f(f1, f2, f3, f);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = ApacheMath.sin(((i * quality * ApacheMath.PI) / 180)) * r;
            final double y2 = ApacheMath.cos(((i * quality * ApacheMath.PI) / 180)) * r;
            glVertex2d(x + x2, y + y2);
        }

        glEnd();
    }

    public static void makeCropBox(float left, float top, float right, float bottom) {
        glPushMatrix();
        glEnable(GL_SCISSOR_TEST);
        cropBox(left, top, right, bottom);
    }

    public static void scaleStart(float x, float y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.translate(-x, -y, 0);
    }
    
    public static void scaleEnd() {
        GlStateManager.popMatrix();
    }

    public static Framebuffer createFramebuffer(Framebuffer framebuffer, boolean depth) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }
    
    public static AxisAlignedBB interpolate(final Entity entity,
                                            final AxisAlignedBB boundingBox,
                                            final float partialTicks) {
        final float invertedPT = 1.0f - partialTicks;
        return boundingBox.offset(
                (entity.posX - entity.prevPosX) * -invertedPT,
                (entity.posY - entity.prevPosY) * -invertedPT,
                (entity.posZ - entity.prevPosZ) * -invertedPT
        );
    }

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static int applyOpacity(int color, float opacity) {
        Color old = new Color(color);
        return applyOpacity(old, opacity).getRGB();
    }

    //Opacity value ranges from 0-1
    public static Color applyOpacity(Color color, float opacity) {
        opacity = ApacheMath.min(1, ApacheMath.max(0, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * opacity));
    }

    public static void glRestoreBlend(final boolean wasEnabled) {
        if (!wasEnabled) {
            glDisable(GL_BLEND);
        }
    }

    public static void drawImage(ResourceLocation image, float x, float y, float width, float height) {
        drawImage(image, x, y, width, height, 255);
    }

    public static void drawImage(ResourceLocation image, float x, float y, float width, float height, float opacity) {
        glPushMatrix();
        glDisable(GL_DEPTH_TEST);
        //glEnable(GL_BLEND);
        glDepthMask(false);
        glColor4f(1, 1, 1, opacity / 255);
        mc.getTextureManager().bindTexture(image);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GlStateManager.color(1, 1, 1, 1);
        glDepthMask(true);
        //glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glPopMatrix();
    }
    
    public static void glDrawBoundingBox(final AxisAlignedBB bb,
                                         final float lineWidth,
                                         final boolean filled) {
        if (filled) {
            // 4 sides
            glBegin(GL_QUAD_STRIP);
            {
                glVertex3d(bb.minX, bb.minY, bb.minZ);
                glVertex3d(bb.minX, bb.maxY, bb.minZ);

                glVertex3d(bb.maxX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.minZ);

                glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

                glVertex3d(bb.minX, bb.minY, bb.maxZ);
                glVertex3d(bb.minX, bb.maxY, bb.maxZ);

                glVertex3d(bb.minX, bb.minY, bb.minZ);
                glVertex3d(bb.minX, bb.maxY, bb.minZ);
            }
            glEnd();

            // Bottom
            glBegin(GL_QUADS);
            {
                glVertex3d(bb.minX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                glVertex3d(bb.minX, bb.minY, bb.maxZ);
            }
            glEnd();

            glCullFace(GL_FRONT);

            // Top
            glBegin(GL_QUADS);
            {
                glVertex3d(bb.minX, bb.maxY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
                glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            }
            glEnd();

            glCullFace(GL_BACK);
        }


        if (lineWidth > 0) {
            glLineWidth(lineWidth);

            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

            glBegin(GL_LINE_STRIP);
            {
                // Bottom
                glVertex3d(bb.minX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                glVertex3d(bb.minX, bb.minY, bb.maxZ);
                glVertex3d(bb.minX, bb.minY, bb.minZ);

                // Top
                glVertex3d(bb.minX, bb.maxY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
                glVertex3d(bb.minX, bb.maxY, bb.maxZ);
                glVertex3d(bb.minX, bb.maxY, bb.minZ);
            }
            glEnd();

            glBegin(GL_LINES);
            {
                glVertex3d(bb.maxX, bb.minY, bb.minZ);
                glVertex3d(bb.maxX, bb.maxY, bb.minZ);

                glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

                glVertex3d(bb.minX, bb.minY, bb.maxZ);
                glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            }
            glEnd();

            glDisable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        }
    }

    public static boolean glEnableBlend() {
        final boolean wasEnabled = glIsEnabled(GL_BLEND);

        if (!wasEnabled) {
            glEnable(GL_BLEND);
            glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        }

        return wasEnabled;
    }

    public static void cropBox(float x, float y, float width, float height) {
        final ScaledResolution scale = new ScaledResolution(mc);
        int factor = scale.getScaleFactor();
        
        glScissor((int) (x * factor), (int) ((scale.getScaledHeight() - height) * factor), (int) ((width - x) * factor), (int) ((height - y) * factor));
    }
    
    public static void destroyCropBox() {
        glDisable(GL_SCISSOR_TEST);
        glPopMatrix();
    }
    
    public static float animate(float target, float current, float speed) {
        boolean larger = (target > current);
        if (speed < 0.0f) speed = 0.0f;
        else if (speed > 1.0f) speed = 1.0f;
        float dif = ApacheMath.abs(current - target);
        float factor = dif * speed;
        if (larger) current += factor;
        else current -= factor;
        return current;
    }
    
    public static boolean inBounds(float x, float y, float w, float h, int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= w && mouseY >= y && mouseY <= h);
    }

    public static void drawGradientRect(float x, float y, float width, float height, int firstColor, int secondColor, boolean perpendicular) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glPushMatrix();
        glShadeModel(GL_SMOOTH);
        glBegin(GL_QUADS);

        color(firstColor);
        glVertex2d(width, y);
        if(perpendicular)
            color(secondColor);
        glVertex2d(x, y);
        color(secondColor);
        glVertex2d(x, height);
        if(perpendicular)
            color(firstColor);
        glVertex2d(width, height);
        glEnd();
        glShadeModel(GL_FLAT);
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
        //glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
        color(-1);
    }

    public static Color toColorRGB(int rgb, float alpha) {
        float[] rgba = convertRGB(rgb);
        return new Color(rgba[0], rgba[1], rgba[2], alpha / 255f);
    }

    public static float[] convertRGB(int rgb) {
        float a = (rgb >> 24 & 0xFF) / 255.0f;
        float r = (rgb >> 16 & 0xFF) / 255.0f;
        float g = (rgb >> 8 & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;
        return new float[]{r, g, b, a};
    }

    public static void drawRect(double x, double y, double width, double height, int color) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glPushMatrix();
        color(color);
        glBegin(GL_QUADS);
        glVertex2d(width, y);
        glVertex2d(x, y);
        glVertex2d(x, height);
        glVertex2d(width, height);
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
        RenderUtil.color(-1);
    }

    public static void color(int color) {
        float[] rgba = convertRGB(color);
        glColor4f(rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    public static void color(Color color, float alpha) {
        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alpha / 255f);
    }
}
