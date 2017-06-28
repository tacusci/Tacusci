package api.users;

import api.core.TAPIClass;
import app.core.core.handlers.GroupHandler;
import app.core.handlers.UserHandler;
import database.daos.DAOManager;
import database.daos.GroupDAO;
import database.daos.User2GroupDAO;
import database.daos.UserDAO;
import database.models.Group;
import database.models.User;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alewis on 22/05/2017.
 */
public class TUsers extends TAPIClass {

    public TUsers(Request request, Response response) { super(request, response); }

    public String getRootAdminUsername() { return UserHandler.INSTANCE.getRootAdmin().getUsername(); }

    public boolean isLoggedIn() { return UserHandler.INSTANCE.isLoggedIn(request); }

    public User getUserByName(String username) { return UserHandler.INSTANCE.getUserDAO().getUser(username); }

    public User getLoggedInUser() { return UserHandler.INSTANCE.getUserDAO().getUser(UserHandler.INSTANCE.loggedInUsername(request)); }

    public boolean isBanned(User user) {
        return UserHandler.INSTANCE.isBanned(user.getUsername());
    }

    public String getLoggedInUsername() { return UserHandler.INSTANCE.loggedInUsername(request); }

    public List<User> getAdminUsers() { return UserHandler.INSTANCE.getAdmins(); }

    public List<User> getModeratorUsers() { return UserHandler.INSTANCE.getModerators(); }

    public List<User> getRegularUsers() { return UserHandler.INSTANCE.getRegularUsers(); }

    public List<User> getUsers() { return UserHandler.INSTANCE.getUsers(); }

    public User getRootAdmin() { return UserHandler.INSTANCE.getRootAdmin(); }

    public List<User> getUsersInGroup(String groupName) {
        ArrayList<User> usersInGroup = new ArrayList<>();
        for (User user : UserHandler.INSTANCE.getUsers()) {
            if (GroupHandler.INSTANCE.userInGroup(user, groupName))
                usersInGroup.add(user);
        }
        return usersInGroup;
    }

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
