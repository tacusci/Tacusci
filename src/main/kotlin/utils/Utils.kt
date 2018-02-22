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

package utils

import extensions.isNullOrBlankOrEmpty
import extensions.toMD5Hash
import mu.KLogging
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by alewis on 14/03/2017.
 */

class Utils {

    companion object : KLogging() {

        val secureRandom = SecureRandom()
        val charSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        fun randomHash(length: Int): String {
            val stringBuilder = StringBuilder(length)
            for (index in 0..length) {
                stringBuilder.append(charSet[secureRandom.nextInt(charSet.length)])
            }
            return stringBuilder.toString()
        }

        fun getDateTimeNow(): String = convertMillisToDateTime(System.currentTimeMillis())
        fun getDateTimeNow(format: String): String = convertMillisToDateTime(System.currentTimeMillis(), format)
        fun getDateNow(): String = convertMillisToDate(System.currentTimeMillis())
        fun getDateNow(format: String): String = convertMillisToDate(System.currentTimeMillis(), format)

        fun convertMillisToDate(millis: Long): String {
            return convertMillisToDate(millis, "dd-MM-yyyy")
        }

        fun convertMillisToDate(millis: Long, format: String): String {
            val formatter = SimpleDateFormat(format)
            return formatter.format(Date(millis))
        }

        fun convertMillisToDateTime(millis: Long): String {
            return convertMillisToDateTime(millis, "HH:mm dd:MM:yyyy")
        }

        fun convertMillisToDateTime(millis: Long, format: String): String {
            val formatter = SimpleDateFormat(format)
            return formatter.format(millis)
        }

        //TODO use regex to get this function to infer the format type from a list
        fun convertDateToMillis(dateToConvert: String, dateStringFormat: String): Long {
            val simpleDateFormat = SimpleDateFormat(dateStringFormat)
            val date = simpleDateFormat.parse(dateToConvert)
            return date.time
        }

        fun convertStringToMD5Hash(textToConvert: String): String {
            return textToConvert.toMD5Hash()
        }

        fun isInteger(stringToCheck: String): Boolean {
            return isInteger(stringToCheck, 10)
        }

        private fun isInteger(stringToCheck: String, radix: Int): Boolean {
            if (stringToCheck.isNullOrBlankOrEmpty()) return false
            for (i in 0 until stringToCheck.length) {
                if (i == 0 && stringToCheck.toCharArray()[i] == '-') {
                    if (stringToCheck.length == 1) return false
                    else continue
                }
                if (Character.digit(stringToCheck.toCharArray()[i], radix) < 0) return false
            }
            return true
        }
    }
}