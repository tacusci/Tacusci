package api.core;

import api.forms.TForms;
import api.users.TUser;
import app.corecontrollers.Web;
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
    }

    public static void injectAPIInstances(Request request, Response response, String templateTitle, VelocityIMTemplateEngine velocityIMTemplateEngine) {
        init(request, response);
        velocityIMTemplateEngine.insertIntoContext(templateTitle, Web.INSTANCE.loadNavBar(request, new HashMap<>()));
        velocityIMTemplateEngine.insertIntoContext(templateTitle, apiObjInstances);
    }

    public static HashMap<String, Object> injectAPIInstances(Request request, Response response, HashMap<String, Object> model) {
        init(request, response);
        for (Pair<String, Object> apiInstance : apiObjInstances) {
            model.put(apiInstance.getFirst(), apiInstance.getSecond());
        }

        model = Web.INSTANCE.loadNavBar(request, model);
        return model;
    }
}
