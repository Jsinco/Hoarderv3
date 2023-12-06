package dev.jsinco.hoarder.utilities

import dev.jsinco.hoarder.manager.FileManager

object Messages {

    val messagesFile = FileManager("messages.yml").generateYamlFile()

    fun getPrefix(): String {
        return Util.fullColor(messagesFile.getString("prefix") ?: "")
    }

    @JvmStatic
    fun getMsgList(path: String): List<String> {
        return Util.fullColor(messagesFile.getStringList(path).map { getPrefix() + it })
    }

    @JvmStatic
    fun getMsg(path: String): String {
        return Util.fullColor((getPrefix() + messagesFile.getString(path)))
    }

    @JvmStatic
    fun getMsgConsoleSender(path: String): String {
        return messagesFile.getString(path) ?: ""
    }
}