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
        thread {
            val path: Path = File(File(file.absolutePath).parent).toPath()

            try {
                val watchService = FileSystems.getDefault().newWatchService()
                val watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)

                while (running) {
                    val wk = watchService.take()
                    wk.pollEvents().forEach { event ->
                        val changed: Path = event.context() as Path
                        if (changed.endsWith(file.name)) {
                            action()
                        }
                    }
                    val valid = wk.reset()
                    if (!valid) {
                        println("Key has been unregistered...")
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}