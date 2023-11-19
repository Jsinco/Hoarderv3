package dev.jsinco.hoarder.gui

import dev.jsinco.hoarder.manager.FileManager
import dev.jsinco.hoarder.Util
import dev.jsinco.hoarder.gui.enums.GUIType
import dev.jsinco.hoarder.objects.HoarderPlayer
import dev.jsinco.hoarder.manager.Settings
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.util.*

class GUICreator (val path: String) : InventoryHolder {

    val file = FileManager(path).getFileYaml()
    val title: String = Util.fullColor(file.getString("title")!!)
    val size: Int = file.getInt("size")
    val paginatedGUI: Optional<PaginatedGUI> = setGuiSpecifics()

    private val gui: Inventory = Bukkit.createInventory(this, size, title)
    private val itemsList: MutableList<GUIItem> = mutableListOf()

    private val guiType = GUIType.valueOf(file.getString("gui-type")?.uppercase() ?: "OTHER")

    init {
        val itemKeyPaths = file.getConfigurationSection("items")!!.getKeys(false)
        for (itemKey in itemKeyPaths) {
            itemsList.add(GUIItem(file, itemKey))
        }

        for (guiItem in itemsList) {
            if (guiItem.multiSlotted) {
                for (i in 0..guiItem.getSlots().size) {
                    gui.setItem(guiItem.getSlots()[i], guiItem.getItemStack())
                }
            } else {
                gui.setItem(guiItem.getSlot(), guiItem.getItemStack())
            }
        }
    }


    fun setGuiSpecifics(): Optional<PaginatedGUI> {
        val dynamicItemsFile = FileManager("guis/dynamicitems.yml").generateFile()
        when (guiType) {
            GUIType.MAIN -> { // FIXME: items need to set placeholders
                // We can use our GuiItem class for these dynamic items
                // TODO: Add runnable for clock!
                val eventItem = GUIItem(dynamicItemsFile, "active_item")
                val clock = GUIItem(dynamicItemsFile, "clock")

                gui.setItem(eventItem.getSlot(), eventItem.getItemStack())
                gui.setItem(clock.getSlot(), clock.getItemStack())
            }

            GUIType.TREASURE -> {
                val treasureItems = Settings.getDataManger().getAllTreasureItems() ?: return Optional.empty()

                val items: MutableList<ItemStack> = mutableListOf()

                for (treasureItem in treasureItems) {
                    val item = treasureItem.itemStack
                    val meta = item.itemMeta!!

                    val lore = meta.lore ?: emptyList<String?>().toMutableList()
                    for (string in dynamicItemsFile.getStringList("treasure.lore")) {
                        lore.add(Util.fullColor(string.replace("%chance%", treasureItem.weight.toString())))
                    }
                    meta.lore = lore
                    item.itemMeta = meta
                    items.add(item)
                }
                return Optional.of(PaginatedGUI(title, gui, items))
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
                return Optional.of(PaginatedGUI(title, gui, playerHeads))
            }
            else -> return Optional.empty()
        }
        return Optional.empty()
    }

    override fun getInventory(): Inventory {
        return gui
    }

}