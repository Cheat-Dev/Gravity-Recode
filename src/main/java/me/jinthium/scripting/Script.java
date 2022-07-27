package me.jinthium.scripting;

import jdk.nashorn.api.scripting.JSObject;
import lombok.Getter;
import org.lwjgl.input.Keyboard;
import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.keyboard.KeyboardPressEvent;
import team.gravityrecode.clientbase.impl.event.networking.PacketEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerMotionEvent;
import team.gravityrecode.clientbase.impl.event.player.PlayerMoveEvent;
import team.gravityrecode.clientbase.impl.event.render.Render2DEvent;
import team.gravityrecode.clientbase.impl.event.render.Render3DEvent;
import team.gravityrecode.clientbase.impl.util.client.Logger;
import team.gravityrecode.clientbase.impl.util.math.MathUtil;
import team.gravityrecode.clientbase.impl.util.network.PacketUtil;
import team.gravityrecode.clientbase.impl.util.player.MovementUtil;
import team.gravityrecode.clientbase.impl.util.render.ColorUtil;
import team.gravityrecode.clientbase.impl.util.render.RenderUtil;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.util.HashMap;
import java.util.function.Function;

@Getter
@ModuleInfo(moduleName = "Script", moduleCategory = Module.ModuleCategory.SCRIPT, isScript = true, moduleKeyBind = Keyboard.KEY_L)
public class Script extends Module {
    private final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private ScriptEngine scriptEngine;
    private Invocable invocable;

    private File file;
    private String scriptName, scriptVersion, scriptAuthor;
    private JSObject playerMotionEvent, render2DEvent, keyPressEvent, onEnable, onDisable;

    private HashMap<String, JSObject> events = new HashMap<>();

    public Script(File script, String src){
        Logger.printSysLog(src);
        this.setFunnyNumber(script.getName().replace(".js", ""));
        this.file = script;
        this.scriptEngine = scriptEngineManager.getEngineByName("nashorn");

        this.scriptEngine.put("script", this);
        this.scriptEngine.put("player", mc.thePlayer);
        this.scriptEngine.put("world", mc.theWorld);

        this.scriptEngine.put("moduleManager", Client.INSTANCE.getModuleManager());
        this.scriptEngine.put("propertyManager", Client.INSTANCE.getPropertyManager());

        this.scriptEngine.put("packetUtil", new PacketUtil());
        this.scriptEngine.put("renderUtil", new RenderUtil());
        this.scriptEngine.put("moveUtil", new MovementUtil());
        this.scriptEngine.put("color", new ColorUtil());
        this.scriptEngine.put("chat", new Logger());
        this.scriptEngine.put("mathUtil", new MathUtil());
        //gets info this way
        scriptEngine.put("initScript", new InitializeScript());

        try {
            scriptEngine.eval(src);
        }catch(ScriptException ex){
            ex.printStackTrace();
        }
        invocable = (Invocable) this.scriptEngine;
    }
    
    /*public void toggle() {
        if (toggled) {
            onDisable();
            toggled = false;
        } else {
            toggled = true;
            onEnable();
        }
    }*/

    @Override
    public void onEnable() {
        super.onEnable();
        //Artemis.INSTANCE.getEventBus().register(this);
        //invoke("onEnable");
        call("onEnable");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        //Artemis.INSTANCE.getEventBus().unregister(this);
        //invoke("onDisable");
        call("onDisable");
    }
    
    public void on(String eventName, JSObject event) {
        events.put(eventName, event);
    }

    //motion/update events

    @EventHandler
    public void onMotion(PlayerMotionEvent event) {
        //invoke("onMotion", event);
        call("onMotion", event);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        //invoke("onMove", event);
        call("onMove", event);
    }

    //render events

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        //invoke("onRender2D", event);
        call("onRender2D", event);
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        //invoke("onRender3D", event);
        call("onRender3D", event);
    }

    //user/player events

    @EventHandler
    public void onKey(KeyboardPressEvent event) {
        //invoke("onKey", event);
        call("onKey", event);
    }

    @EventHandler
    public void onReceivePacket(PacketEvent event) {
        //invoke("onReceivePacket", event);
        call("onPacket", event);
    }

    //public boolean isToggled() {
    //return toggled;
    //}

    //public void setToggled(boolean toggled) {
    //this.toggled = toggled;
    //}

    //note : these do not work, use call instead
    /*public void invoke(String method) {
        try {
            invocable.invokeFunction(method);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            //ignore
        }
    }

    public void invoke(String method, Object... args) {
        try {
            invocable.invokeFunction(method, args);
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            //ignore
        }
    }*/

    public void call(String method, Object event) {
        if(events.containsKey(method)) {
            events.get(method).call(method, event);
        }
    }

    public void call(String method) {
        if(events.containsKey(method)) {
            events.get(method).call(null);
        }
    }

    private class InitializeScript implements Function<JSObject, Script> {
        @Override
        public Script apply(JSObject jsObject) {
            scriptName = (String) jsObject.getMember("name");
            scriptVersion = (String) jsObject.getMember("version");
            scriptAuthor = (String) jsObject.getMember("author");
            return Script.this;
        }
    }
}
