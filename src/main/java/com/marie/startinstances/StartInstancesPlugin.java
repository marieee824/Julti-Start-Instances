package com.marie.startinstances;

import xyz.duncanruns.julti.command.CommandManager;
import xyz.duncanruns.julti.plugin.PluginInitializer;

public class StartInstancesPlugin implements PluginInitializer {

    public static void main(String[] args) {

    }

    @Override
    public void initialize() {
        StartInstancesOptions.load();
        CommandManager.getMainManager().registerCommand(new BootCommand());
    }

    @Override
    public void onMenuButtonPress() {

    }
}
