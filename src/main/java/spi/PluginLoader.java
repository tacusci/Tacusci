package spi;

import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class PluginLoader {

    public ArrayList<Plugin> plugins = new ArrayList<>();

    public void loadPlugins(Request request, Response response) {
        for (Plugin plugin : plugins) {}
    }
}
