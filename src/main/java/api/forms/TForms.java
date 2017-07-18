package api.forms;

import api.core.TAPIClass;
import app.core.core.controllers.Web;
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

    public String getInputField(String identifier, String placeholder) {
        return j2htmlPartials.INSTANCE.inputField(identifier, placeholder).render();
    }

    public String getRequiredInputField(String identifier, String placeholder) {
        return j2htmlPartials.INSTANCE.requiredInputField(identifier, placeholder).render();
    }

    public String getRequiredInputField(String identifier, String placeholder, String defaultContent) {
        return j2htmlPartials.INSTANCE.requiredInputField(identifier, placeholder, defaultContent).render();
    }

    public String getUsernameInputField(String identifier, String placeholder) {
        return j2htmlPartials.INSTANCE.usernameInput(identifier, placeholder).render();
    }

    public String getFullNameInputField(String identifier, String placeholder) {
        return j2htmlPartials.INSTANCE.fullNameInput(identifier, placeholder).render();
    }

    public String getValidatedPasswordInputField(String identifier, String placeholder) {
        return j2htmlPartials.INSTANCE.validatedPasswordInput(identifier, placeholder).render();
    }

    public String getEmailInputField(String identifier, String placeholder) {
        return j2htmlPartials.INSTANCE.emailInput(identifier, placeholder).render();
    }

    public String mapFormToHash(String formName) {
        return Web.INSTANCE.mapFormToHash(request.session(), formName);
    }
}
