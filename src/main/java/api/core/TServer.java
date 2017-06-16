package api.core;

import app.Application;
import spark.Request;
import spark.Response;

/**
 * Created by tauraamui on 15/06/2017.
 */
public class TServer extends TAPIClass {

    private Application instance = null;

    public TServer(Application instance, Request request, Response response) {
        super(request, response);
        this.instance = instance;
    }

    public void restart() {
        if (instance != null) { instance.restartServer(); }
    }
}
