package io.github.sharkzhs83.hardcore.level_scailing

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerJoinEvent

class Level_Scailing : Listener{

    //config 초기화
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val config = Bukkit.getPluginManager().getPlugin("Hardcore")?.config

        if(config?.get("${event.player.name} score") == null || config.get("${event.player.name} score") == 0) {
            config?.set("${event.player.name} score", 0)
            Bukkit.getPluginManager().getPlugin("Hardcore")?.saveConfig()
        }
        if (config?.set("Max Score", 0) == null || config.get("Max Score") == 0) {
            config?.set("Max Score", 0)
            Bukkit.getPluginManager().getPlugin("Hardcore")?.saveConfig()
        }
    }



    @EventHandler
    fun onExp(event: PlayerExpChangeEvent) {

        val config = Bukkit.getPluginManager().getPlugin("Hardcore")?.config

        config?.set("${event.player.name} score", config.get("${event.player.name} score") as Int + event.amount)


        for (player1 in Bukkit.getOnlinePlayers()) {
            if(config?.get("${player1.name} score") as Int >= config.get("Max Score") as Int) {
                config.set("Max Score", config.get("${player1.name} score") as Int)
            }
        }
        Bukkit.getPluginManager().getPlugin("Hardcore")?.saveConfig()
    }

}