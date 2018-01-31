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

import database.daos.DAOManager
import database.daos.IncludeDAO
import database.models.Include
import mu.KLogging

object IncludeHandler : KLogging() {

    val includeDAO = DAOManager.getDAO(DAOManager.TABLE.INCLUDES) as IncludeDAO

    fun createInclude(include: Include): Boolean {
        return includeDAO.insertInclude(include)
    }

    fun updateInclude(include: Include): Boolean {
        return includeDAO.updateInclude(include)
    }

    fun deleteInclude(include: Include): Boolean {
        return includeDAO.deleteInclude(include)
    }

    fun getAllIncludes(): MutableList<Include> {
        return includeDAO.getAllIncludes()
    }

    fun getAllIncludesOrderBy(orderByClause: String): MutableList<Include> {
        return includeDAO.getAllIncludesOrderBy(orderByClause)
    }

    fun getIncludeById(includeId: Int): Include {
        return includeDAO.getIncludeById(includeId, true)
    }

    fun getIncludeByTitle(includeTitle: String): Include {
        return includeDAO.getIncludeById(includeDAO.getIncludeIdByTitle(includeTitle), true)
    }
}