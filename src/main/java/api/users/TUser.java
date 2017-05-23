package api.users;

import app.handlers.UserHandler;
import database.models.User;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

/**
 * Created by alewis on 22/05/2017.
 */
public class TUser {

    public String getRootAdminUsername() { return UserHandler.INSTANCE.getRootAdmin().getUsername(); }
    private Request request = null;
    private Response response = null;

    public TUser(Request request, Response response) { this.request = request; this.response = response; }

    public boolean isLoggedIn() { return UserHandler.INSTANCE.isLoggedIn(request); }

    public User getLoggedInUser() { return UserHandler.INSTANCE.getUserDAO().getUser(UserHandler.INSTANCE.loggedInUsername(request)); }

    public String getLoggedInUsername() { return UserHandler.INSTANCE.loggedInUsername(request); }

    public ArrayList<String> getAllAdminUserUsernames() {
        ArrayList<String> adminUsernames = new ArrayList<>();
        for (User user : UserHandler.INSTANCE.getAdmins()) { adminUsernames.add(user.getUsername()); }
        return adminUsernames;
    }

    public ArrayList<String> getAllModeratorUserUsernames() {
        ArrayList<String> moderatorUsernames = new ArrayList<>();
        for (User user : UserHandler.INSTANCE.getModerators()) { moderatorUsernames.add(user.getUsername()); }
        return moderatorUsernames;
    }

    public ArrayList<String> getAllRegUserUsernames() {
        ArrayList<String> usernames = new ArrayList<>();
        for (User user : UserHandler.INSTANCE.getRegularUsers()) { usernames.add(user.getUsername()); }
        return usernames;
    }
}
