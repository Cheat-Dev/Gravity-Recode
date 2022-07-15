package me.jinthium.clickgui.component;

import lombok.Getter;
import lombok.Setter;
import team.gravityrecode.clientbase.api.property.Property;

@Setter
@Getter
public abstract class SettingComponent<Type extends Property> extends Component {

    protected Type setting;

    public SettingComponent(Type setting, float x, float y, float width, float height) {
        this(setting, x, y, width, height, true);
    }

    public SettingComponent(Type setting, float x, float y, float width, float height, boolean visible) {
        super(x, y, width, height, visible);
        this.setting = setting;
    }
}
