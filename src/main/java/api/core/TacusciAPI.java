package api.core;

import api.forms.TForms;
import api.pages.TPages;
import api.users.TUser;
import app.corecontrollers.Web;
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

    private static void init(Request request, Response response) {
        apiObjInstances.add(new Pair<>("TUser", new TUser(request, response)));
        apiObjInstances.add(new Pair<>("TForms", new TForms(request, response)));
        apiObjInstances.add(new Pair<>("TResponse", new TResponse(request, response)));
        apiObjInstances.add(new Pair<>("TPages", new TPages(request, response)));
        apiObjInstances.add(new Pair<>("TDateTime", new TDateTime(request, response)));
        apiObjInstances.add(new Pair<>("THTMLUtils", new THTMLUtils(request, response)));
    }

    public static void injectAPIInstances(Request request, Response response, String templateTitle, VelocityIMTemplateEngine velocityIMTemplateEngine) {
        init(request, response);
        Page page = new Page();
        velocityIMTemplateEngine.insertIntoContext(templateTitle, Web.INSTANCE.loadNavBar(request, new HashMap<>()));
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
