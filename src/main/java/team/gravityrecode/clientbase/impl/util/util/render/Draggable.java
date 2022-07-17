package team.gravityrecode.clientbase.impl.util.util.render;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.util.MinecraftUtil;
import team.gravityrecode.clientbase.impl.util.util.render.animations.Animation;
import team.gravityrecode.clientbase.impl.util.util.render.animations.Direction;
import team.gravityrecode.clientbase.impl.util.util.render.animations.impl.MainAnimations;

import java.awt.*;

@Getter
@Setter
public class Draggable implements MinecraftUtil {
    @Expose
    @SerializedName("x")
    private float x;

    @Expose
    @SerializedName("y")
    private float y;
    private float initialX, initialY, startX, startY, width, height;
    private boolean dragging;

    @Expose
    @SerializedName("name")
    private String name;

    public Animation hoverAnimation = new MainAnimations(250, 1, Direction.BACKWARDS);

    public Module module;

    public Draggable(Module module, String name, float initialX, float initialY, float width, float height) {
        this.module = module;
        this.name = name;
        this.x = initialX;
        this.y = initialY;
        this.initialX = initialX;
        this.initialY = initialY;
        this.width = width;
        this.height = height;
    }


    public final void onDraw(int mouseX, int mouseY) {
        boolean hovering = RenderUtil.isHovered(x, y, width, height, mouseX, mouseY);
        if(!this.module.isEnabled()) return;
        if (dragging) {
            x = (mouseX - startX);
            y = (mouseY - startY);
        }
        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!hoverAnimation.isDone() || hoverAnimation.finished(Direction.FORWARDS)) {
            RoundedUtil.drawRoundedOutline(x, y, x + width, y + height, 8, 3f, RenderUtil.applyOpacity(Color.WHITE, (float) hoverAnimation.getOutput()).getRGB());
//           RenderUtils.drawBorderedRoundedRect(x, y, x + width, y + height, 4, 0.5f,
//                    RenderUtil.applyOpacity(Color.WHITE, (float) hoverAnimation.getOutput()).getRGB(), new Color(0, 0, 0, 0).getRGB());
        }
    }

    public final void onClick(int mouseX, int mouseY, int button) {
        boolean canDrag = RenderUtil.isHovered(x, y, width, height, mouseX, mouseY);
        if(!this.module.isEnabled()) return;
        if (canDrag) {
            dragging = true;
            startX = (int) (mouseX - x);
            startY = (int) (mouseY - y);
        }
    }

    public final void onRelease(int button) {
        if(!this.module.isEnabled()) return;
        if (button == 0) dragging = false;
    }

}