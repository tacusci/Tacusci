package vapi.users;

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
public class VAPI {

    public static void injectAPIInstances(Request request, String templateTitle, VelocityIMTemplateEngine velocityIMTemplateEngine) {
        List<Pair<String, Object>> apiObjInstances = new ArrayList<>();

        apiObjInstances.add(new Pair<>("VUserAPI", new VUserAPI()));

        velocityIMTemplateEngine.insertIntoContext(templateTitle, Web.INSTANCE.loadNavBar(request, new HashMap<>()));
        velocityIMTemplateEngine.insertIntoContext(templateTitle, apiObjInstances);
    }
}
