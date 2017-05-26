package api.pages;

import app.handlers.PageHandler;
import database.models.Page;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tauraamui on 26/05/2017.
 */
public class TPages {

    private Request request = null;
    private Response response = null;

    public TPages(Request request, Response response) { this.request = request; this.response = response; }

    public List<Page> getAllPages() {
        return PageHandler.INSTANCE.getAllPages();
    }
}
