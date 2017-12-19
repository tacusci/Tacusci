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

    private Include getIncludeByTitle(String includeTitle) { return IncludeHandler.INSTANCE.getIncludeByTitle(includeTitle); }

    public List<Include> getAllIncludes() { return IncludeHandler.INSTANCE.getAllIncludes(); }
    public String renderInclude(String includeTitle) { return IncludeController.INSTANCE.renderInclude(getIncludeByTitle(includeTitle), request, response); }
    public Include getInclude(String includeTitle) { return IncludeHandler.INSTANCE.getIncludeByTitle(includeTitle); }
    public String renderIncludeById(Integer includeId) { return IncludeController.INSTANCE.renderInclude(getIncludeById(includeId), request, response); }
    public Include getIncludeById(Integer includeId) { return IncludeHandler.INSTANCE.getIncludeById(includeId); }
}
