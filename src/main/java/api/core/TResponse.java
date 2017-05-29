package api.core;

import extensions.ExtensionCollectionKt;
import spark.Request;
import spark.Response;

/**
 * Created by alewis on 23/05/2017.
 */
public class TResponse extends TAPIClass {

    public TResponse(Request request, Response response) {
        super(request, response);
    }

    public void redirect(String location) { ExtensionCollectionKt.managedRedirect(response, request, location); }
}
