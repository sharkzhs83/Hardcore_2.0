package io.github.sharkzhs83.hardcore.arrows

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent

class Arrows : Listener{


    @EventHandler
    fun onLaunch(event: ProjectileLaunchEvent) {
        val shooter = event.entity.shooter as LivingEntity
        if(event.entity.type == EntityType.ARROW && shooter.type == EntityType.PLAYER && (shooter as Player).inventory.itemInOffHand.itemMeta?.displayName == "${ChatColor.RED}폭탄 화살") {
            (event.entity as Entity).isGlowing = true
            shooter.inventory.itemInOffHand.amount -= 1
        }
    }

    private fun spawnPlayerExplosion(loc: Location, player: Player) {
        val tnt = loc.world.spawn(loc, TNTPrimed::class.java)
        tnt.yield = 2f
        tnt.fuseTicks = 0
        tnt.source = player
        tnt.sourceEntity = player
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if(event.damager.type == EntityType.ARROW && event.damager.isGlowing) {
            val arrow = event.damager
            val shooter = (arrow as? org.bukkit.entity.Arrow)?.shooter as? Player ?: return
            spawnPlayerExplosion(event.entity.location, shooter)
            arrow.remove()
        }
    }

    @EventHandler
    fun onDamage(event: ProjectileHitEvent) {
        if(event.entity.type == EntityType.ARROW && event.entity.isGlowing) {
            val shooter = (event.entity as org.bukkit.entity.Arrow).shooter as? Player
            if (shooter != null) {
                spawnPlayerExplosion(event.entity.location, shooter)
            } else {
                event.entity.world.createExplosion(event.entity.location, 2f, true)
            }
            event.entity.remove()
        }
    }


}