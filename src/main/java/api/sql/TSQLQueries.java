package api.sql;

import api.core.TAPIClass;
import app.core.handlers.SQLQueryHandler;
import database.models.SQLQuery;
import database.models.SQLQueryType;
import spark.Request;
import spark.Response;

import java.util.List;

public class TSQLQueries extends TAPIClass {

    public TSQLQueries(Request request, Response response) { super(request, response); }

    private List<SQLQuery> getSQLQueriesOfType(String sqlQueryTypeName) { return SQLQueryHandler.INSTANCE.getSQLQueryOfType(SQLQueryType.valueOf(sqlQueryTypeName)); }
    private SQLQuery getSQLQueryById(Integer sqlQueryId) { return SQLQueryHandler.INSTANCE.getSQLQueryById(sqlQueryId); }
    private SQLQuery getSQLQueryByName(String sqlQueryName) { return SQLQueryHandler.INSTANCE.getSQLQueryByName(sqlQueryName); }

}
