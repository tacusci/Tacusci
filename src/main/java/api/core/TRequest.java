package api.core;

import spark.Request;
import spark.Response;

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
}
