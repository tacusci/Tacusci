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

import app.core.pages.structured.StructuredPage
import database.ConnectionPool
import database.models.Page
import mu.KLogging
import java.sql.SQLException
import java.util.*
import kotlin.coroutines.experimental.buildSequence

/**
 * Created by alewis on 04/05/2017.
 */

class PageDAO(url: String, dbProperties: Properties, tableName: String, connectionPool: ConnectionPool) : GenericDAO(url, dbProperties, tableName, connectionPool) {

    companion object : KLogging()

    fun insertPage(page: Page): Boolean {
        connect()
        try {
            val createPageStatementString = "INSERT INTO $tableName (CREATED_DATE_TIME, LAST_UPDATED_DATE_TIME, PAGE_TITLE, PAGE_ROUTE, PAGE_CONTENT, DELETEABLE, DISABLED, TEMPLATE_TO_USE_ID, MAINTENANCE_MODE, AUTHOR_USER_ID, PAGE_TYPE) VALUES (?,?,?,?,?,?,?,?,?,?,?) ${DAOManager.getConflictConstraintCommand("pages_page_route_key")}"
            val preparedStatement = connection?.prepareStatement(createPageStatementString)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setLong(2, System.currentTimeMillis())
            preparedStatement?.setString(3, page.title)
            preparedStatement?.setString(4, page.pageRoute)
            preparedStatement?.setString(5, page.content.trim().removeSuffix("\r\n"))
            preparedStatement?.setBoolean(6, page.isDeleteable)
            preparedStatement?.setBoolean(7, page.isDisabled)
            preparedStatement?.setInt(8, page.templateToUseId)
            preparedStatement?.setBoolean(9, page.maintenanceMode)
            preparedStatement?.setInt(10, page.authorUserId)
            preparedStatement?.setInt(11, page.type.ordinal)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: Exception) { logger.error(e.message); disconnect(); return false }
    }

    fun updatePage(page: Page): Boolean {
        connect()
        try {
            val updateStatement = "UPDATE $tableName SET LAST_UPDATED_DATE_TIME=?, PAGE_TITLE=?, PAGE_ROUTE=?, PAGE_CONTENT=?, DELETEABLE=?, DISABLED=?, TEMPLATE_TO_USE_ID=?, MAINTENANCE_MODE=?, AUTHOR_USER_ID=?, PAGE_TYPE=? WHERE ID_PAGE=?"
            val preparedStatement = connection?.prepareStatement(updateStatement)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setString(2, page.title)
            preparedStatement?.setString(3, page.pageRoute)
            preparedStatement?.setString(4, page.content.trim().removeSuffix("\r\n"))
            preparedStatement?.setBoolean(5, page.isDeleteable)
            preparedStatement?.setBoolean(6, page.isDisabled)
            preparedStatement?.setInt(7, page.templateToUseId)
            preparedStatement?.setBoolean(8, page.maintenanceMode)
            preparedStatement?.setInt(9, page.authorUserId)
            preparedStatement?.setInt(10, page.type.ordinal)
            preparedStatement?.setInt(11, page.id)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun getPageIdsUsingTemplate(templateId: Int): Sequence<Int> {
        connect()
        try {
            val selectStatement = "SELECT ID_PAGE FROM $tableName WHERE TEMPLATE_TO_USE_ID=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setInt(1, templateId)
            val resultSet = preparedStatement?.executeQuery()
            // retrieves list of page ids from statement result
            val pageIds = buildSequence {
                while (resultSet!!.next()) {
                    yield(resultSet.getInt(1))
                }
            }
            return pageIds
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return sequenceOf(-1) }
    }

    fun deletePage(page: Page): Boolean {
        connect()
        try {
            val deleteStatement = "DELETE FROM $tableName WHERE ID_PAGE=?"
            val preparedStatement = connection?.prepareStatement(deleteStatement)
            preparedStatement?.setInt(1, page.id)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun getPageIdByTitle(pageTitle: String): Int {
        connect()
        var pageId = -1
        try {
            val selectStatement = "SELECT ID_PAGE FROM $tableName WHERE PAGE_TITLE=?"
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

    fun getPageIdByRoute(pageRoute: String): Int {
        connect()
        var pageId = -1
        try {
            val selectStatement = "SELECT ID_PAGE FROM $tableName WHERE PAGE_ROUTE=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, pageRoute)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                pageId = resultSet.getInt(1)
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return pageId
    }

    private fun getPageById(pageId: Int): Page {
        val page = Page()
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
                page.isDeleteable = resultSet.getBoolean("DELETEABLE")
                page.isDisabled = resultSet.getBoolean("DISABLED")
                page.templateToUseId = resultSet.getInt("TEMPLATE_TO_USE_ID")
                page.maintenanceMode = resultSet.getBoolean("MAINTENANCE_MODE")
                page.authorUserId = resultSet.getInt("AUTHOR_USER_ID")
                page.type = StructuredPage.PageType.fromInt(resultSet.getInt("PAGE_TYPE"))!!
            }
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return page
    }

    fun getPageById(pageId: Int, closeConnection: Boolean = true): Page {
        if (!closeConnection) {
            return getPageById(pageId)
        } else {
            val page = getPageById(pageId)
            disconnect()
            return page
        }
    }

    fun getAllPages(): MutableList<Page> {
        val pages = mutableListOf<Page>()
        connect()
        try {
            val selectStatement = "SELECT ID_PAGE FROM $tableName"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) {
                val pageId = resultSet.getInt("ID_PAGE")
                pages.add(getPageById(pageId, false))
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return pages
    }

    fun getAllPages(orderByClause: String): MutableList<Page> {
        val pages = mutableListOf<Page>()
        connect()
        try {
            val selectStatement = "SELECT ID_PAGE FROM $tableName ORDER BY $orderByClause"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) {
                val pageId = resultSet.getInt("ID_PAGE")
                pages.add(getPageById(pageId, false))
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return pages
    }

    fun  getAllPageRoutes(): MutableList<String> {
        val pageRoutes = mutableListOf<String>()
        connect()
        try {
            val selectStatement = "SELECT PAGE_ROUTE FROM $tableName"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) {
                pageRoutes.add(resultSet.getString("PAGE_ROUTE"))
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return pageRoutes
    }
}