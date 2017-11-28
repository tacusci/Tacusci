package api.core;

import kotlin.Pair;
import spark.Request;
import spark.Response;
import utils.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tauraaamui on 20/06/2017.
 */
public class TConfig extends TAPIClass {

    public TConfig(Request request, Response response) { super(request, response); }

    public String getProperty(String propertyName) { return Config.props.getProperty(propertyName); }

    public List<Pair<String, String>> getAllDefaultPropertiesAndValues() { return Config.props.getDefaultPropertiesList(); }
}
