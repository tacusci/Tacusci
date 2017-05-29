package api.core;

import spark.Request;
import spark.Response;

/**
 * Created by tauraamui on 29/05/2017.
 */
public class TAPIClass {

    protected Request request = null;
    protected Response response = null;

    public TAPIClass(Request request, Response response) {
        this.request = request;
        this.response = response;
    }
}
