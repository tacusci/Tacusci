package api.forms;

import api.core.TAPIClass;
import app.corecontrollers.Web;
import spark.Request;
import spark.Response;
import utils.j2htmlPartials;

/**
 * Created by alewis on 23/05/2017.
 */
public class TForms extends TAPIClass {

    public TForms(Request request, Response response) {
        super(request, response);
    }

    public String getLoginForm() {
        return j2htmlPartials.INSTANCE.pureFormAligned_Login(request.session(), "login_form","/login", "post").render();
    }

    public String getSignOutForm() {
        return j2htmlPartials.INSTANCE.pureMenuItemForm(request.session(), "sign_out_form", "/login", "post", "Logout").render();
    }

    public String mapFormToHash(String formName) {
        return Web.INSTANCE.mapFormToHash(request.session(), formName);
    }
}
