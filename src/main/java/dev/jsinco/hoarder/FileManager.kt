package dev.jsinco.hoarder

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.nio.file.Files


class FileManager(fileName: String) {

    companion object {
        val plugin: Hoarder = Hoarder.getInstance()
        fun generateFolder(folder: String) {
            if (!File(plugin.dataFolder, folder).exists()) {
                File(plugin.dataFolder, folder).mkdir()
            }
        }
    }

    var file: File = File(plugin.dataFolder, fileName)
    private val yamlConfiguration: YamlConfiguration = YamlConfiguration.loadConfiguration(file)


    fun generateFile(): Boolean {
        var returnValue = false
        try {
            if (!file.exists()) {
                file.createNewFile()

                val inputStream = plugin.getResource(file.name)
                val outputStream = Files.newOutputStream(file.toPath())
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                inputStream.close()
                outputStream.flush()
                outputStream.close()
                returnValue = true
            }
        } catch (ex: IOException) {
            plugin.logger.warning("Couldnt save file: ${file.name} \n $ex")
            returnValue = false
        }
        return returnValue
    }

    fun getFileYaml(): YamlConfiguration {
        return yamlConfiguration
    }

    fun saveFileYaml() {
        yamlConfiguration.save(file)
    }

    fun reloadFileYaml() {
        yamlConfiguration.load(file)
    }

}