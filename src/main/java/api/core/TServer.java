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

    public long getMemoryUsage() { if (instance != null) { return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(); }
        return -1;
    }

    public long getMemoryUsageKB() { if (instance != null) { return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(long)Math.pow(2, 10); }
        return -1;
    }

    public long getMemoryUsageMB() { if (instance != null) { return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(long)Math.pow(2, 20); }
        return -1;
    }

    public long getMemoryUsageGB() { if (instance != null) { return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(long)Math.pow(2, 30); }
        return -1;
    }

    public void restart() {
        if (instance != null) { instance.restartServer(); }
    }
}
