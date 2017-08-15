package spi;

import spark.Request;
import spark.Response;

public interface Plugin {

    Request request = null;
    Response response = null;

    Plugin initRequestResponse(Request request, Response response);
    String getTitle();
    void onLoad();
}
