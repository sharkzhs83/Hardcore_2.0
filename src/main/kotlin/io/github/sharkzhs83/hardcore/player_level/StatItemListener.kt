package io.github.sharkzhs83.hardcore.player_level

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor
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
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.Sound

class StatItemListener(private val plugin: JavaPlugin) : Listener {
    private val itemKey = NamespacedKey(plugin, "stat_up_item")
    private val resetKey = NamespacedKey(plugin, "stat_reset_item")
    private val capKey = NamespacedKey(plugin, "level_cap_item")
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

    fun createResetRecipe(): ShapedRecipe {
        val item = createResetItem()
        val key = NamespacedKey(plugin, "stat_reset")
        val recipe = ShapedRecipe(key, item)
        recipe.shape(" D ", "DED", " D ")
        recipe.setIngredient('D', Material.DIAMOND)
        recipe.setIngredient('E', Material.EMERALD)
        return recipe
    }

    fun createCapRecipe(): ShapedRecipe {
        val item = createCapItem()
        val key = NamespacedKey(plugin, "level_cap")
        val recipe = ShapedRecipe(key, item)
        recipe.shape(" E ", "EDE", " E ")
        recipe.setIngredient('E', Material.EMERALD)
        recipe.setIngredient('D', Material.DIAMOND)
        return recipe
    }

    private fun createStatItem(): ItemStack {
        val item = ItemStack(Material.BOOK)
        val meta = item.itemMeta
        meta.setDisplayName("${ChatColor.GOLD}비싼 책")
        meta.persistentDataContainer.set(itemKey, PersistentDataType.BYTE, 1)
        item.itemMeta = meta
        return item
    }

    private fun createResetItem(): ItemStack {
        val item = ItemStack(Material.EMERALD)
        val meta = item.itemMeta
        meta.setDisplayName("${ChatColor.AQUA}스탯 초기화석")
        meta.persistentDataContainer.set(resetKey, PersistentDataType.BYTE, 1)
        item.itemMeta = meta
        return item
    }

    private fun createCapItem(): ItemStack {
        val item = ItemStack(Material.DIAMOND)
        val meta = item.itemMeta
        meta.setDisplayName("${ChatColor.LIGHT_PURPLE}레벨 상한석")
        meta.persistentDataContainer.set(capKey, PersistentDataType.BYTE, 1)
        item.itemMeta = meta
        return item
    }

    private fun statOption(key: String, display: String, mat: Material): ItemStack {
        val item = ItemStack(mat)
        val meta = item.itemMeta
        meta.setDisplayName(display)
        meta.persistentDataContainer.set(typeKey, PersistentDataType.STRING, key)
        item.itemMeta = meta
        return item
    }

    private fun openGui(player: Player) {
        val inv = Bukkit.createInventory(holder, 9, Component.text("스탯 강화"))
        inv.setItem(0, statOption("health", "${ChatColor.RED}체력", Material.RED_DYE))
        inv.setItem(1, statOption("melee", "${ChatColor.DARK_RED}근접", Material.IRON_SWORD))
        inv.setItem(2, statOption("ranged", "${ChatColor.AQUA}원거리", Material.BOW))
        inv.setItem(3, statOption("explosive", "${ChatColor.GOLD}폭발", Material.TNT))
        inv.setItem(4, statOption("speed", "${ChatColor.GREEN}스피드", Material.SUGAR))
        inv.setItem(5, statOption("defense", "${ChatColor.BLUE}방어력", Material.SHIELD))
        player.openInventory(inv)
        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
    }

    private fun resetStats(player: Player) {
        val uuid = player.uniqueId
        val cfg = plugin.config
        val base = "players.$uuid"
        val stats = listOf("health", "melee", "ranged", "explosive", "speed", "defense")
        var spent = 0
        for (s in stats) {
            val value = cfg.getInt("$base.$s")
            spent += value
            cfg.set("$base.$s", 0)
        }
        cfg.set("$base.points", cfg.getInt("$base.points") + spent)
        plugin.saveConfig()
        PlayerLevelScaling.applyStats(player, plugin)
        player.sendMessage(
            Component.text("스탯이 초기화되었습니다.").color(NamedTextColor.GOLD)
        )
    }

    private fun raiseCap(player: Player) {
        val uuid = player.uniqueId
        val base = "players.$uuid"
        val cfg = plugin.config
        var cap = cfg.getInt("$base.cap", 5)
        if (cap >= 30) {
            player.sendMessage(
                Component.text("이미 최고 레벨입니다.").color(NamedTextColor.RED)
            )
            return
        }
        cap = (cap + 5).coerceAtMost(30)
        cfg.set("$base.cap", cap)
        plugin.saveConfig()
        player.sendMessage(
            Component.text("최대 레벨이 $cap 로 증가했습니다!").color(NamedTextColor.AQUA)
        )
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return
        val item = event.item ?: return
        val meta = item.itemMeta ?: return
        event.isCancelled = true
        val pdc = meta.persistentDataContainer
        when {
            pdc.has(itemKey, PersistentDataType.BYTE) -> {
                openGui(event.player)
            }
            pdc.has(resetKey, PersistentDataType.BYTE) -> {
                resetStats(event.player)
                item.amount = item.amount - 1
            }
            pdc.has(capKey, PersistentDataType.BYTE) -> {
                raiseCap(event.player)
                item.amount = item.amount - 1
            }
            else -> event.isCancelled = false
        }
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.view.topInventory.holder !== holder) return
        event.isCancelled = true
        val player = event.whoClicked as? Player ?: return
        val clicked = event.currentItem ?: return
        val meta = clicked.itemMeta ?: return
        val stat = meta.persistentDataContainer.get(typeKey, PersistentDataType.STRING) ?: return
        val display = ChatColor.stripColor(meta.displayName ?: stat)
        val uuid = player.uniqueId
        val cfg = plugin.config
        val pointsPath = "players.$uuid.points"
        val points = cfg.getInt(pointsPath)
        if (points <= 0) {
            player.sendMessage(
                Component.text("포인트가 부족합니다.")
                    .color(NamedTextColor.RED)
            )
            return
        }
        cfg.set(pointsPath, points - 1)
        val statPath = "players.$uuid.$stat"
        cfg.set(statPath, cfg.getInt(statPath) + 1)
        plugin.saveConfig()
        PlayerLevelScaling.applyStats(player, plugin)
        player.sendMessage(
            Component.text("$display 스탯이 1 증가했습니다.")
                .color(NamedTextColor.GREEN)
        )
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
    }
}
