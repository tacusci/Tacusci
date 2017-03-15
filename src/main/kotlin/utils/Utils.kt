package utils

import java.math.BigInteger
import java.security.SecureRandom

/**
 * Created by alewis on 14/03/2017.
 */

class Utils {

    companion object {

        val secureRandom = SecureRandom()

        fun randomHash(): String {
            return BigInteger(260, secureRandom).toString(32)
        }
    }
}