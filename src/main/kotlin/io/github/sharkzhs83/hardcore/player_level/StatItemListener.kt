package io.github.sharkzhs83.hardcore.player_level

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

class StatItemListener(private val plugin: JavaPlugin) : Listener {
    private val itemKey = NamespacedKey(plugin, "stat_up_item")
    private val typeKey = NamespacedKey(plugin, "stat_type")
    private val holder = object : InventoryHolder {
        private val inv = Bukkit.createInventory(this, 9, Component.text("스탯 강화"))
        override fun getInventory(): Inventory = inv
    }

    fun createRecipe(): ShapedRecipe {
        val item = createStatItem()
        val key = NamespacedKey(plugin, "stat_tome")
        val recipe = ShapedRecipe(key, item)
        recipe.shape(" D ", "DBD", " D ")
        recipe.setIngredient('D', Material.DIAMOND)
        recipe.setIngredient('B', Material.BOOK)
        return recipe
    }

    private fun createStatItem(): ItemStack {
        val item = ItemStack(Material.BOOK)
        val meta = item.itemMeta
        meta.setDisplayName("Stat Tome")
        meta.persistentDataContainer.set(itemKey, PersistentDataType.BYTE, 1)
        item.itemMeta = meta
        return item
    }

    private fun statOption(name: String, mat: Material): ItemStack {
        val item = ItemStack(mat)
        val meta = item.itemMeta
        meta.setDisplayName(name)
        meta.persistentDataContainer.set(typeKey, PersistentDataType.STRING, name)
        item.itemMeta = meta
        return item
    }

    private fun openGui(player: Player) {
        val inv = Bukkit.createInventory(holder, 9, Component.text("스탯 강화"))
        inv.setItem(0, statOption("health", Material.RED_DYE))
        inv.setItem(1, statOption("melee", Material.IRON_SWORD))
        inv.setItem(2, statOption("ranged", Material.BOW))
        inv.setItem(3, statOption("explosive", Material.TNT))
        inv.setItem(4, statOption("speed", Material.SUGAR))
        inv.setItem(5, statOption("defense", Material.SHIELD))
        player.openInventory(inv)
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return
        val item = event.item ?: return
        val meta = item.itemMeta ?: return
        if (!meta.persistentDataContainer.has(itemKey, PersistentDataType.BYTE)) return
        event.isCancelled = true
        openGui(event.player)
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.view.topInventory.holder !== holder) return
        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return
        val clicked = event.currentItem ?: return
        val meta = clicked.itemMeta ?: return
        val stat = meta.persistentDataContainer.get(typeKey, PersistentDataType.STRING) ?: return
        val uuid = player.uniqueId
        val cfg = plugin.config
        val pointsPath = "players.$uuid.points"
        val points = cfg.getInt(pointsPath)
        if (points <= 0) {
            player.sendMessage(Component.text("포인트가 부족합니다."))
            return
        }
        cfg.set(pointsPath, points - 1)
        val statPath = "players.$uuid.$stat"
        cfg.set(statPath, cfg.getInt(statPath) + 1)
        plugin.saveConfig()
        PlayerLevelScaling.applyStats(player, plugin)
        player.sendMessage(Component.text("$stat 스탯이 1 증가했습니다."))
        player.closeInventory()
    }
}
