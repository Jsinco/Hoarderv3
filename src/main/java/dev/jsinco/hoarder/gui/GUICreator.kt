package dev.jsinco.hoarder.gui

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.Util
import dev.jsinco.hoarder.gui.enums.GUIType
import dev.jsinco.hoarder.manager.FileManager
import dev.jsinco.hoarder.manager.Settings
import dev.jsinco.hoarder.objects.HoarderPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class GUICreator (val path: String) : InventoryHolder {

    private val plugin: Hoarder = Hoarder.getInstance()

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
    var paginatedGUI: PaginatedGUI? = null


    private lateinit var material: Material
    private var sellPrice: Double = 0.0

    // FIXME: probably redo/edit this
    fun setGuiSpecifics() {
        material = Settings.getDataManger().getEventMaterial()
        sellPrice = Settings.getDataManger().getEventSellPrice()

        val dynamicItemsFile = FileManager("guis/dynamicitems.yml").generateYamlFile()
        when (guiType) {
            GUIType.MAIN -> { // FIXME: items need to set placeholders
                // We can use our GuiItem class for these dynamic items
                // TODO: Add runnable for clock

                val activeItem = ItemStack(Material.valueOf(setMainGUIStrings(dynamicItemsFile.getString("items.active_item.material")!!)))
                val activeMeta = activeItem.itemMeta!!

                activeMeta.setDisplayName(setMainGUIStrings(dynamicItemsFile.getString("items.active_item.name")!!))
                activeMeta.lore = setMainGUIStrings(dynamicItemsFile.getStringList("items.active_item.lore"))
                if (dynamicItemsFile.getBoolean("items.active_item.enchanted")) activeMeta.addEnchant(Enchantment.DURABILITY, 1, true)
                activeMeta.persistentDataContainer.set(NamespacedKey(plugin, "action") , PersistentDataType.STRING, dynamicItemsFile.getString("items.active_item.action") ?: "NONE")
                activeItem.itemMeta = activeMeta
                gui.setItem(dynamicItemsFile.getInt("items.active_item.slot"), activeItem)
            }

            GUIType.TREASURE -> {
                val treasureItems = Settings.getDataManger().getAllTreasureItems() ?: return

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

                paginatedGUI = PaginatedGUI(title, gui, items)
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
                paginatedGUI = PaginatedGUI(title, gui, playerHeads)
            }
            else -> {}
        }
    }

    private fun setMainGUIStrings(string: String): String {
        return Util.fullColor(
            string.replace("%material%", material.toString())
                .replace("%material_formatted%", Util.formatMaterialName(material))
                .replace("%sell_price%", sellPrice.toString())
        )
    }

    private fun setMainGUIStrings(list: List<String>): List<String> {
        return Util.fullColor(
            list.map {
                it.replace("%material%", material.toString())
                    .replace("%material_formatted%", Util.formatMaterialName(material))
                    .replace("%sell_price%", sellPrice.toString())
            }
        )
    }

}