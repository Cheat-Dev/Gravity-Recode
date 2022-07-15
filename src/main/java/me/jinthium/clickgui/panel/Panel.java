package me.jinthium.clickgui.panel;
import lombok.Getter;
import lombok.Setter;
import me.jinthium.clickgui.component.Component;
import me.jinthium.clickgui.component.SettingComponent;
import me.jinthium.clickgui.panel.implementations.MultiSelectPanel;
import me.jinthium.clickgui.theme.Theme;
import me.jinthium.clickgui.theme.implementations.MainTheme;
import team.gravityrecode.clientbase.impl.util.util.render.RenderUtil;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public abstract class Panel extends Component {

    protected boolean extended;
    protected final List<Component> components = new ArrayList<>();

    public Panel(float x, float y, float width, float height) {
        this(x, y, width, height, new MainTheme());
    }

    public Panel(float x, float y, float width, float height, boolean visible) {
        this(x, y, width, height, visible, new MainTheme());
    }

    public Panel(float x, float y, float width, float height, boolean visible, Theme theme) {
        super(x, y, width, height, visible, theme);
        init();
    }

    public Panel(float x, float y, float width, float height, Theme theme) {
        this(x, y, width, height, true, theme);
    }

    @Override
    public void reset() {
        for (Component component : components) {
            component.reset();
        }
    }

    @Override
    public boolean focused() {
        boolean focused = false;
        for (Component component : components) {
            if (component.isVisible() && component.focused())
                focused = true;
        }
        return focused || this.focused;
    }

    public void drawScreen(int mouseX, int mouseY) {
        if (isExtended()) {
            updatePositions();
            for (Component component : components) {
                if (component.isVisible()) {
                    component.drawScreen(mouseX, mouseY);
                }
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovered(x, y, width, height, mouseX, mouseY)) {
            if (mouseButton == 1) {
                extended = !extended;
                if (extended) reset();
                for (Component component : components) {
                    if (component instanceof SettingComponent) {
                        component.setVisible(extended && ((SettingComponent<?>) component).getSetting().getVisible().getAsBoolean());
                    } else component.setVisible(extended);
                }
            }
        } else if (RenderUtil.inBounds(x, y, x + width, y + totalHeight(), mouseX, mouseY)) {
            if (extended) {
                for (Component component : components) {
                    if (component.isVisible()) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (isExtended()) {
            for (Component component : components) {
                if (component.isVisible()) {
                    component.mouseReleased(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (isExtended()) {
            for (Component component : components) {
                if (component.isVisible()) {
                    component.keyTyped(typedChar, keyCode);
                }
            }
        }
    }

    public void updateComponents() {
        for (Component component : components) {
            if (component instanceof SettingComponent) {
                component.setVisible(((SettingComponent<?>) component).getSetting().getVisible().getAsBoolean());
            }
            
            if(component instanceof MultiSelectPanel){
                component.setVisible((((MultiSelectPanel) component).getSetting().getVisible().getAsBoolean()));
            }
        }
    }

    public void updatePositions() {
        float yOffset = height;
        for (Component component : components) {
            if (component.isVisible()) {
                component.setPosition(x, y + yOffset, component.width(), component.height());
                yOffset += component instanceof Panel ? ((Panel) component).isExtended() ? ((Panel) component).totalHeight() : component.height() : component.height();
            }
        }
    }

    public float totalHeight() {
        float height = this.height;
        for (Component component : components) {
            if (component.isVisible()) {
                height += component instanceof Panel ? ((Panel) component).isExtended() ? ((Panel) component).totalHeight() : component.height() : component.height();
            }
        }
        return height;
    }
}
