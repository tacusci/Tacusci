package api.includes;

import api.core.TAPIClass;
import app.core.handlers.IncludeHandler;
import app.core.pages.includes.IncludeController;
import database.models.Include;
import spark.Request;
import spark.Response;

import java.util.List;

public class TIncludes extends TAPIClass {

    public TIncludes(Request request, Response response) { super(request, response); }

    public List<Include> getAllIncludes() { return IncludeHandler.INSTANCE.getAllIncludes(); }
    public String getInclude(String includeTitle) { return IncludeController.INSTANCE.renderInclude(getIncludeByTitle(includeTitle), request, response); }
    public Include getIncludeByTitle(String includeTitle) { return IncludeHandler.INSTANCE.getIncludeByTitle(includeTitle); }
    public Include getIncludeById(Integer includeId) { return IncludeHandler.INSTANCE.getIncludeById(includeId); }
}
