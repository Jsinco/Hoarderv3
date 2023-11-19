package dev.jsinco.hoarder.gui

import dev.jsinco.hoarder.Util
import dev.jsinco.hoarder.gui.enums.GUIType
import dev.jsinco.hoarder.manager.FileManager
import dev.jsinco.hoarder.manager.Settings
import dev.jsinco.hoarder.objects.HoarderPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class GUICreator (val path: String) : InventoryHolder {

    val file = FileManager(path).getFileYaml()
    val title: String = Util.fullColor(file.getString("title")!!)
    val size: Int = file.getInt("size")

    val gui: Inventory = Bukkit.createInventory(this, size, title)
    val itemsList: MutableList<GUIItem> = mutableListOf()

    val guiType = GUIType.valueOf(file.getString("gui-type")?.uppercase() ?: "OTHER")

    init {
        val itemKeyPaths = file.getConfigurationSection("items")!!.getKeys(false)
        for (itemKey in itemKeyPaths) {
            itemsList.add(GUIItem(file, itemKey))
        }

        for (guiItem in itemsList) {
            if (guiItem.multiSlotted) {
                for (slot in guiItem.getSlots()) {
                    gui.setItem(slot, guiItem.getItemStack())
                }
            } else {
                gui.setItem(guiItem.getSlot(), guiItem.getItemStack())
            }
        }
    }


    override fun getInventory(): Inventory {
        return gui
    }

    // Hopefully no exceptions
    val paginatedGUI: PaginatedGUI? = setGuiSpecifics()

    fun setGuiSpecifics(): PaginatedGUI? {
        val dynamicItemsFile = FileManager("guis/dynamicitems.yml").generateYamlFile()
        when (guiType) {
            GUIType.MAIN -> { // FIXME: items need to set placeholders
                // We can use our GuiItem class for these dynamic items
                // TODO: Add runnable for clock!

                val eventItem = GUIItem(dynamicItemsFile, "items.active_item")
                val clock = GUIItem(dynamicItemsFile, "items.clock")

                gui.setItem(eventItem.getSlot(), eventItem.getItemStack())
                gui.setItem(clock.getSlot(), clock.getItemStack())
            }

            GUIType.TREASURE -> {
                val treasureItems = Settings.getDataManger().getAllTreasureItems() ?: return null

                val items: MutableList<ItemStack> = mutableListOf()

                for (treasureItem in treasureItems) {
                    val item = treasureItem.itemStack
                    val meta = item.itemMeta!!

                    val lore = meta.lore ?: emptyList<String?>().toMutableList()
                    for (string in dynamicItemsFile.getStringList("items.treasure.lore")) {
                        lore.add(Util.fullColor(string.replace("%chance%", treasureItem.weight.toString())))
                    }
                    meta.lore = lore
                    item.itemMeta = meta
                    items.add(item)
                }

                return PaginatedGUI(title, gui, items)
            }

            GUIType.STATS -> {
                val hoarderPlayerUUIDS = Settings.getDataManger().getAllHoarderPlayersUUIDS()

                val playerHeads: MutableList<ItemStack> = mutableListOf()

                for (uuid in hoarderPlayerUUIDS) {
                    val hoarderPlayer = HoarderPlayer(uuid)

                    val item = ItemStack(Material.valueOf(dynamicItemsFile.getString("items.stats.material")!!.uppercase()))
                    val meta = item.itemMeta!!

                    meta.setDisplayName(Util.fullColor(dynamicItemsFile.getString("items.stats.name")!!
                        .replace("%name%", hoarderPlayer.getName()))
                        .replace("%points%", hoarderPlayer.getPoints().toString())
                    )

                    meta.lore = Util.fullColor(dynamicItemsFile.getStringList("items.stats.lore").map {
                        it.replace("%name%", hoarderPlayer.getName())
                            .replace("%points%", hoarderPlayer.getPoints().toString())
                    })
                    if (dynamicItemsFile.getBoolean("items.stats.enchanted")) meta.addEnchant(Enchantment.DURABILITY, 1, true)

                    item.itemMeta = meta
                    playerHeads.add(GUIItem.setPlayerHead(item, uuid))
                }
                return PaginatedGUI(title, gui, playerHeads)
            }
            else -> return null
        }
        return null
    }



}