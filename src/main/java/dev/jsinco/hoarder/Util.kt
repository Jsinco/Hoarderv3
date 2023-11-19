package dev.jsinco.hoarder

import com.iridium.iridiumcolorapi.IridiumColorAPI
import org.bukkit.ChatColor
import org.bukkit.Material
import java.io.File

object Util {

    private const val WITH_DELIMITER = "((?<=%1\$s)|(?=%1\$s))"


    // Text coloring


    /**
     * @param text The string of text to apply color/effects to
     * @return Returns a string of text with color/effects applied
     */
    @JvmStatic
    fun colorcode(text: String): String {
        val texts = text.split(String.format(WITH_DELIMITER, "&").toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val finalText = StringBuilder()
        var i = 0
        while (i < texts.size) {
            if (texts[i].equals("&", ignoreCase = true)) {
                //get the next string
                i++
                if (texts[i][0] == '#') {
                    finalText.append(net.md_5.bungee.api.ChatColor.of(texts[i].substring(0, 7)).toString() + texts[i].substring(7))
                } else {
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]))
                }
            } else {
                finalText.append(texts[i])
            }
            i++
        }
        return finalText.toString()
    }
    @JvmStatic
    fun colorcode(list: List<String>): List<String> {
        val coloredList: MutableList<String> = ArrayList()
        for (string in list) {
            coloredList.add(colorcode(string))
        }
        return coloredList
    }
    @JvmStatic
    fun fullColor(string: String): String {
        return IridiumColorAPI.process(colorcode(string))
    }
    @JvmStatic
    fun fullColor(list: List<String>): List<String> {
        return colorcode(list).map { IridiumColorAPI.process(it) }
    }


    fun listFilesForFolder(folder: File): List<File> {
        if (!folder.exists() || !folder.isDirectory) return listOf()
        val files: MutableList<File> = mutableListOf()

        for (fileEntry in folder.listFiles()!!) {
            if (fileEntry.isDirectory()) {
                files.addAll(listFilesForFolder(fileEntry))
            } else {
                files.add(fileEntry)
            }
        }
        return files
    }
}