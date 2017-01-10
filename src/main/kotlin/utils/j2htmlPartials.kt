package utils

import handlers.UserHandler
import j2html.tags.Tag
import j2html.TagCreator.*

/**
 * Created by alewis on 04/01/2017.
 */

object j2htmlPartials {

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
        return input("username", placeholder)
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

    fun pureChartElement(lookAndFeelClass: String = "pure-button", href: String, text: String): Tag {
        return a().withClass("size-chart-item $lookAndFeelClass").withHref(href).with(span().withClass("size-chart-label").with(span().withClass("size-chart-mod").withText(text)))
    }

    fun pureMenuItem(lookAndFeelClass: String, href: String, text: String): Tag {
        return li().withClass("pure-menu-item $lookAndFeelClass").with(a().withHref(href).withClass("pure-menu-link").withText(text))
    }
}
