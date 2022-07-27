package me.jinthium.scripting;

import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import team.gravityrecode.clientbase.Client;

import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

@Getter
public enum ScriptManager {
    INSTANCE;

    public ArrayList<Script> scripts = new ArrayList<Script>();

    public File dir;
    public boolean fail = false;

    @SneakyThrows
    public void init() {
        dir = new File(Minecraft.getMinecraft().mcDataDir,"Gravity/Gravity Scripts");
        if (!dir.exists()) {
            fail = true;
            dir.mkdirs();
        }

        if (fail) {
            System.out.println("Scripts directory not found! Restart Client!");
            return;
        }
        reload();
    }

    @SneakyThrows
    public void reload() {

        if(!scripts.isEmpty()) {
            System.out.println("Reloading scripts...");
            Client.INSTANCE.getModuleManager().clearScripts();
            scripts.clear();
        }

        Client.INSTANCE.getModuleManager().clearScripts();

        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".js")) {
                String rawName = f.getName().replace(".js", "");

                try (final BufferedReader reader = Files.newBufferedReader(Paths.get(f.getAbsolutePath()))) {
                    final StringBuilder builder = new StringBuilder();

                    String nextLine;
                    while ((nextLine = reader.readLine()) != null) {
                        builder.append(nextLine).append('\n');
                    }

                    Script script = new Script(f, builder.toString());

                    addScript(script);
                    Client.INSTANCE.getModuleManager().addScript(script);
                } catch (final IOException ignored) {
                    ignored.printStackTrace();
                }
            }
        }
    }

    public void addScript(Script script) {
        scripts.add(script);
    }

}
