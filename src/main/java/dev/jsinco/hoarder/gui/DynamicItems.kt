package dev.jsinco.hoarder.gui

import dev.jsinco.hoarder.Hoarder
import dev.jsinco.hoarder.HoarderEvent
import dev.jsinco.hoarder.utilities.Util
import dev.jsinco.hoarder.gui.enums.GUIType
import dev.jsinco.hoarder.manager.FileManager
import dev.jsinco.hoarder.manager.Settings
import dev.jsinco.hoarder.objects.HoarderPlayer
import dev.jsinco.hoarder.objects.Time
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable

class DynamicItems(val guiCreator: GUICreator) {

    companion object {
        private val plugin: Hoarder = Hoarder.getInstance()
    }

    private val material: Material = HoarderEvent.activeMaterial
    private val sellPrice: Double = HoarderEvent.activeSellPrice
    private val gui = guiCreator.gui
    private val dItemsFile = FileManager("guis/dynamicitems.yml").generateYamlFile()

    // TODO: probably redo/edit this
    fun setGuiSpecifics(player: Player) {

        when (guiCreator.guiType) {
            GUIType.MAIN -> { // FIXME: items need to set placeholders
                // We can use our GuiItem class for these dynamic items
                // TODO: Add runnable for clock

                val activeItem = ItemStack(Material.valueOf(setMainGUIStrings(dItemsFile.getString("items.active_item.material")!!)))

                val activeMeta = activeItem.itemMeta!!

                activeMeta.setDisplayName(setMainGUIStrings(dItemsFile.getString("items.active_item.name")!!))
                activeMeta.lore = setMainGUIStrings(dItemsFile.getStringList("items.active_item.lore"))
                if (dItemsFile.getBoolean("items.active_item.enchanted")) {
                    activeMeta.addEnchant(Enchantment.DURABILITY, 1, true)
                    activeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                }
                activeMeta.persistentDataContainer.set(NamespacedKey(plugin, "action") , PersistentDataType.STRING, dItemsFile.getString("items.active_item.action") ?: "NONE")
                activeItem.itemMeta = activeMeta
                gui.setItem(dItemsFile.getInt("items.active_item.slot"), activeItem)
                startClockRunnable() // FIXME
            }

            GUIType.TREASURE -> {
                val treasureItems = Settings.getDataManger().getAllTreasureItems() ?: return

                val items: MutableList<ItemStack> = mutableListOf()

                for (treasureItem in treasureItems) {
                    val item = treasureItem.itemStack
                    val meta = item.itemMeta!!

                    val lore = meta.lore ?: emptyList<String?>().toMutableList()
                    for (string in dItemsFile.getStringList("items.treasure.lore")) {
                        lore.add(Util.fullColor(string.replace("%weight%", treasureItem.weight.toString())))
                    }
                    meta.lore = lore
                    item.itemMeta = meta
                    items.add(item)
                }

                guiCreator.paginatedGUI = PaginatedGUI(guiCreator.title, gui, items)
            }

            GUIType.STATS -> {
                val hoarderPlayerUUIDS = Util.getEventPlayersByTop().keys

                val playerHeads: MutableList<ItemStack> = mutableListOf()

                for (uuid in hoarderPlayerUUIDS) {
                    val hoarderPlayer = HoarderPlayer(uuid)

                    val item = ItemStack(Material.valueOf(dItemsFile.getString("items.stats.material")!!.uppercase()))
                    val meta = item.itemMeta!!

                    meta.setDisplayName(
                        Util.fullColor(dItemsFile.getString("items.stats.name")!!
                        .replace("%name%", hoarderPlayer.getName())
                        .replace("%points%", hoarderPlayer.getPoints().toString())
                        .replace("%position%", (hoarderPlayerUUIDS.indexOf(uuid) + 1).toString()))
                    )

                    meta.lore = Util.fullColor(dItemsFile.getStringList("items.stats.lore").map {
                        it.replace("%name%", hoarderPlayer.getName())
                            .replace("%points%", hoarderPlayer.getPoints().toString())
                            .replace("%position%", (hoarderPlayerUUIDS.indexOf(uuid) + 1).toString())
                    })
                    if (dItemsFile.getBoolean("items.stats.enchanted")) meta.addEnchant(Enchantment.DURABILITY, 1, true)

                    item.itemMeta = meta
                    playerHeads.add(GUIItem.setPlayerHead(item, uuid))
                }
                guiCreator.paginatedGUI = PaginatedGUI(guiCreator.title, gui, playerHeads)
            }
            GUIType.TREASURE_CLAIM -> {
                val materialMatchString = dItemsFile.getString("items.treasure.string-matched-materials") ?: "CONCRETE_POWDER"
                val materials: MutableList<Material> = mutableListOf()

                for (material in Material.entries) {
                    if (material.name.contains(materialMatchString)) materials.add(material)
                }

                val amt = Settings.getDataManger().getClaimableTreasures(player.uniqueId.toString())

                val items: MutableList<ItemStack> = mutableListOf()

                for (i in 0 until amt) {
                    val item = createItem(materials.random(), dItemsFile.getString("items.treasure_claim.name") ?: "", dItemsFile.getStringList("items.treasure_claim.lore"), dItemsFile.getBoolean("items.treasure_claim.enchanted"), dItemsFile.getString("items.treasure_claim.action") ?: "NONE")
                    items.add(item)
                }
                guiCreator.paginatedGUI = PaginatedGUI(guiCreator.title, gui, items)
            }
            GUIType.OTHER -> {}
        }
    }

    fun createItem(material: Material, name: String, lore: List<String>, enchanted: Boolean, action:String): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta!!

        meta.setDisplayName(Util.fullColor(name))
        meta.lore = Util.fullColor(lore)
        if (enchanted) meta.addEnchant(Enchantment.DURABILITY, 1, true)

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
        meta.persistentDataContainer.set(NamespacedKey(plugin, "action"), PersistentDataType.STRING, action)

        item.itemMeta = meta
        return item
    }


    /**
     * Start the clock runnable for main GUI
     * Expire time 5m
     */
    private fun startClockRunnable() {
        val item = ItemStack(Material.valueOf(dItemsFile.getString("items.clock.material") ?: "CLOCK"))
        val meta = item.itemMeta!!

        guiCreator.guiRunnable = object : BukkitRunnable() {
            override fun run() {
                meta.setDisplayName(setMainGUIStrings(dItemsFile.getString("items.clock.name") ?: ""))

                meta.lore = dItemsFile.getStringList("items.clock.lore").map { setClockStrings(it) }
                item.itemMeta = meta
                gui.setItem(dItemsFile.getInt("items.clock.slot"), item)

            }
        }.runTaskTimer(plugin, 0, 20).taskId
    }

    private fun setClockStrings(string: String): String {
        val time = Time()
        return Util.fullColor(
            string.replace("%hours%", time.hours.toString())
                .replace("%minutes%", time.mins.toString())
                .replace("%seconds%", time.secs.toString())
        )
    }


    private fun setMainGUIStrings(string: String): String {
        return Util.fullColor(
            string.replace("%material%", material.toString())
                .replace("%material_formatted%", Util.formatMaterialName(material))
                .replace("%sell_price%", Util.formatEconAmt(sellPrice))
        )
    }

    private fun setMainGUIStrings(list: List<String>): List<String> {
        return Util.fullColor(
            list.map {
                it.replace("%material%", material.toString())
                    .replace("%material_formatted%", Util.formatMaterialName(material))
                    .replace("%sell_price%", Util.formatEconAmt(sellPrice))
            }
        )
    }
}