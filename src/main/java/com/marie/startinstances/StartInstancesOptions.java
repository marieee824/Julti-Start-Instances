package com.marie.startinstances;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.julti.Julti;
import xyz.duncanruns.julti.JultiOptions;
import xyz.duncanruns.julti.util.ExceptionUtil;
import xyz.duncanruns.julti.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StartInstancesOptions {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path SAVE_PATH = JultiOptions.getJultiDir().resolve("startinstances.json");

    private static StartInstancesOptions instance = null;

//    not using same field as benchmark plugin so you can have different amounts
//    for actual benchmark vs warming instances up
    public int resetGoal = 1000;

    public static StartInstancesOptions getInstance() {
        return instance;
    }

    public static void load() {
        if (!Files.exists(SAVE_PATH)) {
            instance = new StartInstancesOptions();
        } else {
            String s;
            try {
                s = FileUtil.readString(SAVE_PATH);
            } catch (IOException e) {
                instance = new StartInstancesOptions();
                return;
            }
            instance = GSON.fromJson(s, StartInstancesOptions.class);
        }
    }

    public static void save() {
        try {
            FileUtil.writeString(SAVE_PATH, GSON.toJson(instance));
        } catch (IOException e) {
            Julti.log(Level.ERROR, "Failed to save Start Instances Options: " + ExceptionUtil.toDetailedString(e));
        }
    }
}
