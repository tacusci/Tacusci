package spi;

import app.GravatarPlugin;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class PluginLoader {

    public static ArrayList<Plugin> plugins = new ArrayList<>();

    private PluginLoader() { plugins.add(new GravatarPlugin()); }

    public static void loadPlugins() {
        for (Plugin plugin : plugins) { plugin.onLoad(); }
    }
}
