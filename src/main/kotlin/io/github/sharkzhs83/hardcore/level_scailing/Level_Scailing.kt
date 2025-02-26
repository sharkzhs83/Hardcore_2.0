package io.github.sharkzhs83.hardcore.level_scailing

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
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

    @EventHandler
    fun onSpawn(event: EntitySpawnEvent) {
        val config = Bukkit.getPluginManager().getPlugin("Hardcore")?.config
        val t = event.entity.type

        if(t == EntityType.ZOMBIE  || t == EntityType.SKELETON || t == EntityType.CREEPER || t == EntityType.SLIME || t == EntityType.SILVERFISH || t == EntityType.WITCH || t == EntityType.GUARDIAN || t == EntityType.ELDER_GUARDIAN || t == EntityType.STRAY || t == EntityType.ZOMBIE_VILLAGER || t == EntityType.HUSK || t == EntityType.DROWNED || t == EntityType.VINDICATOR || t == EntityType.EVOKER || t == EntityType.VEX || t == EntityType.PILLAGER || t == EntityType.RAVAGER || t == EntityType.PHANTOM || t == EntityType.ZOGLIN || t == EntityType.WARDEN || t == EntityType.BREEZE || t == EntityType.BOGGED || t == EntityType.CREAKING || t == EntityType.GHAST || t == EntityType.BLAZE || t == EntityType.MAGMA_CUBE || t == EntityType.WITHER_SKELETON || t == EntityType.PIGLIN || t == EntityType.PIGLIN_BRUTE || t == EntityType.HOGLIN || t == EntityType.SHULKER || t == EntityType.ENDERMITE || t == EntityType.ZOMBIFIED_PIGLIN || t == EntityType.ENDERMAN) {
            if(config?.get("Max Score") as Int >= 100) {
                (event.entity as LivingEntity).getAttribute(Attribute.MAX_HEALTH)!!.baseValue *= (1 + (config.get("Max Score") as Int / 100) * 0.1)
                (event.entity as LivingEntity).health *= (1 + (config.get("Max Score") as Int / 100) * 0.1)
                if(t != EntityType.GHAST ) {
                    (event.entity as LivingEntity).getAttribute(Attribute.ATTACK_DAMAGE)!!.baseValue *= (1 + (config.get("Max Score") as Int / 100) * 0.1)
                    (event.entity as LivingEntity).getAttribute(Attribute.MOVEMENT_SPEED)!!.baseValue *= (1 + (config.get("Max Score") as Int / 100) * 0.05)
                }
                (event.entity as LivingEntity).getAttribute(Attribute.FOLLOW_RANGE)!!.baseValue *= (1 + (config.get("Max Score") as Int / 100) * 0.1)
            }
        }
    }
}