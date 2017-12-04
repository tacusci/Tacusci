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

package app.core.handlers

import app.core.pages.pagecontrollers.PageController
import database.daos.DAOManager
import database.daos.PageDAO
import database.models.Page
import database.models.User
import mu.KLogging

/**
 * Created by alewis on 04/05/2017.
 */
object PageHandler : KLogging() {

    val pageDAO = DAOManager.getDAO(DAOManager.TABLE.PAGES) as PageDAO

    fun createPage(page: Page): Boolean {
        val createdSuccessfully = pageDAO.insertPage(page)
        if (createdSuccessfully)
            PageController.mapPageRouteToDBPage(page.pageRoute)
        return createdSuccessfully
    }

    fun updatePage(page: Page): Boolean {
        val existingRoute = pageDAO.getPageById(page.id).pageRoute
        val updatedSuccessfully = pageDAO.updatePage(page)
        if (updatedSuccessfully) {
            PageController.mapPageRouteTo404Page(existingRoute)
            PageController.mapPageRouteToDBPage(page.pageRoute)
        }
        return updatedSuccessfully
    }

    fun deletePage(page: Page): Boolean {
        var deletedSuccessfully = false
        if (page.isDeleteable) {
            val existingRoute = pageDAO.getPageById(page.id).pageRoute
            deletedSuccessfully = pageDAO.deletePage(page)
            if (deletedSuccessfully) {
                PageController.mapPageRouteTo404Page(existingRoute)
            }
        } else {
            logger.error("Cannot delete page ${page.title}...")
        }
        return deletedSuccessfully
    }

    fun getAllPageRoutes(): MutableList<String> {
        return pageDAO.getAllPageRoutes()
    }

    fun getAllPages(): MutableList<Page> {
        return pageDAO.getAllPages()
    }

    fun getPageById(id: Int): Page {
        return pageDAO.getPageById(id, true)
    }

    fun getPageByTitle(title: String): Page {
        return pageDAO.getPageById(pageDAO.getPageIdByTitle(title), true)
    }

    fun getPageByRoute(route: String): Page {
        return pageDAO.getPageById(pageDAO.getPageIdByRoute(route), true)
    }

    fun updatePageFooter(pageFooterData: String, authorUser: User) {}
}