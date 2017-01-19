package database

import database.daos.DAOManager
import mu.KLogging
import kotlin.concurrent.thread

/**
 * Created by alewis on 19/01/2017.
 */

object DBPoller : KLogging() {

    private var running = false

    fun start() {
        running = true
        run()
    }

    fun stop() {
        running = false
        logger.info("Stopping DB poll...")
    }

    private fun run() {
        thread(name = "DB polling thread") {
            val userDAO = DAOManager.getDAO(DAOManager.TABLE.USERS)
            var lastTime = System.currentTimeMillis()
            while (running) {
                //every 30 minutes
                if (System.currentTimeMillis() - lastTime > 1800000) {
                    logger.info("Polling USERS table to keep connection alive")
                    userDAO.count()
                    lastTime = System.currentTimeMillis()
                }
            }
        }
    }
}