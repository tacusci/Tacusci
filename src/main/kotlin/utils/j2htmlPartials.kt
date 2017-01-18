package utils

import j2html.TagCreator.*
import j2html.tags.ContainerTag
import j2html.tags.Tag

/**
 * Created by alewis on 04/01/2017.
 */

object j2htmlPartials {

    fun fullNameInput(identifier: String, placeholder: String): Tag {
        return input()
                .withId(identifier)
                .withType("text")
                .withName(identifier)
                .withPlaceholder(placeholder)
                .isRequired
    }

    fun usernameInput(identifier: String, placeholder: String): Tag {
        return input()
                .withId(identifier)
                .withType("text")
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

    fun emailInput(identifier: String, placeholder: String): Tag {
        return input()
                .withId(identifier)
                .withType("text")
                .withName(identifier)
                .withPlaceholder(placeholder)
                .isRequired
    }

    fun pureFormCompact_Login(legend: String, href: String): ContainerTag {
        return form().withClass("pure-form").withHref(href).withMethod("post").with(
                fieldset().with(
                        legend(legend),
                        input().withId("username").withPlaceholder("Username"),
                        passwordInput("password", "Password"),
                        button().withMethod("submit").withClass("pure-button pure-button-primary").withText("Login")
                )
        )
    }

    fun pureFormStacked_Login(legend: String, href: String, method: String): ContainerTag {
        return form().withClass("pure-form pure-form-stacked").withHref(href).withMethod(method).with(
                fieldset().with(
                        legend(legend),
                        usernameInput("username", "Username"),
                        passwordInput("password", "Password"),
                        button().withMethod("submit").withClass("pure-button pure-button-primary").withText("Login")
                )
        )
    }

    fun pureFormAligned_Login(href: String, method: String): ContainerTag {
        return form().withClass("pure-form pure-form-aligned").withHref(href).withMethod(method).with(
                fieldset().with(
                        div().withClass("pure-control-group").with(
                                label("Username").attr("for", "username"),
                                usernameInput("username", "Username")
                        ),

                        div().withClass("pure-control-group").with(
                                label("Password").attr("for", "password"),
                                passwordInput("password", "Password")
                        ),

                        div().withClass("pure-controls").attr("style", "margin: 1.5em 0 0 auto;").with(
                                button().withMethod("submit").withClass("pure-button pure-button-primary").withText("Login")
                        )
                )
        )
    }

    fun pureFormAligned_Register(href: String, method: String): ContainerTag {
        return form().withClass("pure-form pure-form-aligned").withHref(href).withMethod(method).with(
                fieldset().with(
                        div().withClass("pure-control-group").with(
                                label("Full Name").attr("for", "full_name"),
                                fullNameInput("full_name", "Full Name")
                        ),

                        div().withClass("pure-control-group").with(
                                label("Username").attr("for", "username"),
                                usernameInput("username", "Username")
                        ),

                        div().withClass("pure-control-group").with(
                                label("Password").attr("for", "password"),
                                passwordInput("password", "Password")
                        ),

                        div().withClass("pure-control-group").with(
                                label("Email").attr("for", "email"),
                                emailInput("email", "Email")
                        ),

                        button("Register").withMethod("submit").withClass("pure-button pure-button-primary")
                )
        )
    }

    fun submitButton(text: String): Tag {
        return button().withType("submit").withText(text)
    }

    fun link(lookAndFeelClass: String, href: String, text: String): Tag {
        return a().withHref(href).withClass(lookAndFeelClass).withText(text)
    }

    fun submitButton(text: String, classString: String): Tag {
        return button().withType("submit").withClass(classString).withText(text)
    }

    fun pureChartElement(lookAndFeelClass: String = "pure-button", href: String, text: String): Tag {
        return a().withClass("size-chart-item $lookAndFeelClass").withHref(href).with(span().withClass("size-chart-label").with(span().withClass("size-chart-mod").withText(text)))
    }

    fun pureMenuItemLink(href: String, text: String): Tag {
        return li().withClass("pure-menu-item").with(a().withHref(href).withClass("pure-menu-link").withText(text))
    }

    fun pureMenuItemForm(href: String, method: String, text: String): Tag {
        return li().withClass("pure-menu-item").with(form().withClass("pure-menu-link").withMethod(method).withAction(href).with(submitButton(text, "pure-menu-link")))
    }
}
