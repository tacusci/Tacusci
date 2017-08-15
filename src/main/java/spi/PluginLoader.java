package spi;

import app.GravatarPlugin;

import java.util.ArrayList;

public class PluginLoader {

    public static ArrayList<Plugin> plugins = new ArrayList<>();

    static { plugins.add(new GravatarPlugin()); }

    public static void loadPlugins() {
        for (Plugin plugin : plugins) { plugin.onLoad(); }
    }
}
