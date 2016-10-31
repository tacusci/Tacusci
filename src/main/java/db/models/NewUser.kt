package db.models

/**
 * Created by alewis on 28/10/2016.
 */
data class NewUser(val fullName: String, val username: String, val password: String, val email: String)

fun NewUser.isValid(): Boolean {
    if (!isUsernameValid()) { return false }
    if (!isFullnameValid()) { return false }
    if (!isPasswordValid()) { return false }
    if (!isEmailValid()) { return false }
    return true
}

fun NewUser.isUsernameValid(): Boolean { if (username.isBlank() || username.isEmpty()) { return false } else { return true }}
fun NewUser.isFullnameValid(): Boolean { if (fullName.isBlank() || fullName.isEmpty()) { return false } else { return true }}
fun NewUser.isPasswordValid(): Boolean { if (password.isBlank() || password.isEmpty()) { return false } else { return true }}
fun NewUser.isEmailValid(): Boolean { if (email.isBlank() || email.isEmpty()) { return false } else { return true }}