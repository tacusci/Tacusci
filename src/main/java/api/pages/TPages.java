package api.pages;

import api.core.TAPIClass;
import app.core.handlers.PageHandler;
import database.models.Page;
import spark.Request;
import spark.Response;

import java.util.List;

/**
 * Created by tauraamui on 26/05/2017.
 */
public class TPages extends TAPIClass {

    public TPages(Request request, Response response) {
        super(request, response);
    }

    public List<Page> getAllPages() { return PageHandler.INSTANCE.getAllPages(); }

    public List<Page> getAllPages(String orderByClause) { return PageHandler.INSTANCE.getAllPages(orderByClause); }
}
