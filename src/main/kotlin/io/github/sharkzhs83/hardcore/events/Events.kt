package io.github.sharkzhs83.hardcore.events

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector

class Events : Listener {

    //크리퍼
    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        if(event.entity.type == EntityType.CREEPER) {
            val tnt = event.entity.world.spawn(event.location, TNTPrimed::class.java)
        }
        else if(event.entity.type == EntityType.TNT) {
            val creeper = event.entity.world.spawn(event.location, Creeper::class.java)
        }
    }

    //Entity 소환
    @EventHandler
    fun onSpawn(event: EntitySpawnEvent) {
        val config = Bukkit.getPluginManager().getPlugin("Hardcore")?.config
        val t = event.entity.type

        if(t == EntityType.ZOMBIE  || t == EntityType.SKELETON || t == EntityType.CREEPER || t == EntityType.SLIME || t == EntityType.SILVERFISH || t == EntityType.WITCH || t == EntityType.GUARDIAN || t == EntityType.ELDER_GUARDIAN || t == EntityType.STRAY || t == EntityType.ZOMBIE_VILLAGER || t == EntityType.HUSK || t == EntityType.DROWNED || t == EntityType.VINDICATOR || t == EntityType.EVOKER || t == EntityType.VEX || t == EntityType.PILLAGER || t == EntityType.RAVAGER || t == EntityType.PHANTOM || t == EntityType.ZOGLIN || t == EntityType.WARDEN || t == EntityType.BREEZE || t == EntityType.BOGGED || t == EntityType.CREAKING || t == EntityType.GHAST || t == EntityType.BLAZE || t == EntityType.MAGMA_CUBE || t == EntityType.WITHER_SKELETON || t == EntityType.PIGLIN || t == EntityType.PIGLIN_BRUTE || t == EntityType.HOGLIN || t == EntityType.SHULKER || t == EntityType.ENDERMITE || t == EntityType.ZOMBIFIED_PIGLIN || t == EntityType.ENDERMAN) {
            if(config?.get("Max Score") as Int >= 100) {
                (event.entity as LivingEntity).getAttribute(Attribute.MAX_HEALTH)!!.baseValue *= (1 + (config.get("Max Score") as Int / 100) * 0.1)
                (event.entity as LivingEntity).health *= (1 + (config.get("Max Score") as Int / 100) * 0.1)
                (event.entity as LivingEntity).getAttribute(Attribute.ATTACK_DAMAGE)!!.baseValue *= (1 + (config.get("Max Score") as Int / 100) * 0.1)
                (event.entity as LivingEntity).getAttribute(Attribute.MOVEMENT_SPEED)!!.baseValue *= (1 + (config.get("Max Score") as Int / 100) * 0.05)
                (event.entity as LivingEntity).getAttribute(Attribute.FOLLOW_RANGE)!!.baseValue *= (1 + (config.get("Max Score") as Int / 100) * 0.1)
            }
        }

        if(t == EntityType.DROWNED) {
            (event.entity as LivingEntity).equipment!!.setItemInMainHand(ItemStack(Material.TRIDENT))
        }

    }

    @EventHandler
    fun onLaunch(event: ProjectileLaunchEvent) {
        val shooter = event.entity.shooter as LivingEntity
        if(event.entity.type == EntityType.ARROW && shooter.type == EntityType.SKELETON) {
            event.entity.fireTicks = 100
            event.entity.velocity = Vector(event.entity.velocity.x * 3, event.entity.velocity.y * 3, event.entity.velocity.z * 3)
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if(event.entity.type == EntityType.ZOMBIE && event.cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
            event.damage = 0.0
        }
    }

    @EventHandler
    fun entityOnDamage(event: EntityDamageByEntityEvent) {
        if(event.entity.type == EntityType.PLAYER && event.damager.type == EntityType.SPIDER) {
            val player = event.entity as Player
            val entity = event.damager as LivingEntity

            player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 100, 5, false, false))
            player.addPotionEffect(PotionEffect(PotionEffectType.POISON, 100, 2, false, false))
            entity.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 100, 1))
        }
        else if(event.entity.type == EntityType.PLAYER && event.damager.type == EntityType.CAVE_SPIDER) {
            val player = event.entity as Player
            val entity = event.damager as LivingEntity

            player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 100, 5, false, false))
            player.addPotionEffect(PotionEffect(PotionEffectType.WITHER, 20, 1, false, false))
            entity.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 100, 1))
        }
        else if(event.entity.type == EntityType.PLAYER && event.damager.type == EntityType.TRIDENT) {
            event.damager.world.spawn(event.entity.location, LightningStrike::class.java)
        }
    }

    @EventHandler
    fun projectileOnGround(event: ProjectileHitEvent) {
        if(event.entity.type == EntityType.POTION && (event.entity.shooter as Entity).type == EntityType.WITCH) {
            val range = 1..5
            event.entity.world.createExplosion(event.entity.location, range.random().toFloat())
        }
    }
}