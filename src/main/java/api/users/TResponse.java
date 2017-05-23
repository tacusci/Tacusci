package api.users;

import spark.Request;
import spark.Response;

/**
 * Created by alewis on 23/05/2017.
 */
public class TResponse {

    private Request request = null;
    private Response response = null;

    public TResponse(Request request, Response response) { this.request = request; this.response = response; }

    public void redirect(String location) { response.redirect(location); }
}
