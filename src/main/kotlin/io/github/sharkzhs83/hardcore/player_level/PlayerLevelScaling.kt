package io.github.sharkzhs83.hardcore.player_level

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.attribute.Attribute
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerLevelScaling(private val plugin: JavaPlugin) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        initPlayer(event.player)
        applyStats(event.player)
        updateTab(event.player)
    }

    @EventHandler
    fun onMobKill(event: EntityDeathEvent) {
        val killer = event.entity.killer ?: return
        val xp = xpFor(event.entity.type)
        addXp(killer, xp)
    }

    private fun xpFor(type: EntityType): Int = when (type) {
        EntityType.ZOMBIE -> 10
        EntityType.SKELETON -> 12
        EntityType.CREEPER -> 15
        else -> 5
    }

    private fun addXp(player: Player, amount: Int) {
        val base = "players.${player.uniqueId}"
        val cfg = plugin.config
        var xp = cfg.getInt("$base.xp")
        var level = cfg.getInt("$base.level")
        val cap = cfg.getInt("$base.cap", 5)
        xp += amount
        val needed = 100
        var leveled = 0
        while (xp >= needed && level < cap) {
            xp -= needed
            level += 1
            leveled += 1
        }
        if (level >= cap) {
            xp = xp.coerceAtMost(needed - 1)
        }
        if (leveled > 0) {
            val pointsPath = "$base.points"
            cfg.set(pointsPath, cfg.getInt(pointsPath) + leveled * 2)
            player.sendMessage(
                Component.text("레벨 $level 달성! 스탯 포인트 ${leveled * 2} 획득!")
                    .color(NamedTextColor.GOLD)
            )
            val maxPath = "Max Level"
            if (level > cfg.getInt(maxPath)) {
                cfg.set(maxPath, level)
            }
        }
        cfg.set("$base.xp", xp)
        cfg.set("$base.level", level)
        plugin.saveConfig()
        updateTab(player)
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
            is TNTPrimed -> damager.source as? Player ?: damager.source as? Player
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
            cfg.set("$base.level", 1)
            cfg.set("$base.xp", 0)
            cfg.set("$base.cap", 5)
            plugin.saveConfig()
        }
    }

    fun applyStats(player: Player) {
        applyStats(player, plugin.config)
    }

    private fun updateTab(player: Player) {
        val uuid = player.uniqueId
        val cfg = plugin.config
        val base = "players.$uuid"
        val level = cfg.getInt("$base.level")
        val xp = cfg.getInt("$base.xp")
        player.playerListName(
            Component.text(player.name, NamedTextColor.WHITE)
                .append(Component.text(" [", NamedTextColor.DARK_GRAY))
                .append(Component.text("레벨 $level", NamedTextColor.GOLD))
                .append(Component.text(" XP $xp", NamedTextColor.AQUA))
                .append(Component.text("]", NamedTextColor.DARK_GRAY))
        )
    }

    companion object {
        fun applyStats(player: Player, config: FileConfiguration) {
            val base = "players.${player.uniqueId}"
            val health = config.getInt("$base.health")
            val melee = config.getInt("$base.melee")
            val speed = config.getInt("$base.speed")
            player.getAttribute(Attribute.MAX_HEALTH)?.let {
                it.baseValue = 20.0 + health * 2
                if (player.health > it.baseValue) player.health = it.baseValue
            }
            player.getAttribute(Attribute.ATTACK_DAMAGE)?.let {
                it.baseValue = 1.0 + melee * 0.5
            }
            player.getAttribute(Attribute.MOVEMENT_SPEED)?.let {
                it.baseValue = 0.1 + speed * 0.005
            }
        }
    }
}
