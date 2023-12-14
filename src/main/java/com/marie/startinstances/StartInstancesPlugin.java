package com.marie.startinstances;

import com.google.common.io.Resources;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.julti.Julti;
import xyz.duncanruns.julti.JultiAppLaunch;
import xyz.duncanruns.julti.benchmarkplugin.BenchmarkCommand;
import xyz.duncanruns.julti.benchmarkplugin.BenchmarkPlugin;
import xyz.duncanruns.julti.benchmarkplugin.BenchmarkResetManager;
import xyz.duncanruns.julti.command.CommandManager;
import xyz.duncanruns.julti.instance.InstanceState;
import xyz.duncanruns.julti.instance.KeyPresser;
import xyz.duncanruns.julti.instance.MinecraftInstance;
import xyz.duncanruns.julti.instance.StateTracker;
import xyz.duncanruns.julti.management.InstanceManager;
import xyz.duncanruns.julti.plugin.PluginEvents;
import xyz.duncanruns.julti.plugin.PluginInitializer;
import xyz.duncanruns.julti.plugin.PluginManager;
import xyz.duncanruns.julti.util.SafeInstanceLauncher;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class StartInstancesPlugin implements PluginInitializer {

    public static void main(String[] args) throws IOException {
        JultiAppLaunch.launchWithDevPlugin(args, PluginManager.JultiPluginData.fromString(
                Resources.toString(Resources.getResource(StartInstancesPlugin.class, "/julti.plugin.json"), Charset.defaultCharset())
        ), new StartInstancesPlugin());
    }

    @Override
    public void initialize() {
        CommandManager.getMainManager().registerCommand(new BootCommand());
        PluginEvents.RunnableEventType.ALL_INSTANCES_FOUND.register(StartInstancesPlugin::instancesFound);
        PluginEvents.InstanceEventType.STATE_CHANGE.register(m -> stateChange());
    }

    private static void instancesFound() {
        for (MinecraftInstance instance : InstanceManager.getInstanceManager().getInstances()) {
            instance.reset();
        }
    }

    private static void stateChange() {
        ArrayList<MinecraftInstance> instancesWaiting = new ArrayList<>(InstanceManager.getInstanceManager().getInstances());
        while (instancesWaiting.size() > 0) {
            for (MinecraftInstance instance : InstanceManager.getInstanceManager().getInstances()) {
                Julti.log(Level.DEBUG, "hi");
                StateTracker tracker = instance.getStateTracker();
                if (tracker.isCurrentState(InstanceState.INWORLD)) {
                    Julti.log(Level.DEBUG, "if");
                    Julti.getJulti().activateInstance(instance);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    instance.reset();
                    Julti.getJulti().focusWall();
                    instancesWaiting.remove(instance);
                }
            }
        }
        BenchmarkResetManager.getBenchmarkResetManager().startBenchmark();
    }

    @Override
    public void onMenuButtonPress() {
        SafeInstanceLauncher.launchInstances(InstanceManager.getInstanceManager().getInstances());
    }

    @Override
    public String getMenuButtonName() {
        return "Boot Instances";
    }
}
