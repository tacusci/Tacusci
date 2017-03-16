package utils

import app.controllers.Web
import database.models.RouteEntityNode
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
                .attr("pattern", Validation.fullNameRegexStruct())
                .attr("oninvalid", "setCustomValidity('${Validation.getFullNameValidationMessage()}')")
                .attr("oninput", "setCustomValidity('')")
                .isRequired
    }

    fun usernameInput(identifier: String, placeholder: String): Tag {
        return input()
                .withId(identifier)
                .withType("text")
                .withName(identifier)
                .withPlaceholder(placeholder)
                .attr("pattern", Validation.usernameRegexStruct())
                .attr("oninvalid", "setCustomValidity('${Validation.getUsernameValidationMessage()}')")
                .attr("oninput", "setCustomValidity('')")
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

    fun validatedPasswordInput(identifier: String, placeholder: String): Tag {
        return input()
                .withType("password")
                .withId(identifier)
                .withName(identifier)
                .withPlaceholder(placeholder)
                .attr("pattern", Validation.passwordRegexStruct())
                .attr("oninvalid", "setCustomValidity('${Validation.getPasswordValidationMessage()}')")
                .attr("oninput", "setCustomValidity('')")
                .isRequired
    }

    fun emailInput(identifier: String, placeholder: String): Tag {
        return input()
                .withId(identifier)
                .withType("text")
                .withName(identifier)
                .withPlaceholder(placeholder)
                .attr("pattern", Validation.emailRegexStruct())
                .attr("oninvalid", "setCustomValidity('${Validation.getEmailValidationMessage()}')")
                .attr("oninput", "setCustomValidity('')")
                .isRequired
    }

    fun pureFormCompact_Login(session: Session, name: String, legend: String, href: String): ContainerTag {
        val hash = Web.mapFormToHash(session, name)
        return form().withId(name).withName(name).withClass("pure-form").withHref(href).withMethod("post").with(
                input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden,
                input().withName("formName").withValue(name).isHidden,
                fieldset().with(
                        legend(legend),
                        input().withId("username").withPlaceholder("Username"),
                        validatedPasswordInput("password", "Password"),
                        button().withMethod("submit").withClass("pure-button pure-button-primary").withText("Login")
                )
        )
    }

    fun pureFormStacked_Login(session: Session, name: String, legend: String, href: String, method: String): ContainerTag {
        val hash = Web.mapFormToHash(session, name)
        return form().withId(name).withName(name).withClass("pure-form pure-form-stacked").withHref(href).withMethod(method).with(
                input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden,
                input().withId("formName").withName(name).isHidden,
                fieldset().with(
                        legend(legend),
                        usernameInput("username", "Username"),
                        validatedPasswordInput("password", "Password"),
                        button().withMethod("submit").withClass("pure-button pure-button-primary").withText("Login")
                )
        )
    }

    fun pureFormAligned_Login(session: Session, name: String, href: String, method: String): ContainerTag {
        val hash = Web.mapFormToHash(session, name)
        return form().withId(name).withName(name).withClass("pure-form pure-form-aligned").withHref(href).withMethod(method).with(
                input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden,
                input().withName("formName").withValue(name).isHidden,
                fieldset().with(
                        div().withClass("pure-control-group").with(
                                label("Username").attr("for", "username"),
                                usernameInput("username", "Username")
                        ),

                        div().withClass("pure-control-group").with(
                                label("Password").attr("for", "password"),
                                validatedPasswordInput("password", "Password")
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
                input().withName("formName").withValue(name).isHidden,
                fieldset().with(
                        div().withClass("pure-control-group").with(
                                label("Full Name").attr("for", "full_name"),
                                fullNameInput("full_name", "Full Name")
                        ),

                        label("Full Name Invalid").withCondHidden(!session.attribute<Boolean>("full_name_field_error")),

                        div().withClass("pure-control-group").with(
                                label("Username").attr("for", "username"),
                                usernameInput("username", "Username")
                        ),

                        label("Username is invalid").withCondHidden(!session.attribute<Boolean>("username_field_error")),
                        label("Username is not available").withCondHidden(!session.attribute<Boolean>("username_not_available_error")),

                        div().withClass("pure-control-group").with(
                                label("Password").attr("for", "password"),
                                validatedPasswordInput("password", "Password")
                        ),

                        label("Password is invalid").withCondHidden(!session.attribute<Boolean>("password_field_error")),

                        div().withClass("pure-control-group").with(
                                label("Repeat Password").attr("for", "repeat_password"),
                                validatedPasswordInput("repeat_password", "Repeat Password")
                        ),

                        label("Repeated password is invalid").withCondHidden(!session.attribute<Boolean>("repeated_password_field_error")),
                        label("Passwords do not match").withCondHidden(!session.attribute<Boolean>("passwords_mismatch_error")),

                        div().withClass("pure-control-group").with(
                                label("Email").attr("for", "email"),
                                emailInput("email", "Email")
                        ),

                        label("Email is invalid").withCondHidden(!session.attribute<Boolean>("email_field_error")),

                        div().withClass("pure-controls").with(
                                button("Register").withMethod("submit").withClass("pure-button pure-button-primary")
                        ),

                        label("User created successfully").withCondHidden(!session.attribute<Boolean>("user_created_successfully"))
                )
        )
    }

    fun pureFormAligned_ResetPassword(session: Session, name: String, href: String, method: String): ContainerTag {
        val hash = Web.mapFormToHash(session, name)
        return form().withId(name).withName(name).withClass("pure-form pure-form-aligned").withHref(href).withMethod(method).with(
                input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden,
                input().withName("formName").withValue(name).isHidden,
                fieldset().with(
                        div().withClass("pure-control-group").with(
                                label("Type new password").attr("for", "new_password"),
                                validatedPasswordInput("new_password", "New password")
                        ),

                        label("New password is invalid").withCondHidden(!session.attribute<Boolean>("new_password_field_error")),

                        div().withClass("pure-control-group").with(
                                label("Retype new password").attr("for", "new_password_repeated"),
                                validatedPasswordInput("new_password_repeated", "Retype new password")
                        ),

                        label("Repeated new password is invalid").withCondHidden(!session.attribute<Boolean>("new_password_repeated_field_error")),

                        div().withClass("pure-controls").with(
                                button("Reset").withMethod("submit").withClass("pure-button pure-button-primary")
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
        return li().withClass("pure-menu-item").with(form().withId(name).withName(name).withMethod(method).withAction(href).with(input().withId("hashid").withName("hashid").withType("text").withValue(hash).isHidden, input().withName("formName").withValue(name).isHidden, submitLink(text, "submit-link")))
    }

    fun findElementIndexByID(containerTag: ContainerTag, idToFind: String): Int {
        containerTag.children.forEachIndexed { i, tag ->
            if (tag.render().contains(idToFind)) {
                return i
            }
        }
        return -1
    }

    fun generateListFromRouteElementNode(routeEntityNode: RouteEntityNode) {
        val listHeader = li(routeEntityNode.data.name)
    }
}
