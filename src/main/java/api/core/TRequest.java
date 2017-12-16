package api.core;

import spark.Request;
import spark.Response;
import spark.Session;

/**
 * Created by alewis on 23/05/2017.
 */
public class TRequest extends TAPIClass {

    public TRequest(Request request, Response response) {
        super(request, response);
    }

    public String getRequestURI() {
        return request.uri();
    }

    public String getRequestURIParamValue(String paramName) { return request.params(paramName); }

    public Session getSession() { return request.session(); }

    public Request getRequest() { return request; }
}
