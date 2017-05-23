package api.forms;

import app.basecontrollers.Web;
import spark.Request;
import spark.Response;
import utils.j2htmlPartials;

/**
 * Created by alewis on 23/05/2017.
 */
public class TForms {

    private Request request = null;
    private Response response = null;

    public TForms(Request request, Response response) { this.request = request; this.response = response; }

    public String getLoginForm() {
        return j2htmlPartials.INSTANCE.pureFormAligned_Login(request.session(), "login_form","/login", "post").render();
    }

    public String getSignOutForm() {
        return j2htmlPartials.INSTANCE.pureMenuItemForm(request.session(), "sign_out_form", "/login", "post", "Logout").render();
    }

    public String getFormHash(String formName) {
        return Web.INSTANCE.getFormHash(request.session(), formName);
    }
}
