package api.users;

import api.forms.TForm;
import app.basecontrollers.Web;
import kotlin.Pair;
import spark.Request;
import spark.template.velocity.VelocityIMTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alewis on 22/05/2017.
 */
public class TacusciAPI {

    private static List<Pair<String, Object>> apiObjInstances = new ArrayList<>();

    private static void init(Request request) {
        apiObjInstances.add(new Pair<>("TUser", new TUser(request)));
        apiObjInstances.add(new Pair<>("TForm", new TForm(request)));
    }

    public static void injectAPIInstances(Request request, String templateTitle, VelocityIMTemplateEngine velocityIMTemplateEngine) {
        init(request);
        velocityIMTemplateEngine.insertIntoContext(templateTitle, Web.INSTANCE.loadNavBar(request, new HashMap<>()));
        velocityIMTemplateEngine.insertIntoContext(templateTitle, apiObjInstances);
    }

    public static HashMap<String, Object> injectAPIInstances(Request request, HashMap<String, Object> model) {
        init(request);
        for (Pair<String, Object> apiInstance : apiObjInstances) {
            model.put(apiInstance.getFirst(), apiInstance.getSecond());
        }

        model = Web.INSTANCE.loadNavBar(request, model);
        return model;
    }
}
