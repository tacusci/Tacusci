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

    public List<SQLQuery> getSQLQueriesOfType(String sqlQueryTypeName) { return SQLQueryHandler.INSTANCE.getSQLQueryOfType(SQLQueryType.valueOf(sqlQueryTypeName)); }
    public SQLQuery getSQLQueryById(Integer sqlQueryId) { return SQLQueryHandler.INSTANCE.getSQLQueryById(sqlQueryId); }
    public SQLQuery getSQLQueryByName(String sqlQueryName) { return SQLQueryHandler.INSTANCE.getSQLQueryByName(sqlQueryName); }

}
