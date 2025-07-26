package io.github.sharkzhs83.hardcore.player_level

import net.kyori.adventure.text.Component
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLevelChangeEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerLevelScaling(private val plugin: JavaPlugin) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        initPlayer(event.player)
        applyStats(event.player)
    }

    @EventHandler
    fun onLevelChange(event: PlayerLevelChangeEvent) {
        val diff = event.newLevel - event.oldLevel
        if (diff > 0) {
            val path = "players.${event.player.uniqueId}.points"
            plugin.config.set(path, plugin.config.getInt(path) + diff)
            plugin.saveConfig()
            event.player.sendMessage(Component.text("스탯 포인트 $diff 획득!"))
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        val defense = plugin.config.getInt("players.${player.uniqueId}.defense")
        if (defense > 0) {
            event.damage *= (1 - defense * 0.05).coerceAtLeast(0.0)
        }
    }

    @EventHandler
    fun onAttack(event: EntityDamageByEntityEvent) {
        val attacker: Player? = when (val damager = event.damager) {
            is Player -> damager
            is Projectile -> damager.shooter as? Player
            is TNTPrimed -> damager.source as? Player ?: damager.sourceEntity as? Player
            else -> null
        }
        attacker ?: return
        val uuid = attacker.uniqueId
        if (event.damager is Projectile) {
            val bonus = plugin.config.getInt("players.$uuid.ranged")
            event.damage += event.damage * bonus * 0.1
        } else if (event.damager is TNTPrimed) {
            val bonus = plugin.config.getInt("players.$uuid.explosive")
            event.damage += event.damage * bonus * 0.1
        }
    }

    private fun initPlayer(player: Player) {
        val uuid = player.uniqueId
        val cfg = plugin.config
        val base = "players.$uuid"
        if (!cfg.contains(base)) {
            cfg.set("$base.points", 0)
            cfg.set("$base.health", 0)
            cfg.set("$base.melee", 0)
            cfg.set("$base.ranged", 0)
            cfg.set("$base.explosive", 0)
            cfg.set("$base.speed", 0)
            cfg.set("$base.defense", 0)
            plugin.saveConfig()
        }
    }

    fun applyStats(player: Player) {
        applyStats(player, plugin.config)
    }

    companion object {
        fun applyStats(player: Player, config: FileConfiguration) {
            val base = "players.${player.uniqueId}"
            val health = config.getInt("$base.health")
            val melee = config.getInt("$base.melee")
            val speed = config.getInt("$base.speed")
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.let {
                it.baseValue = 20.0 + health * 2
                if (player.health > it.baseValue) player.health = it.baseValue
            }
            player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.let {
                it.baseValue = 1.0 + melee * 0.5
            }
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.let {
                it.baseValue = 0.1 + speed * 0.005
            }
        }
    }
}
