/*
if (typeof(Storage) !== "undefined") {
    alert("HTML 5 Local Storage is supported")
} else {
    alert("HTML 5 Local Storage not supported")
}
*/

var editor = ace.edit("editor");
editor.setTheme("ace/theme/twilight");
editor.session.setMode("ace/mode/velocity");
editor.session.setUseWrapMode(true);