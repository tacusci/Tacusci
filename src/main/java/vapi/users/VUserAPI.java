package vapi.users;

import app.handlers.UserHandler;
import database.models.User;

import java.util.ArrayList;

/**
 * Created by alewis on 22/05/2017.
 */
public class VUserAPI {

    public String getRootAdminUsername() { return UserHandler.INSTANCE.getRootAdmin().getUsername(); }

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
