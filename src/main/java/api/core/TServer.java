package api.core;

import app.Application;
import spark.Request;
import spark.Response;
import utils.Config;

import java.io.File;
import java.util.Date;

/**
 * Created by tauraamui on 15/06/2017.
 */
public class TServer extends TAPIClass {

    private Application instance = null;

    public TServer(Application instance, Request request, Response response) {
        super(request, response);
        this.instance = instance;
    }

    public long getFreeMemory() { return Runtime.getRuntime().freeMemory(); }

    public long getFreeMemoryKB() { return Runtime.getRuntime().freeMemory()/(long)Math.pow(2, 10); }

    public long getFreeMemoryMB() { return Runtime.getRuntime().freeMemory()/(long)Math.pow(2, 20); }

    public long getFreeMemoryGB() { return Runtime.getRuntime().freeMemory()/(long)Math.pow(2, 30); }

    public long getTotalMemory() { return Runtime.getRuntime().totalMemory(); }

    public long getTotalMemoryKB() { return Runtime.getRuntime().totalMemory()/(long)Math.pow(2, 10); }

    public long getTotalMemoryMB() { return Runtime.getRuntime().totalMemory()/(long)Math.pow(2, 20); }

    public long getTotalMemoryGB() { return Runtime.getRuntime().totalMemory()/(long)Math.pow(2, 30); }

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

    public File getStaticAssetFolder() {
        return new File(Config.props.getProperty("static-asset-folder"));
    }

    public void restart() {
        if (instance != null) { instance.restartServer(); }
    }
}
