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

package database.daos

import app.pages.structured.StructuredPage
import database.models.Page
import mu.KLogging
import java.sql.SQLException
import java.util.*

/**
 * Created by alewis on 04/05/2017.
 */

class PagesDAO(url: String, dbProperties: Properties, tableName: String) : GenericDAO(url, dbProperties, tableName) {

    companion object : KLogging()

    fun insertPage(page: Page): Boolean {
        connect()
        try {
            val createPageStatementString = "INSERT INTO $tableName (CREATED_DATE_TIME, LAST_UPDATED_DATE_TIME, PAGE_TITLE, PAGE_ROUTE, PAGE_CONTENT, MAINTENANCE_MODE, AUTHOR_USER_ID, PAGE_TYPE) (?,?,?,?,?,?,?,?)"
            val preparedStatement = connection?.prepareStatement(createPageStatementString)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setLong(2, System.currentTimeMillis())
            preparedStatement?.setString(3, page.title)
            preparedStatement?.setString(4, page.pageRoute)
            preparedStatement?.setString(5, page.content)
            preparedStatement?.setInt(6, page.maintenanceMode)
            preparedStatement?.setInt(7, page.type.ordinal)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: Exception) { e.printStackTrace(); disconnect(); return false }
    }

    fun updatePage(page: Page): Boolean {
        connect()
        try {
            val updateStatement = "UPDATE $tableName SET LAST_UPDATED_DATE_TIME=? PAGE_TITLE=? PAGE_ROUTE=? PAGE_CONTENT=? MAINTENANCE_MODE=? AUTHOR_USER_ID=? PAGE_TYPE=? WHERE ID_PAGE=?"
            val preparedStatement = connection?.prepareStatement(updateStatement)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setString(2, page.title)
            preparedStatement?.setString(3, page.pageRoute)
            preparedStatement?.setString(4, page.content)
            preparedStatement?.setInt(5, page.maintenanceMode)
            preparedStatement?.setInt(6, page.authorUserId)
            preparedStatement?.setInt(7, page.type.ordinal)
            preparedStatement?.setInt(8, page.id)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun getPageIDByTitle(pageTitle: String): Int {
        connect()
        var pageId = -1
        try {
            val selectStatement = "SELECT PAGE_ID FROM $tableName WHERE PAGE_TITLE=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, pageTitle)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                pageId = resultSet.getInt(1)
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return pageId
    }

    fun getPage(pageId: Int): Page {
        val page = Page(-1, -1, -1, "", "", 0, "", -1)
        connect()
        try {
            val selectStatement = "SELECT * FROM $tableName WHERE ID_PAGE=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setInt(1, pageId)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                page.id = resultSet.getInt("ID_PAGE")
                page.createdDateTime = resultSet.getLong("CREATED_DATE_TIME")
                page.lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
                page.title = resultSet.getString("PAGE_TITLE")
                page.pageRoute = resultSet.getString("PAGE_ROUTE")
                page.content = resultSet.getString("PAGE_CONTENT")
                page.maintenanceMode = resultSet.getInt("MAINTENANCE_MODE")
                page.authorUserId = resultSet.getInt("AUTHOR_USER_ID")
                page.type = StructuredPage.PageType.fromInt(resultSet.getInt("PAGE_TYPE"))!!
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return page
    }

    fun getPages() {
        val pages = mutableListOf<Page>()
        connect()
        try {
            val selectStatement = "SELECT * FROM $tableName"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) {
                val page = Page(-1, -1, -1, "", "", 0, "", -1)
                page.id = resultSet.getInt(1, )
            }
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
    }
}