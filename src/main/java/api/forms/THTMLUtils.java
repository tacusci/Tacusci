package api.forms;

import api.core.TAPIClass;
import spark.Request;
import spark.Response;
import utils.j2htmlPartials;

/**
 * Created by tauraamui on 29/05/2017.
 */
public class THTMLUtils extends TAPIClass {

    public THTMLUtils(Request request, Response response) {
        super(request, response);
    }

    public String getButtonStyledLink(String href, String buttonText) {
        return j2htmlPartials.INSTANCE.link("pure-button", href, buttonText).render();
    }

    public String formatForEditing(String content) {
        return content.replaceAll("&", "&amp;").replaceAll("<", "&lt;");
    }

    public String formatBackForSaving(String content) {
        return content.replaceAll("&amp;", "&").replaceAll("<", "&lt;");
    }
}
