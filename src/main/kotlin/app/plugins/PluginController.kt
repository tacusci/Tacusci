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

package app.plugins

import mu.KLogging
import utils.Config
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile

class PluginController {

    companion object : KLogging()

    private val pluginJars = mutableListOf<File>()

    fun loadPlugins(): MutableList<Class<*>> {
        val pluginClasses = mutableListOf<Class<*>>()
        val pluginsFolder = File(Config.getProperty("plugins-folder"))
        if (pluginsFolder.isDirectory) {
            pluginsFolder.listFiles().forEach { if (it.endsWith(".jar")); pluginJars.add(it) }
        }

        pluginJars.forEach {
            val loader = URLClassLoader.newInstance(arrayOf(it.toURI().toURL()), ClassLoader.getSystemClassLoader())
            val jarFile = JarFile(it)
            val allElements = jarFile.entries()
            while (allElements.hasMoreElements()) {
                val jarEntry = allElements.nextElement()
                if (jarEntry.name.endsWith(".class")) {
                    try {
                        val clazzInstance = Class.forName(jarEntry.name.removeSuffix(".class"), true, loader)
                        val inherits = classInheritsInterface(clazzInstance, "co.uk.taurasystems.tacusci.plugin.TacusciPlugin")
                        if (inherits) pluginClasses.add(clazzInstance)
                    } catch (e: ClassNotFoundException) {
                        logger.error(e.message)
                    }
                }
            }
        }
        return pluginClasses
    }

    private fun classInheritsInterface(clazz: Class<*>, interfaceName: String): Boolean {
        var found = false
        clazz.interfaces.forEach { found = it.name == interfaceName; println(it.name); if (found) return found }
        return found
    }
}