package utils

/**
 * Created by alewis on 01/02/2017.
 */
object Validation {

    fun matchPasswordPattern(password: String) = Regex(passwordRegexStruct()).matches(password)
    fun passwordRegexStruct() = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}"
    fun getPasswordValidationMessage(): String = "Password must contain at least 8 characters including at least one number and one uppercase and lowercase letter"

    fun matchEmailPattern(email: String) = Regex(emailRegexStruct()).matches(email)
    fun emailRegexStruct() = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}$"
    fun getEmailValidationMessage(): String = "Must be a valid email address"

    fun matchUsernamePattern(username: String) = Regex(usernameRegexStruct()).matches(username)
    fun usernameRegexStruct() = "^[a-zA-Z0-9_]{2,20}$"
    fun getUsernameValidationMessage(): String = "Username must be between 2 and 20 characters and can only contain underscores"

    fun matchFullNamePattern(fullName: String) = Regex(fullNameRegexStruct()).matches(fullName)
    fun fullNameRegexStruct() = "^[ \\da-zA-Z,.'-]{2,30}$"
    fun getFullNameValidationMessage(): String = "Full name must be at least 2 characters long"
}