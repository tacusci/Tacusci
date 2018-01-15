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

import database.ConnectionPool
import database.models.Template
import mu.KLogging
import java.sql.SQLException
import java.util.*

/**
 * Created by tauraaamui on 17/06/2017.
 */

class TemplateDAO(url: String, dbProperties: Properties, tableName: String, connectionPool: ConnectionPool) : GenericDAO(url, dbProperties, tableName, connectionPool) {

    companion object : KLogging()

    fun insertTemplate(template: Template): Boolean {
        connect()
        try {
            val createTemplateStatementString = "INSERT INTO $tableName (CREATED_DATE_TIME, LAST_UPDATED_DATE_TIME, TEMPLATE_TITLE, TEMPLATE_CONTENT, AUTHOR_USER_ID) VALUES (?,?,?,?,?) ${DAOManager.getConflictConstraintCommand("templates_template_title_key")}"
            val preparedStatement = connection?.prepareStatement(createTemplateStatementString)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setLong(2, System.currentTimeMillis())
            preparedStatement?.setString(3, template.title)
            preparedStatement?.setString(4, template.content.trim().removeSuffix("\r\n"))
            preparedStatement?.setInt(5, template.authorUserId)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }

    fun updateTemplate(template: Template): Boolean {
        connect()
        return try {
            val updateStatement = "UPDATE $tableName SET LAST_UPDATED_DATE_TIME=?, TEMPLATE_TITLE=?, TEMPLATE_CONTENT=?, AUTHOR_USER_ID=? WHERE ID_TEMPLATE=?"
            val preparedStatement = connection?.prepareStatement(updateStatement)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setString(2, template.title)
            preparedStatement?.setString(3, template.content.trim().removeSuffix("\r\n"))
            preparedStatement?.setInt(4, template.authorUserId)
            preparedStatement?.setInt(5, template.id)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); false }
    }

    fun deleteTemplate(template: Template): Boolean {
        val pageDAO = DAOManager.getDAO(DAOManager.TABLE.PAGES) as PageDAO
        val idsOfPagesWhichUseTemplate = pageDAO.getPageIdsUsingTemplate(template.id)

        idsOfPagesWhichUseTemplate.forEach {
            val currentPageUsingTemplate = pageDAO.getPageById(it)
            currentPageUsingTemplate.templateToUseId = -1
            pageDAO.updatePage(currentPageUsingTemplate)
        }

        connect()
        return try {
            val deleteStatement = "DELETE FROM $tableName WHERE ID_TEMPLATE=?"
            val preparedStatement = connection?.prepareStatement(deleteStatement)
            preparedStatement?.setInt(1, template.id)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); false }
    }

    fun getTemplateIdByTitle(templateTitle: String): Int {
        connect()
        var templateId = -1
        try {
            val selectStatement = "SELECT ID_TEMPLATE FROM $tableName WHERE TEMPLATE_TITLE=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, templateTitle)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                templateId = resultSet.getInt(1)
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return templateId
    }

    fun getTemplateById(templateId: Int): Template {
        val template = Template()
        connect()
        try {
            val selectStatement = "SELECT * FROM $tableName WHERE ID_TEMPLATE=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setInt(1, templateId)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                template.id = resultSet.getInt("ID_TEMPLATE")
                template.createdDateTime = resultSet.getLong("CREATED_DATE_TIME")
                template.lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
                template.title = resultSet.getString("TEMPLATE_TITLE")
                template.content = resultSet.getString("TEMPLATE_CONTENT")
                template.authorUserId = resultSet.getInt("AUTHOR_USER_ID")
            }
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return template
    }

    fun getTemplateById(templateId: Int, closeConnection: Boolean): Template {
        if (!closeConnection) {
            return getTemplateById(templateId)
        } else {
            val template = getTemplateById(templateId)
            disconnect()
            return template
        }
    }

    fun getAllTemplates(): MutableList<Template> {
        val templates = mutableListOf<Template>()
        connect()
        try {
            val selectStatement = "SELECT ID_TEMPLATE FROM $tableName"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) {
                val templateId = resultSet.getInt("ID_TEMPLATE")
                templates.add(getTemplateById(templateId, false))
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect() }
        return templates
    }
}