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
import database.models.RoutePermission
import java.sql.SQLException
import java.util.*

/**
 * Created by tauraaamui on 24/06/2017.
 */
class RoutePermissionDAO(url: String, dbProperties: Properties, tableName: String, connectionPool: ConnectionPool) : GenericDAO(url, dbProperties, tableName, connectionPool) {

    fun getRoutePermission(routePermissionId: Int): RoutePermission {
        connect()
        val routePermission = RoutePermission()
        try {
            val selectStatement = "SELECT * FROM $tableName WHERE ID_PERMISSION=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setInt(1, routePermissionId)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                routePermission.id = resultSet.getInt("ID_PERMISSION")
                routePermission.createdDateTime = resultSet.getLong("CREATED_DATE_TIME")
                routePermission.lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
                routePermission.title = resultSet.getString("PERMISSION_TITLE")
                routePermission.route = resultSet.getString("ROUTE")
                routePermission.groupId = resultSet.getInt("ID_GROUPS")
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return routePermission }
        return routePermission
    }

    fun getRoutePermission(routePermissionTitle: String): RoutePermission {
        connect()
        val routePermission = RoutePermission()
        try {
            val selectStatement = "SELECT * FROM $tableName WHERE PERMISSION_TITLE=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, routePermissionTitle)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                routePermission.id = resultSet.getInt("ID_PERMISSION")
                routePermission.createdDateTime = resultSet.getLong("CREATED_DATE_TIME")
                routePermission.lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
                routePermission.title = resultSet.getString("PERMISSION_TITLE")
                routePermission.route = resultSet.getString("ROUTE")
                routePermission.groupId = resultSet.getInt("ID_GROUPS")
            }
            disconnect()
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return routePermission }
        return routePermission
    }

    fun getRoutePermissions(route: String): MutableList<RoutePermission> {
        connect()
        val routePermissions = mutableListOf<RoutePermission>()
        try {
            val selectStatement = "SELECT * FROM $tableName WHERE ROUTE=?"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            preparedStatement?.setString(1, route)
            val resultSet = preparedStatement?.executeQuery()
            if (resultSet!!.next()) {
                val routePermission = RoutePermission()
                routePermission.id = resultSet.getInt("ID_PERMISSION")
                routePermission.createdDateTime = resultSet.getLong("CREATED_DATE_TIME")
                routePermission.lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
                routePermission.title = resultSet.getString("PERMISSION_TITLE")
                routePermission.route = resultSet.getString("ROUTE")
                routePermission.groupId = resultSet.getInt("ID_GROUPS")
                routePermissions.add(routePermission)
            }
        } catch (e: SQLException) { e.message; disconnect(); return routePermissions }
        return routePermissions
    }

    fun getRoutePermissions(): MutableList<RoutePermission> {
        connect()
        val routePermissions = mutableListOf<RoutePermission>()
        try {
            val selectStatement = "SELECT * FROM $tableName"
            val preparedStatement = connection?.prepareStatement(selectStatement)
            val resultSet = preparedStatement?.executeQuery()
            while (resultSet!!.next()) {
                val routePermission = RoutePermission()
                routePermission.id = resultSet.getInt("ID_PERMISSION")
                routePermission.createdDateTime = resultSet.getLong("CREATED_DATE_TIME")
                routePermission.lastUpdatedDateTime = resultSet.getLong("LAST_UPDATED_DATE_TIME")
                routePermission.title = resultSet.getString("PERMISSION_TITLE")
                routePermission.route = resultSet.getString("ROUTE")
                routePermission.groupId = resultSet.getInt("ID_GROUPS")
                routePermissions.add(routePermission)
            }
        } catch (e: SQLException) { e.message; disconnect(); return routePermissions }
        return routePermissions
    }

    fun insertRoutePermission(routePermission: RoutePermission): Boolean {
        connect()
        try {
            val createRoutePermissionStatementString = "INSERT INTO $tableName (CREATED_DATE_TIME, LAST_UPDATED_DATE_TIME, PERMISSION_TITLE, ROUTE, ID_GROUPS) VALUES (?,?,?,?,?)"
            val preparedStatement = connection?.prepareStatement(createRoutePermissionStatementString)
            preparedStatement?.setLong(1, System.currentTimeMillis())
            preparedStatement?.setLong(2, System.currentTimeMillis())
            preparedStatement?.setString(3, routePermission.title)
            preparedStatement?.setString(4, routePermission.route)
            preparedStatement?.setInt(5, routePermission.groupId)
            preparedStatement?.execute()
            connection?.commit()
            preparedStatement?.close()
            disconnect()
            return true
        } catch (e: SQLException) { logger.error(e.message); disconnect(); return false }
    }
}