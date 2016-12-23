package db.models

/**
 * Created by alewis on 20/12/2016.
 */
data class User(val fullName: String, val username: String, val password: String, val email: String, val banned: Int)
fun User.isValid(): Boolean {
    if (!isUsernameValid()) { return false }
    if (!isFullnameValid()) { return false }
    if (!isPasswordValid()) { return false }
    if (!isEmailValid()) { return false }
    return true
}

fun User.isUsernameValid(): Boolean {
    return !(username.isBlank() || username.isEmpty())
}

fun User.isFullnameValid(): Boolean {
    return !(fullName.isBlank() || fullName.isEmpty())
}
fun User.isPasswordValid(): Boolean {
    return !(password.isBlank() || password.isEmpty())
}
fun User.isEmailValid(): Boolean {
    return !(email.isBlank() || email.isEmpty())
}

data class Group(val name: String)
fun Group.isValid(): Boolean {
    if (!isNameValid()) { return false }
    return true
}

private fun Group.isNameValid(): Boolean {
    return !(name.isBlank() || name.isEmpty())
}
