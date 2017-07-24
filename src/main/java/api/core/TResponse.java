package api.core;

import app.core.core.controllers.Web;
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

    public String fourOhFour() { return Web.INSTANCE.get404Page(request, response); }

    public String fiveHundered() { return Web.INSTANCE.get500Page(request, response); }

    public void redirect(String location) { ExtensionCollectionKt.managedRedirect(response, request, location); }

}
