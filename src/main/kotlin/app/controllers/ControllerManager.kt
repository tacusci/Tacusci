package app.controllers

import spark.Session

/**
 * Created by alewis on 21/02/2017.
 */

object ControllerManager {
    val profileController = ProfileController()
    val resetPasswordController = ResetPasswordController()
    val routesAndControllers = mapOf<String, Controller>(Pair("/", IndexController()),
            Pair("/dashboard", DashboardController()),
            Pair("/register", RegisterController()),
            Pair("/dashboard/user_management", UserManagementController()),
            Pair("/dashboard/log_file", LogFileViewController()),
            Pair("/dashboard/page_management", PageManagementController()),
            Pair("/login", LoginController()),
            Pair("/profile", profileController),
            Pair("/profile/:username", profileController),
            Pair("/reset_password/:username", resetPasswordController),
            Pair("/reset_password/:username/:authhash", resetPasswordController))

    fun initSessionAttributes(session: Session) = routesAndControllers.forEach { it.value.initSessionBoolAttributes(session) }
}
