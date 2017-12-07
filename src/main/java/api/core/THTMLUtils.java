package api.core;

import spark.Request;
import spark.Response;
import utils.j2htmlPartials;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public String getButtonStyledLink(String href, String buttonText, String inlineCss) {
        return j2htmlPartials.INSTANCE.link("pure-button " + inlineCss, href, buttonText).render();
    }

    public String getButtonStyledLinkOpenNewTab(String href, String buttonText) {
        return j2htmlPartials.INSTANCE.link("pure-button", href, buttonText).render();
    }

    public String getButtonStyledLinkOpenNewTab(String href, String buttonText, String inlineCss) {
        return j2htmlPartials.INSTANCE.linkWithTarget("pure-button " + inlineCss, href, "_blank", buttonText).render();
    }

    public String formatForEditing(String content) {
        if (content != null)
            return content.replaceAll("&", "&amp;").replaceAll("<", "&lt;");
        else
            return "";
    }

    public String formatBackForSaving(String content) {
        return content.replaceAll("&amp;", "&").replaceAll("<", "&lt;");
    }
}
