package io.github.sharkzhs83.hardcore

import io.github.sharkzhs83.hardcore.arrows.Arrows
import io.github.sharkzhs83.hardcore.events.Events
import io.github.sharkzhs83.hardcore.level_scailing.Level_Scailing
import io.github.sharkzhs83.hardcore.player_level.PlayerLevelScaling
import io.github.sharkzhs83.hardcore.player_level.PlayerStatsCommand
import io.github.sharkzhs83.hardcore.player_level.StatItemListener
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin

class Hardcore : JavaPlugin() , Listener{

    override fun onEnable() {
        logger.info("HARDCORE has enabled")
        server.pluginManager.registerEvents(Events(), this)
        server.pluginManager.registerEvents(Level_Scailing(), this)
        server.pluginManager.registerEvents(PlayerLevelScaling(this), this)
        val statItemListener = StatItemListener(this)
        server.pluginManager.registerEvents(statItemListener, this)
        server.addRecipe(statItemListener.createRecipe())
        getCommand("stats")?.setExecutor(PlayerStatsCommand(this))
        server.pluginManager.registerEvents(Arrows(), this)
        saveDefaultConfig()

        //폭탄 화살 조합

        val bombArrow1 = ItemStack(Material.ARROW, 4)
        val bombMeta = bombArrow1.itemMeta
        bombMeta.setDisplayName("${ChatColor.RED}폭탄 화살")
        bombArrow1.itemMeta = bombMeta

        val bombKey = NamespacedKey(this, "bomb_arrow_LV1")
        val bombRecipe = ShapedRecipe(bombKey, bombArrow1)
        bombRecipe.shape(" A ", "AGA", " A ")
        bombRecipe.setIngredient('A', Material.ARROW)
        bombRecipe.setIngredient('G', Material.GUNPOWDER)
        server.addRecipe(bombRecipe)

    }


    override fun onDisable() {
        logger.info("HARDCORE has disabled")
        saveConfig()
    }
}
