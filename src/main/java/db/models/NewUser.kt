package db.models

/**
 * Created by alewis on 28/10/2016.
 */
data class NewUser(val fullName: String, val username: String, val password: String, val email: String)

fun NewUser.isValid(): Boolean {
    if (fullName.isBlank() || fullName.isEmpty()) return false
    if (username.isBlank() || username.isEmpty()) return false
    if (password.isBlank() || password.isEmpty()) return false
    if (email.isBlank() || email.isEmpty()) return false
    return true
}