package io.github.sharkzhs83.hardcore.events

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.type.TNT
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.EntityTargetEvent
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

        val entity = event.entity

        if(entity.type == EntityType.CREEPER) {
            val tnt = event.entity.world.spawn(event.location, TNTPrimed::class.java)
        }
        else if(entity.type == EntityType.TNT && (entity as TNTPrimed).source is Player) {
            val creeper = event.entity.world.spawn(event.location, Creeper::class.java)
        }
    }

    //가디언
    @EventHandler
    fun onGuardianAim(event: EntityTargetEvent) {
        if(event.entity.type == EntityType.GUARDIAN && event.target?.type == EntityType.PLAYER) {
            (event.target as Player).addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 100, 1, true, true))
        }
    }

    //소환사
    @EventHandler
    fun onEvokerAim(event: EntityTargetEvent) {
        if(event.entity.type == EntityType.EVOKER && event.target?.type == EntityType.PLAYER) {
            (event.target as Player).addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 100, 1, true, true))
        }
    }


    @EventHandler
    //Entity 소환
    fun onSpawn(event: EntitySpawnEvent) {
        val config = Bukkit.getPluginManager().getPlugin("Hardcore")?.config
        val t = event.entity.type
        val level = config?.getInt("Max Level") ?: 1

        if(t == EntityType.DROWNED) {
            (event.entity as LivingEntity).equipment!!.setItemInMainHand(ItemStack(Material.TRIDENT))
        }
        else if(t == EntityType.VINDICATOR) {

            if(level in 5..7) {
                val axe = ItemStack(Material.DIAMOND_AXE)

                (event.entity as LivingEntity).equipment!!.setItemInMainHand(axe)
            }
            else if(level in 8..9) {
                val axe = ItemStack(Material.DIAMOND_AXE)
                axe.addEnchantment(Enchantment.SHARPNESS, 1)

                (event.entity as LivingEntity).equipment!!.setItemInMainHand(axe)
            }
            else if(level in 10..12) {
                val axe = ItemStack(Material.DIAMOND_AXE)
                axe.addEnchantment(Enchantment.SHARPNESS, 2)

                (event.entity as LivingEntity).equipment!!.setItemInMainHand(axe)
            }
            else if(level in 13..14) {
                val axe = ItemStack(Material.NETHERITE_AXE)

                (event.entity as LivingEntity).equipment!!.setItemInMainHand(axe)
            }
            else if(level in 15..17) {
                val axe = ItemStack(Material.NETHERITE_AXE)
                axe.addEnchantment(Enchantment.SHARPNESS, 1)

                (event.entity as LivingEntity).equipment!!.setItemInMainHand(axe)
            }
            else if(level in 18..20) {
                val axe = ItemStack(Material.NETHERITE_AXE)
                axe.addEnchantment(Enchantment.SHARPNESS, 2)

                (event.entity as LivingEntity).equipment!!.setItemInMainHand(axe)
            }
            else if(level > 20) {
                val axe = ItemStack(Material.NETHERITE_AXE)
                axe.addEnchantment(Enchantment.SHARPNESS, 3)

                (event.entity as LivingEntity).equipment!!.setItemInMainHand(axe)
            }
        }
        else if(t == EntityType.PILLAGER) {
            if(level in 5..7) {
                val crossbow = ItemStack(Material.CROSSBOW)

                (event.entity as LivingEntity).equipment!!.setItemInMainHand(crossbow)
            }
            else if(level in 8..9) {
                val crossbow = ItemStack(Material.CROSSBOW)
                crossbow.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 1)
                crossbow.addUnsafeEnchantment(Enchantment.MULTISHOT, 1)

                (event.entity as LivingEntity).equipment!!.setItemInMainHand(crossbow)
            }
            else if(level in 10..12) {
                val crossbow = ItemStack(Material.CROSSBOW)
                crossbow.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 2)
                crossbow.addUnsafeEnchantment(Enchantment.MULTISHOT, 1)

                (event.entity as LivingEntity).equipment!!.setItemInMainHand(crossbow)
            }
            else if(level in 13..14) {
                val crossbow = ItemStack(Material.CROSSBOW)
                crossbow.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 3)
                crossbow.addUnsafeEnchantment(Enchantment.MULTISHOT, 1)

                (event.entity as LivingEntity).equipment!!.setItemInMainHand(crossbow)
            }
            else if(level in 15..17) {
                val crossbow = ItemStack(Material.CROSSBOW)
                crossbow.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 4)
                crossbow.addUnsafeEnchantment(Enchantment.MULTISHOT, 2)

                (event.entity as LivingEntity).equipment!!.setItemInMainHand(crossbow)
            }
            else if(level in 18..20) {
                val crossbow = ItemStack(Material.CROSSBOW)
                crossbow.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 5)
                crossbow.addUnsafeEnchantment(Enchantment.MULTISHOT, 3)

                (event.entity as LivingEntity).equipment!!.setItemInMainHand(crossbow)
            }
            else if(level > 20) {
                val crossbow = ItemStack(Material.CROSSBOW)
                crossbow.addUnsafeEnchantment(Enchantment.QUICK_CHARGE, 6)
                crossbow.addUnsafeEnchantment(Enchantment.MULTISHOT, 4)

                (event.entity as LivingEntity).equipment!!.setItemInMainHand(crossbow)
            }
        }
    }

    @EventHandler
    fun onLaunch(event: ProjectileLaunchEvent) {
        val shooter = event.entity.shooter
        val config = Bukkit.getPluginManager().getPlugin("Hardcore")?.config
        val level = config?.getInt("Max Level") ?: 1

        if(event.entity.type == EntityType.ARROW && (shooter as LivingEntity).type == EntityType.SKELETON) {
            event.entity.fireTicks = 100
            val scale = 1 + (level - 1) * 0.1
            event.entity.velocity = Vector(
                event.entity.velocity.x * scale,
                event.entity.velocity.y * scale,
                event.entity.velocity.z * scale
            )
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

        val config = Bukkit.getPluginManager().getPlugin("Hardcore")?.config

        if(event.entity.type == EntityType.PLAYER && event.damager.type == EntityType.SPIDER) {
            val player = event.entity as Player
            val entity = event.damager as LivingEntity

            player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 100, 5, true, false))
            player.addPotionEffect(PotionEffect(PotionEffectType.POISON, 100, 2, true, false))
            entity.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 100, 1))
        }
        else if(event.entity.type == EntityType.PLAYER && event.damager.type == EntityType.CAVE_SPIDER) {
            val player = event.entity as Player
            val entity = event.damager as LivingEntity

            player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 100, 5, true, false))
            player.addPotionEffect(PotionEffect(PotionEffectType.WITHER, 60, 1, true, false))
            entity.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 100, 1))
        }
        else if(event.entity.type == EntityType.PLAYER && (event.damager.type == EntityType.ZOMBIE || event.damager.type == EntityType.ZOMBIE_VILLAGER || event.damager.type == EntityType.HUSK)) {
            //방패로 막았을 때
            if(event.finalDamage != 0.0) {
                val player = event.entity as Player
                val inventory = player.inventory
                val items = inventory.contents.filterNotNull()

                if (items.isNotEmpty()) {
                    val randomItem = items.random()
                    if(!randomItem.containsEnchantment(Enchantment.BINDING_CURSE)) {
                        val dropItem = randomItem.clone()
                        dropItem.amount = 1

                        randomItem.amount -= 1

                        if (randomItem.amount <= 0) {
                            inventory.remove(randomItem)
                        }

                        player.world.dropItem(player.location, dropItem)
                    }
                }
            }
        }
        else if(event.entity.type == EntityType.PLAYER && event.damager.type == EntityType.SILVERFISH) {
            event.entity.world.spawn(event.entity.location, Silverfish::class.java)
            event.entity.world.spawnParticle(org.bukkit.Particle.WITCH, event.entity.location, 10)
        }
        else if(event.entity.type == EntityType.SLIME && event.damager.type == EntityType.PLAYER) {
            //한방이 안날 시 작은 슬라임 소환
            if(event.finalDamage < (event.entity as LivingEntity).health) {
                val small_slime = event.entity.world.spawn(event.entity.location, Slime::class.java)
                small_slime.size = 1
                small_slime.health = 1.0
                event.entity.world.spawnParticle(org.bukkit.Particle.ITEM_SLIME, event.entity.location, 10)
            }
        }
        else if(event.entity.type == EntityType.PLAYER && event.damager.type == EntityType.ENDERMAN) {
            if(event.finalDamage != 0.0) {
                event.entity.teleport(Location(event.entity.world,event.entity.location.x, event.entity.location.y + 3.5, event.entity.location.z))
            }
        }
        else if(event.entity.type == EntityType.PLAYER && event.damager.type == EntityType.ZOMBIFIED_PIGLIN) {
            if(event.finalDamage != 0.0) {
                (event.entity as Player).foodLevel -= 1
            }
        }
        else if(event.entity.type == EntityType.GHAST && event.damager.type == EntityType.FIREBALL) {
            event.isCancelled = true
        }
        else if(event.entity.type == EntityType.PLAYER && event.damager.type == EntityType.TRIDENT) {
            event.damager.world.spawn(event.entity.location, LightningStrike::class.java)
        }
    }

    @EventHandler
    fun projectileOnHit(event: ProjectileHitEvent) {
        val config = Bukkit.getPluginManager().getPlugin("Hardcore")?.config
        val level = config?.getInt("Max Level") ?: 1
        if(event.entity.type == EntityType.POTION && (event.entity.shooter as Entity).type == EntityType.WITCH) {
            val range = 1..5
            event.entity.world.createExplosion(event.entity.location, range.random().toFloat())
        }
        else if(event.entity.type == EntityType.ARROW && (event.entity.shooter as Entity).type == EntityType.PILLAGER) {
            val power = 1 + (level - 1) * 0.02
            event.entity.world.createExplosion(event.entity.location, power.toFloat())
            event.entity.remove()
        }
    }


}