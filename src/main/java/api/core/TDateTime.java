package api.core;

import spark.Request;
import spark.Response;
import utils.Utils;

/**
 * Created by alewis on 28/05/2017.
 */
public class TDateTime extends TAPIClass {

    public TDateTime(Request request, Response response) {
        super(request, response);
    }

    public String getDateNow() { return Utils.Companion.getDateNow(); }
    public String getDateNow(String format) { return Utils.Companion.getDateNow(format); }
    public String getDateTimeNow() { return Utils.Companion.getDateTimeNow(); }
    public String getDateTimeNow(String format) { return Utils.Companion.getDateTimeNow(format); }

    public String convertMillisToDate(Long millis) { return Utils.Companion.convertMillisToDate(millis); }
    public String convertMillisToDate(Long millis, String format) { return Utils.Companion.convertMillisToDate(millis, format); }
    public String convertMillisToDateTime(Long millis) { return Utils.Companion.convertMillisToDateTime(millis); }
    public String convertMillisToDateTime(Long millis, String format) { return Utils.Companion.convertMillisToDateTime(millis, format); }
}
