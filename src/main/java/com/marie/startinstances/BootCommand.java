package com.marie.startinstances;

import xyz.duncanruns.julti.Julti;
import xyz.duncanruns.julti.benchmarkplugin.BenchmarkResetManager;
import xyz.duncanruns.julti.cancelrequester.CancelRequester;
import xyz.duncanruns.julti.command.Command;

public class BootCommand extends Command {
    @Override
    public String helpDescription() {
        return "boot - Launches all instances, makes one world on each of them to remove lag, " +
                "then runs a benchmark with the configurable amount of resets";
    }

    @Override
    public int getMinArgs() {
        return 0;
    }

    @Override
    public int getMaxArgs() {
        return 0;
    }

    @Override
    public String getName() {
        return "boot";
    }

    @Override
    public void run(String[] args, CancelRequester cancelRequester) {
        new StartInstancesPlugin().onMenuButtonPress();
    }
}
