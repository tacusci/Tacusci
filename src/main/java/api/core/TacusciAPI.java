package api.core;

import api.forms.TForms;
import api.groups.TGroups;
import api.pages.TPages;
import api.templates.TTemplates;
import api.users.TUsers;
import app.Application;
import app.core.core.controllers.Web;
import database.models.Page;
import kotlin.Pair;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityIMTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alewis on 22/05/2017.
 */
public class TacusciAPI {

    private static List<Pair<String, Object>> apiObjInstances = new ArrayList<>();
    private static Application instance = null;

    public static void setApplication(Application application) {
        instance = application;
    }

    private static void init(Request request, Response response) {
        apiObjInstances.add(new Pair<>("TUsers", new TUsers(request, response)));
        apiObjInstances.add(new Pair<>("TForms", new TForms(request, response)));
        apiObjInstances.add(new Pair<>("TPages", new TPages(request, response)));
        apiObjInstances.add(new Pair<>("TGroups", new TGroups(request, response)));
        apiObjInstances.add(new Pair<>("TConfig", new TConfig(request, response)));
        apiObjInstances.add(new Pair<>("TTemplates", new TTemplates(request, response)));
        apiObjInstances.add(new Pair<>("TResponse", new TResponse(request, response)));
        apiObjInstances.add(new Pair<>("TRequest", new TRequest(request, response)));
        apiObjInstances.add(new Pair<>("TDateTime", new TDateTime(request, response)));
        apiObjInstances.add(new Pair<>("THTMLUtils", new THTMLUtils(request, response)));
        apiObjInstances.add(new Pair<>("TServer", new TServer(instance, request, response)));
    }

    public static void injectAPIInstances(Request request, Response response, String templateTitle, VelocityIMTemplateEngine velocityIMTemplateEngine) {
        init(request, response);
        Page page = new Page();
        velocityIMTemplateEngine.insertIntoContext(templateTitle, Web.INSTANCE.loadNavigationElements(request, new HashMap<>()));
        velocityIMTemplateEngine.insertIntoContext(templateTitle, apiObjInstances);
    }

    public static HashMap<String, Object> injectAPIInstances(Request request, Response response, HashMap<String, Object> model) {
        init(request, response);
        for (Pair<String, Object> apiInstance : apiObjInstances) {
            model.put(apiInstance.getFirst(), apiInstance.getSecond());
        }
        return model;
    }
}
