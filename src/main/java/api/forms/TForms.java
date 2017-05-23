package api.forms;

import spark.Request;
import utils.j2htmlPartials;

/**
 * Created by alewis on 23/05/2017.
 */
public class TForms {

    private Request request = null;

    public TForms(Request request) { this.request = request; }

    public String getLoginForm() {
        return j2htmlPartials.INSTANCE.pureFormAligned_Login(request.session(), "login_form","/login", "post").render();
    }

    public String getSignOutForm() {
        return j2htmlPartials.INSTANCE.pureMenuItemForm(request.session(), "sign_out_form", "/login", "post", "Logout").render();
    }
}
