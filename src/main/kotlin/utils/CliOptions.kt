/*
 * # DON'T BE A DICK PUBLIC LICENSE
 *
 * > Version 1.1, December 2016
 *
 * > Copyright (C) 2016 Adam Prakash Lewis
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

package utils

import mu.KLogging

/**
 * Created by alewis on 25/04/2017.
 */

data class CliOption(val title: String, val cliText: String, val argumentExpected: Boolean = false, var isFlag: Boolean = false, var value: String = "")

object CliOptions : KLogging() {

    val cliOptions = mutableListOf<CliOption>()
    var usageString = ""

    fun parseArgs(args: Array<String>) {
        args.forEachIndexed { index, arg ->
            cliOptions.forEach { cliOption ->
                if (arg.startsWith("-")) {
                    if (arg.replace("-", "").toLowerCase() == cliOption.cliText.toLowerCase()) {
                        if (cliOption.argumentExpected) {
                            if (index + 1 < args.size && !args[index + 1].startsWith("-")) {
                                cliOption.value = args[index + 1]
                            }
                        } else {
                            cliOption.isFlag = true
                        }
                    }
                }
            }
        }

        cliOptions.forEach { cliOption ->
            if (cliOption.argumentExpected && (cliOption.value.isEmpty() || cliOption.value.isBlank())) {
                outputUsageAndClose()
            }
            if (cliOption.argumentExpected) Config.setProperty(cliOption.cliText, cliOption.value)
            if (!cliOption.argumentExpected) Config.setProperty(cliOption.cliText, cliOption.isFlag.toString())
        }
    }

    fun getOptionValue(cliText: String): String {
        cliOptions.forEach { if (it.cliText == cliText) return it.value }
        return ""
    }

    fun getFlag(cliText: String): Boolean {
        cliOptions.forEach { if (it.cliText == cliText) return it.isFlag }
        return false
    }

    fun outputUsageAndClose() {
        System.err.println("Missing required CLI arguments -> $usageString")
        System.exit(1)
    }
}
