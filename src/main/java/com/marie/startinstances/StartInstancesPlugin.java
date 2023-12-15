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
    private static ArrayList<MinecraftInstance> instancesWaiting = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        JultiAppLaunch.launchWithDevPlugin(args, PluginManager.JultiPluginData.fromString(
                Resources.toString(Resources.getResource(StartInstancesPlugin.class, "/julti.plugin.json"), Charset.defaultCharset())
        ), new StartInstancesPlugin());
    }

    @Override
    public void initialize() {
        CommandManager.getMainManager().registerCommand(new BootCommand());
        PluginEvents.RunnableEventType.ALL_INSTANCES_FOUND.register(StartInstancesPlugin::instancesFound);
        PluginEvents.InstanceEventType.STATE_CHANGE.register(instance -> {
            Julti.log(Level.DEBUG, "event " + instance.getNameSortingNum());
            try {
                stateChange(instance);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void instancesFound() {
        for (MinecraftInstance instance : InstanceManager.getInstanceManager().getInstances()) {
            instance.reset();
            instancesWaiting.add(instance);
        }
    }

    private void stateChange(MinecraftInstance instance) throws InterruptedException {
        if (!instancesWaiting.isEmpty()) {
            StateTracker tracker = instance.getStateTracker();
            Julti.log(Level.DEBUG, tracker.getInstanceState().toString());
            if (tracker.isCurrentState(InstanceState.INWORLD)) {
                Julti.log(Level.DEBUG, "if");
                Julti.getJulti().activateInstance(instance);
                Thread.sleep(10000);
                instance.ensureNotFullscreen();
                instance.reset();
                Julti.getJulti().focusWall();
                instancesWaiting.remove(instance);
                Julti.log(Level.DEBUG, String.valueOf(instancesWaiting.size()));
            }
        if (instancesWaiting.isEmpty()) {
            BenchmarkResetManager.getBenchmarkResetManager().startBenchmark();
        }
        }
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
