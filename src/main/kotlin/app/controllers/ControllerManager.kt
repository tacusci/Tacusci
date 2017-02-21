package app.controllers

/**
 * Created by alewis on 21/02/2017.
 */

object ControllerManager {
    val routesAndControllers = mapOf<String, Controller>(Pair("/", IndexController()),
            Pair("/dashboard", DashboardController()),
            Pair("/register", RegisterController()),
            Pair("/dashboard/user_management", UserManagementController()),
            Pair("/dashboard/log_file", LogFileViewController()),
            Pair("/dashboard/page_management", PageManagementController()),
            Pair("/login", LoginController()),
            Pair("/profile", ProfileController()),
            Pair("/profile/:username", ProfileController()))
}
