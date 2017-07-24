package api.groups;

import api.core.TAPIClass;
import app.core.core.handlers.GroupHandler;
import database.models.Group;
import database.models.User;
import spark.Request;
import spark.Response;

import java.util.List;

/**
 * Created by tauraaamui on 19/06/2017.
 */
public class TGroups extends TAPIClass {

    //THE GROUP API SHOULD JUST BE READ ONLY, AT LEAST FOR NOW

    public TGroups(Request request, Response response) {
        super(request, response);
    }

    public boolean groupExists(String groupName) { return GroupHandler.INSTANCE.groupExists(groupName); }
    public boolean userInGroup(User user, String groupName) { return GroupHandler.INSTANCE.userInGroup(user, groupName); }
    public boolean userInGroup(String username, String groupName) { return GroupHandler.INSTANCE.userInGroup(username, groupName); }
    public List<User> getUsersInGroup(String groupName) { return GroupHandler.INSTANCE.getUsersInGroup(groupName); }
    public List<Group> getGroups() { return GroupHandler.INSTANCE.getGroupDAO().getGroups(); }
}