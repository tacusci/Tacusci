package htmlutils

/**
 * Created by tauraamui on 08/11/2016.
 */

object HTMLUtils {
    fun genRadioButton(name: String, id: String, value: String): String {
        val radioButtonString = "<input type='radio' name='$name' id='$id' value='$value'>"
        return radioButtonString
    }
}
