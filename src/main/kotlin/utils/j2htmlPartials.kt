package utils

import controllers.Web
import j2html.TagCreator.*
import j2html.tags.ContainerTag
import j2html.tags.Tag
import spark.Session

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

    fun pureFormCompact_Login(session: Session, name: String, legend: String, href: String): ContainerTag {
        val hash = Web.mapFormToHash(session, name)
        return form().withId(name).withName(name).withClass("pure-form").withHref(href).withMethod("post").with(
                input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden,
                fieldset().with(
                        legend(legend),
                        input().withId("username").withPlaceholder("Username"),
                        passwordInput("password", "Password"),
                        button().withMethod("submit").withClass("pure-button pure-button-primary").withText("Login")
                )
        )
    }

    fun pureFormStacked_Login(session: Session, name: String, legend: String, href: String, method: String): ContainerTag {
        val hash = Web.mapFormToHash(session, name)
        return form().withId(name).withName(name).withClass("pure-form pure-form-stacked").withHref(href).withMethod(method).with(
                input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden,
                fieldset().with(
                        legend(legend),
                        usernameInput("username", "Username"),
                        passwordInput("password", "Password"),
                        button().withMethod("submit").withClass("pure-button pure-button-primary").withText("Login")
                )
        )
    }

    fun pureFormAligned_Login(session: Session, name: String, href: String, method: String): ContainerTag {
        val hash = Web.mapFormToHash(session, name)
        return form().withId(name).withName(name).withClass("pure-form pure-form-aligned").withHref(href).withMethod(method).with(
                input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden,
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

    fun pureFormAligned_Register(session: Session, name: String, href: String, method: String): ContainerTag {
        val hash = Web.mapFormToHash(session, name)
        return form().withId(name).withName(name).withClass("pure-form pure-form-aligned").withHref(href).withMethod(method).with(
                input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden,
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

                        div().withClass("pure-controls").with(
                                button("Register").withMethod("submit").withClass("pure-button pure-button-primary")
                        )
                )
        )
    }

    fun submitButton(text: String): ContainerTag {
        return button(text).withMethod("submit").withClass("pure-button pure-button-primary")
    }

    fun link(lookAndFeelClass: String, href: String, text: String): Tag {
        return a().withHref(href).withClass(lookAndFeelClass).withText(text)
    }

    fun submitLink(text: String, classString: String): Tag {
        return input().withType("submit").withClass(classString).withValue(text)
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

    fun pureMenuItemForm(session: Session, name: String, href: String, method: String, text: String): Tag {
        val hash = Web.mapFormToHash(session, name)
        return li().withClass("pure-menu-item").with(form().withId(name).withName(name).withMethod(method).withAction(href).with(input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden, submitLink(text, "submit-link")))
    }
}
