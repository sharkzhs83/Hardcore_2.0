package io.github.sharkzhs83.hardcore.arrows

import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
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

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if(event.damager.type == EntityType.ARROW && event.damager.isGlowing) {
            event.entity.world.createExplosion(event.entity.location, 2f, true)
            event.damager.remove()
        }
    }

    @EventHandler
    fun onDamage(event: ProjectileHitEvent) {
        if(event.entity.type == EntityType.ARROW && event.entity.isGlowing) {
            event.entity.world.createExplosion(event.entity.location, 2f, true)
            event.entity.remove()
        }
    }


}