package api.templates;

import api.core.TAPIClass;
import app.core.core.handlers.TemplateHandler;
import database.models.Template;
import spark.Request;
import spark.Response;

import java.util.List;

/**
 * Created by tauraaamui on 17/06/2017.
 */
public class TTemplates extends TAPIClass {

    public TTemplates(Request request, Response response) {
        super(request, response);
    }

    public List<Template> getAllTemplates() { return TemplateHandler.INSTANCE.getAllTemplates(); }
}
