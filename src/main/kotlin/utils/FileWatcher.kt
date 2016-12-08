package utils

import mu.KLogging
import java.io.File
import java.nio.file.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * Created by alewis on 06/12/2016.
 */

class FileWatcher(var file: File) : KLogging() {

    var running = false
    var action: () -> Any = {}

    fun start() {
        running = true
        run()
    }

    fun stop() {
        running = false
    }

    private fun run() {
        thread(name = "file_watcher:${file.name}") {
            val path: Path = File(File(file.absolutePath).parent).toPath()

            try {
                val watchService = FileSystems.getDefault().newWatchService()
                val watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)

                while (running) {
                    val wk = watchService.take()
                    wk.pollEvents().forEach { event ->
                        val changed: Path = event.context() as Path
                        if (changed.endsWith(file.name)) {
                            logger.info("File ${file.name} has been updated")
                            action()
                        }
                    }
                    val valid = wk.reset()
                    if (!valid) {
                        logger.info("File ${file.name} watch key has been unregistered...")
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}