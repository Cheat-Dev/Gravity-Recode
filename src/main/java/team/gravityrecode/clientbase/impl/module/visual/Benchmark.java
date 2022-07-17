package team.gravityrecode.clientbase.impl.module.visual;

import team.gravityrecode.clientbase.Client;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.render.BenchmarkEvent;
import team.gravityrecode.clientbase.impl.util.util.client.Logger;

@ModuleInfo(moduleName = "Benchmark", moduleCategory = Module.ModuleCategory.VISUAL)
public class Benchmark extends Module {

    @EventHandler
    public void on(BenchmarkEvent benchmarkEvent) {}

    @Override
    public void onEnable() {
        super.onEnable();
        long millis = System.currentTimeMillis();

        for(int i = 0; i < 1_000_000; i++){
            Client.INSTANCE.getPubSubEventBus().publish(new BenchmarkEvent());
        }

        Logger.printSysLog("Event Benchmark took " + (System.currentTimeMillis() - millis) + "ms");
        toggle();
    }
}
