package htmlutils

/**
 * Created by alewis on 15/11/2016.
 */

class HTMLForm {

    private val model = StringBuilder()

    var acceptCharset = ""
    var action = ""
    var method = ""
    var name = ""
    var noValidate = ""
    var target = ""
    var content = ""

    constructor() {}

    constructor(acceptCharset: String, action: String, method: String, name: String, noValidate: String, target: String, content: String) {
        this.acceptCharset = acceptCharset
        this.action = action
        this.method = method
        this.name = name
        this.noValidate = noValidate
        this.target = target
        this.content = content
    }

    fun create(): String {
        val model = StringBuilder()
        model.append("<form accept-charset=\"$acceptCharset\" action=\"$action\" method=\"$method\" name=\"$name\" novalidate=\"$noValidate\" target=\"$target\">")
        model.append(content)
        model.append("</form>")
        return model.toString()
    }
}