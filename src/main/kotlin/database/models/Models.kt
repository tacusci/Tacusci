/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
 *
 * > Copyright (C) 2016-2017 Adam Prakash Lewis
 *
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document.
 *
 * > DON'T BE A DICK PUBLIC LICENSE
 * > TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 *
 *  1. Do whatever you like with the original work, just don't be a dick.
 *
 *      Being a dick includes - but is not limited to - the following instances:
 *
 * 	 1a. Outright copyright infringement - Don't just copy this and change the name.
 * 	 1b. Selling the unmodified original with no work done what-so-ever, that's REALLY being a dick.
 * 	 1c. Modifying the original work to contain hidden harmful content. That would make you a PROPER dick.
 *
 *  2. If you become rich through modifications, related works/services, or supporting the original work,
 *  share the love. Only a dick would make loads off this work and not buy the original work's
 *  creator(s) a pint.
 *
 *  3. Code is provided with no warranty. Using somebody else's code and bitching when it goes wrong makes
 *  you a DONKEY dick. Fix the problem yourself. A non-dick would submit the fix back.
 */
 
 
 
 package database.models

import app.core.handlers.RouteEntityHandler
import app.core.pages.structured.StructuredPage
import utils.Validation
import utils.tree.Node
import utils.tree.Tree

/**
 * Created by alewis on 20/12/2016.
 */
data class User(var id: Int, var createdDateTime: Long, var lastUpdatedDateTime: Long,
                var fullName: String, var username: String, var password: String, var email: String,
                var banned: Int, var rootAdmin: Int) {

    fun isValid(): Boolean {
        if (isUsernameValid()) { return true }
        if (isFullnameValid()) { return true }
        if (isPasswordValid()) { return true }
        if (isEmailValid()) { return true }
        return false
    }

    fun isUsernameValid(): Boolean {
        return !(username.isBlank() || username.isEmpty()) && Validation.matchUsernamePattern(username)
    }

    fun isFullnameValid(): Boolean {
        return !(fullName.isBlank() || fullName.isEmpty()) && Validation.matchFullNamePattern(fullName)
    }

    fun isPasswordValid(): Boolean {
        return !(password.isBlank() || password.isEmpty()) && Validation.matchPasswordPattern(password)
    }

    fun isEmailValid(): Boolean {
        return !(email.isBlank() || email.isEmpty()) && Validation.matchEmailPattern(email)
    }
}


data class Group(var name: String, var parentGroupId: Int = -1) {

    fun isValid(): Boolean {
        if (!isNameValid()) { return false }
        return true
    }

    fun isNameValid(): Boolean {
        return !(name.isBlank() || name.isEmpty())
    }
}

class RouteEntityTree() : Tree<RouteEntity>() {

    constructor(routeEntityNode: RouteEntityNode) : this() {
        this.rootElement = routeEntityNode
    }

}
class RouteEntityNode() : Node<RouteEntity>() {

    constructor(routeEntity: RouteEntity) : this() {
        this.data = routeEntity
    }
    override fun addChild(node: Node<RouteEntity>) {
        node.data.parentId = this.data.id
        super.addChild(node)
    }

    fun addChildren(nodeList: List<Node<RouteEntity>>) = nodeList.forEach { addChild(it) }

}

data class RouteEntity(var id: Long, var parentId: Long, var name: String,
                       var routeEntityType: RouteEntityHandler.ROUTE_ENTITY_TYPE,
                       var pageId: Long)

data class Page(var id: Int = -1, var createdDateTime: Long = -1, var lastUpdatedDateTime: Long = -1,
                var title: String = "", var pageRoute: String = "", var maintenanceMode: Int = 0,
                var content: String = "",
                var isDeleteable: Boolean = true,
                var templateToUseId: Int = -1,
                var authorUserId: Int = -1,
                var type: StructuredPage.PageType = StructuredPage.PageType.RAW)

data class Template(var id: Int = 1, var createdDateTime: Long = 1, var lastUpdatedDateTime: Long = -1,
                    var title: String = "", var content: String = "", var authorUserId: Int = -1)