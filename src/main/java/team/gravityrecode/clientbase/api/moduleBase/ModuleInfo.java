package team.gravityrecode.clientbase.api.moduleBase;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface ModuleInfo {
    public String moduleName() default "No name";
    public int moduleKeyBind() default 0;
    public String moduleDescription() default "hahahahhahahahahaha";
    public Module.ModuleCategory moduleCategory();

}
