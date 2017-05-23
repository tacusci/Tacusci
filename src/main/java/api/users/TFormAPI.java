package api.users;

import spark.Request;
import utils.j2htmlPartials;

/**
 * Created by alewis on 23/05/2017.
 */
public class TFormAPI {

    private Request request = null;

    public TFormAPI(Request request) { this.request = request; }

    public String getLoginForm() {
        return j2htmlPartials.INSTANCE.pureFormAligned_Login(request.session(), "login_form","/login", "post").render();
    }
}
