package api.sql;

import api.core.TAPIClass;
import app.core.handlers.SQLQueryHandler;
import database.models.SQLQuery;
import spark.Request;
import spark.Response;

public class TSQLQueries extends TAPIClass {

    public TSQLQueries(Request request, Response response) { super(request, response); }

    private SQLQuery getSQLQueryById(Integer sqlQueryId) { return SQLQueryHandler.INSTANCE.getSQLQueryById(sqlQueryId); }
    private SQLQuery getSQLQueryByName(String sqlQueryName) { return SQLQueryHandler.INSTANCE.getSQLQueryByName(sqlQueryName); }

}
