package db.models

/**
 * Created by alewis on 28/10/2016.
 */
data class NewUser(val fullName: String, val username: String, val password: String, val email: String, val banned: Int)

fun NewUser.isValid(): Boolean {
    if (!isUsernameValid()) { return false }
    if (!isFullnameValid()) { return false }
    if (!isPasswordValid()) { return false }
    if (!isEmailValid()) { return false }
    return true
}

fun NewUser.isUsernameValid(): Boolean {
    return !(username.isBlank() || username.isEmpty())
}
fun NewUser.isFullnameValid(): Boolean {
    return !(fullName.isBlank() || fullName.isEmpty())
}
fun NewUser.isPasswordValid(): Boolean {
    return !(password.isBlank() || password.isEmpty())
}
fun NewUser.isEmailValid(): Boolean {
    return !(email.isBlank() || email.isEmpty())
}