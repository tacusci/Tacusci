package spi;

import app.GravatarPlugin;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class PluginLoader {

    public ArrayList<Plugin> plugins = new ArrayList<>();

    public void loadPlugins(Request request, Response response) {
        plugins.add(new GravatarPlugin());
        for (Plugin plugin : plugins) { plugin.onLoad(); }
    }
}
