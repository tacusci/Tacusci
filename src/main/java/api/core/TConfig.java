package api.core;

import spark.Request;
import spark.Response;
import utils.Config;

/**
 * Created by tauraaamui on 20/06/2017.
 */
public class TConfig extends TAPIClass {

    public TConfig(Request request, Response response) { super(request, response); }

    public String getProperty(String propertyName) {
        return Config.props.getProperty(propertyName);
    }
}
