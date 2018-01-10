package api.forms;

import api.core.TAPIClass;
import app.core.Web;
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

    public void registerContactUsForm(String formName, String hrefUri) {
        Web.INSTANCE.registerContactUsForm(request, response, formName, hrefUri);
    }

    public String getInputField(String identifier) {
        return j2htmlPartials.INSTANCE.inputField(identifier).render();
    }

    public String getInputField(String identifier, String classAttribute) {
        return j2htmlPartials.INSTANCE.inputField(identifier, classAttribute).render();
    }

    public String getInputField(String identifier, String classAttribute, String placeholder) {
        return j2htmlPartials.INSTANCE.inputField(identifier, classAttribute, placeholder).render();
    }

    public String getInputField(String identifier, String classAttribute, String placeholder, String defaultContent) {
        return j2htmlPartials.INSTANCE.inputField(identifier, classAttribute, placeholder, defaultContent).render();
    }

    public String getRequiredInputField(String identifier) {
        return j2htmlPartials.INSTANCE.requiredInputField(identifier).render();
    }

    public String getRequiredInputField(String identifier, String classAttribute) {
        return j2htmlPartials.INSTANCE.requiredInputField(identifier, classAttribute).render();
    }

    public String getRequiredInputField(String identifier, String classAttribute, String placeholder) {
        return j2htmlPartials.INSTANCE.requiredInputField(identifier, classAttribute, placeholder).render();
    }

    public String getRequiredInputField(String identifier, String classAttribute, String placeholder, String defaultContent) {
        return j2htmlPartials.INSTANCE.requiredInputField(identifier, classAttribute, placeholder, defaultContent).render();
    }

    public String getUsernameInputField(String identifier) {
        return j2htmlPartials.INSTANCE.usernameInput(identifier).render();
    }

    public String getUsernameInputField(String identifier, String classAttribute) {
        return j2htmlPartials.INSTANCE.usernameInput(identifier, classAttribute).render();
    }

    public String getUsernameInputField(String identifier, String classAttribute, String placeholder) {
        return j2htmlPartials.INSTANCE.usernameInput(identifier, classAttribute, placeholder).render();
    }

    public String getUsernameInputField(String identifier, String classAttribute, String placeholder, String defaultContent) {
        return j2htmlPartials.INSTANCE.usernameInput(identifier, classAttribute, placeholder, defaultContent).render();
    }

    public String getFullNameInputField(String identifier) {
        return j2htmlPartials.INSTANCE.fullNameInput(identifier).render();
    }

    public String getFullNameInputField(String identifier, String classAttribute) {
        return j2htmlPartials.INSTANCE.fullNameInput(identifier, classAttribute).render();
    }

    public String getFullNameInputField(String identifier, String classAttribute, String placeholder) {
        return j2htmlPartials.INSTANCE.fullNameInput(identifier, classAttribute, placeholder).render();
    }

    public String getFullNameInputField(String identifier, String classAttribute, String placeholder, String defaultContent) {
        return j2htmlPartials.INSTANCE.fullNameInput(identifier, classAttribute, placeholder, defaultContent).render();
    }

    public String getValidatedPasswordInputField(String identifier) {
        return j2htmlPartials.INSTANCE.validatedPasswordInput(identifier).render();
    }

    public String getValidatedPasswordInputField(String identifier, String classAttribute) {
        return j2htmlPartials.INSTANCE.validatedPasswordInput(identifier, classAttribute).render();
    }

    public String getValidatedPasswordInputField(String identifier, String classAttribute, String placeholder) {
        return j2htmlPartials.INSTANCE.validatedPasswordInput(identifier, classAttribute, placeholder).render();
    }

    public String getValidatedPasswordInputField(String identifier, String classAttribute, String placeholder, String defaultContent) {
        return j2htmlPartials.INSTANCE.validatedPasswordInput(identifier, classAttribute, placeholder, defaultContent).render();
    }

    public String getEmailInputField(String identifier) {
        return j2htmlPartials.INSTANCE.emailInput(identifier).render();
    }

    public String getEmailInputField(String identifier, String classAttribute) {
        return j2htmlPartials.INSTANCE.emailInput(identifier, classAttribute).render();
    }

    public String getEmailInputField(String identifier, String classAttribute, String placeholder) {
        return j2htmlPartials.INSTANCE.emailInput(identifier, classAttribute, placeholder).render();
    }

    public String getEmailInputField(String identifier, String classAttribute, String placeholder, String defaultContent) {
        return j2htmlPartials.INSTANCE.emailInput(identifier, classAttribute, placeholder, defaultContent).render();
    }

    public String getReadOnlyInputField(String identifier) {
        return j2htmlPartials.INSTANCE.readOnlyInputField(identifier).render();
    }

    public String getReadOnlyInputField(String identifier, String classAttribute) {
        return j2htmlPartials.INSTANCE.readOnlyInputField(identifier, classAttribute).render();
    }

    public String getReadOnlyInputField(String identifier, String classAttribute, String placeholder) {
        return j2htmlPartials.INSTANCE.readOnlyInputField(identifier, classAttribute, placeholder).render();
    }

    public String getReadOnlyInputField(String identifier, String classAttribute, String placeholder, String defaultContent) {
        return j2htmlPartials.INSTANCE.readOnlyInputField(identifier, classAttribute, placeholder, defaultContent).render();
    }

    public String mapFormToHash(String formName) {
        return Web.INSTANCE.mapFormToHash(request.session(), formName);
    }
}
