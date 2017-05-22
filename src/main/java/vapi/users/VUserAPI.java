package vapi.users;

import app.handlers.UserHandler;
import database.models.User;

import java.util.ArrayList;

/**
 * Created by alewis on 22/05/2017.
 */
public class VUserAPI {

    public ArrayList<String> getAllRegUserUsernames() {
        ArrayList<String> usernames = new ArrayList<>();
        for (User user : UserHandler.INSTANCE.getRegularUsers()) { usernames.add(user.getUsername()); }
        return usernames;
    }
}
