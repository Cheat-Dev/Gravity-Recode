package me.jinthium.clickgui.component;


import lombok.Getter;
import lombok.Setter;
import me.jinthium.clickgui.theme.Theme;
import me.jinthium.clickgui.theme.implementations.NewTheme;
import me.jinthium.clickgui.theme.implementations.OldTheme;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.impl.module.visual.ClickGui;
import team.gravityrecode.clientbase.impl.util.render.RenderUtil;

@Getter
@Setter
public abstract class Component {

    protected Theme theme;
    protected float x, y, origX, origY, origWidth, origHeight, width, height;
    protected boolean visible, focused;
    protected float offset;

    public Component(float x, float y, float width, float height) {
        this(x, y, width, height, true);
    }

    public Component(float x, float y, float width, float height, boolean visible) {
        this(x, y, width, height, visible, Client.INSTANCE.getPropertyManager().get(Client.INSTANCE.getModuleManager().getModule(ClickGui.class), "Mode")
                .getValue().equals(ClickGui.ClickGuiMode.NEW) ? new NewTheme() : new OldTheme());
    }

    public Component(float x, float y, float width, float height, Theme theme) {
        this(x, y, width, height, true, theme);
    }

    public Component(float x, float y, float width, float height, boolean visible, Theme theme) {
        this.x = x;
        this.y = y;
        this.origX = x;
        this.origY = y;
        this.width = width;
        this.height = height;
        this.origWidth = width;
        this.origHeight = height;
        this.visible = visible;
        this.theme = theme;
    }

    public void setPosition(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void init() {
    }

    public void reset() {
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return RenderUtil.isHovered(x, y, width, height, mouseX, mouseY);
    }

    public abstract void drawScreen(int mouseX, int mouseY);

    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);

    public abstract void mouseReleased(int mouseX, int mouseY, int mouseButton);

    public abstract void keyTyped(char typedChar, int keyCode);

    public Theme theme() {
        return theme;
    }

    public void theme(Theme theme) {
        this.theme = theme;
    }

    public float origX() {
        return origX;
    }

    public void origX(float origX) {
        this.origX = origX;
    }

    public float origY() {
        return origY;
    }

    public void origY(float origY) {
        this.origY = origY;
    }

    public float x() {
        return x;
    }

    public void x(float x) {
        this.x = x;
    }

    public float y() {
        return y;
    }

    public void y(float y) {
        this.y = y;
    }

    public float width() {
        return width;
    }

    public void width(float width) {
        this.width = width;
    }

    public float height() {
        return height;
    }

    public void height(float height) {
        this.height = height;
    }

    public boolean focused() {
        return focused;
    }

    public void focused(boolean focused) {
        this.focused = focused;
    }

    public float origWidth() {
        return origWidth;
    }

    public void origWidth(float origWidth) {
        this.origWidth = origWidth;
    }

    public float origHeight() {
        return origHeight;
    }

    public void origHeight(float origHeight) {
        this.origHeight = origHeight;
    }
}
