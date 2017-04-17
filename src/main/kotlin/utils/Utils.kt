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

import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by alewis on 14/03/2017.
 */

class Utils {

    companion object {

        val secureRandom = SecureRandom()
        val charSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";


        fun randomHash(length: Int): String {
            val stringBuilder = StringBuilder(length)
            for (index in 0..length) {
                stringBuilder.append(charSet[secureRandom.nextInt(charSet.length)])
            }
            return stringBuilder.toString()
        }

        fun getDateTimeNow(): String = convertMillisToDataTime(System.currentTimeMillis())
        fun getDateNow(): String = convertMillisToDate(System.currentTimeMillis())

        fun convertMillisToDate(millis: Long): String {
            val formatter = SimpleDateFormat("dd-MM-yyyy")
            return formatter.format(Date(millis))
        }

        fun convertMillisToDataTime(millis: Long): String {
            val formatter = SimpleDateFormat("HH:mm dd-MM-yyyy")
            return formatter.format(millis)
        }

        //TODO use regex to get this function to infer the format type from a list
        fun convertDateToMillis(dateToConvert: String, dateStringFormat: String): Long {
            val simpleDateFormat = SimpleDateFormat(dateStringFormat)
            val date = simpleDateFormat.parse(dateToConvert)
            return date.time
        }
    }
}