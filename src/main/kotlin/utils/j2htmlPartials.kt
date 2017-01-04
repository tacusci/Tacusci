package utils

import j2html.tags.Tag
import j2html.TagCreator.*

/**
 * Created by alewis on 04/01/2017.
 */
fun enterPasswordInput(placeholder: String): Tag {
    return passwordInput("enterPassword", placeholder)
}

fun choosePasswordInput(placeholder: String): Tag {
    return passwordInput("choosePassword", placeholder)
}

fun repeatPasswordInput(placeholder: String): Tag {
    return passwordInput("repeatPassword", placeholder)
}

fun usernameInput(placeholder: String): Tag {
    return utils.input("username", placeholder)
}

fun input(identifier: String, placeholder: String): Tag {
    return input().withId(identifier)
            .withName(identifier)
            .withPlaceholder(placeholder)
            .isRequired
}

fun passwordInput(identifier: String, placeholder: String): Tag {
    return input()
            .withType("password")
            .withId(identifier)
            .withName(identifier)
            .withPlaceholder(placeholder)
            .isRequired
}

fun emailInput(placeholder: String): Tag {
    return input()
            .withType("email")
            .withId("email")
            .withName("email")
            .withPlaceholder(placeholder)
            .isRequired
}

fun submitButton(text: String): Tag {
    return button().withType("submit").withText(text)
}

fun submitButton(text: String, classString: String): Tag {
    return button().withType("submit").withClass(classString).withText(text)
}