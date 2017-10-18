package api.core;

import spark.Request;
import spark.Response;
import utils.Utils;

/**
 * Created by tauraamui on 26/06/2017.
 */
public class TUtils extends TAPIClass {

    public TUtils(Request request, Response response) {
        super(request, response);
    }

    public String getDateTimeNow() { return Utils.Companion.getDateTimeNow(); }

    public String getDateTimeNow(String format) { return Utils.Companion.getDateTimeNow(format); }

    public String getDateNow() { return Utils.Companion.getDateNow(); }

    public String getDateNow(String format) { return Utils.Companion.getDateNow(format); }

    public String convertMillisToDate(Long millies) {
        return Utils.Companion.convertMillisToDate(millies);
    }

    public String convertMillisToDate(Long millies, String format) { return Utils.Companion.convertMillisToDate(millies, format); }

    public String convertMillisToDateTime(Long millis) {
        return Utils.Companion.convertMillisToDateTime(millis);
    }

    public String convertMillisToDateTime(Long millis, String format) { return Utils.Companion.convertMillisToDateTime(millis, format); }

    public String convertStringToMD5Hash(String textToConvert) { return Utils.Companion.convertStringToMD5Hash(textToConvert); }
}
