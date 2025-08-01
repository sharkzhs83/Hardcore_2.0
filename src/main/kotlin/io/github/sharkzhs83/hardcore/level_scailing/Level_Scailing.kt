package io.github.sharkzhs83.hardcore.level_scailing

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerJoinEvent

class Level_Scailing : Listener{

    //config 초기화
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val config = Bukkit.getPluginManager().getPlugin("Hardcore")?.config

        if(config?.get("Max Level") == null || config.getInt("Max Level") == 0){
            config?.set("Max Level", 1)
            Bukkit.getPluginManager().getPlugin("Hardcore")?.saveConfig()
        }
        val lvl = config?.getInt("players.${event.player.uniqueId}.level") ?: 1
        val currentMax = config?.getInt("Max Level") ?: 1
        if(lvl > currentMax){
            config?.set("Max Level", lvl)
            Bukkit.getPluginManager().getPlugin("Hardcore")?.saveConfig()
        }
    }

    @EventHandler
    fun onSpawn(event: EntitySpawnEvent) {
        val config = Bukkit.getPluginManager().getPlugin("Hardcore")?.config
        val t = event.entity.type

        if(t == EntityType.ZOMBIE  || t == EntityType.SKELETON || t == EntityType.CREEPER || t == EntityType.SLIME || t == EntityType.SILVERFISH || t == EntityType.WITCH || t == EntityType.GUARDIAN || t == EntityType.ELDER_GUARDIAN || t == EntityType.STRAY || t == EntityType.ZOMBIE_VILLAGER || t == EntityType.HUSK || t == EntityType.DROWNED || t == EntityType.VINDICATOR || t == EntityType.EVOKER || t == EntityType.VEX || t == EntityType.PILLAGER || t == EntityType.RAVAGER || t == EntityType.PHANTOM || t == EntityType.ZOGLIN || t == EntityType.WARDEN || t == EntityType.BREEZE || t == EntityType.BOGGED || t == EntityType.CREAKING || t == EntityType.GHAST || t == EntityType.BLAZE || t == EntityType.MAGMA_CUBE || t == EntityType.WITHER_SKELETON || t == EntityType.PIGLIN || t == EntityType.PIGLIN_BRUTE || t == EntityType.HOGLIN || t == EntityType.SHULKER || t == EntityType.ENDERMITE || t == EntityType.ZOMBIFIED_PIGLIN || t == EntityType.ENDERMAN) {
            val max = config?.getInt("Max Level") ?: 1
            if(max > 1) {
                val scale = 1 + (max - 1) * 0.1
                (event.entity as LivingEntity).getAttribute(Attribute.MAX_HEALTH)!!.baseValue *= scale
                (event.entity as LivingEntity).health *= scale
                if(t != EntityType.GHAST ) {
                    (event.entity as LivingEntity).getAttribute(Attribute.ATTACK_DAMAGE)!!.baseValue *= scale
                    (event.entity as LivingEntity).getAttribute(Attribute.MOVEMENT_SPEED)!!.baseValue *= 1 + (max - 1) * 0.05
                }
                (event.entity as LivingEntity).getAttribute(Attribute.FOLLOW_RANGE)!!.baseValue *= scale
            }

            val (color, descriptor) = when {
                max <= 5 -> NamedTextColor.GREEN to "약한"
                max <= 10 -> NamedTextColor.YELLOW to "보통"
                max <= 15 -> NamedTextColor.GOLD to "강한"
                else -> NamedTextColor.RED to "매우 강한"
            }
            val name = "${descriptor} ${koreanName(t)}"
            (event.entity as LivingEntity).customName(Component.text(name).color(color))
            (event.entity as LivingEntity).isCustomNameVisible = true
        }
    }

    private fun koreanName(type: EntityType): String = when (type) {
        EntityType.ZOMBIE -> "좀비"
        EntityType.SKELETON -> "스켈레톤"
        EntityType.CREEPER -> "크리퍼"
        EntityType.SLIME -> "슬라임"
        EntityType.SILVERFISH -> "실버피쉬"
        EntityType.WITCH -> "마녀"
        EntityType.GUARDIAN -> "가디언"
        EntityType.ELDER_GUARDIAN -> "엘더 가디언"
        EntityType.STRAY -> "스트레이"
        EntityType.ZOMBIE_VILLAGER -> "좀비 주민"
        EntityType.HUSK -> "허스크"
        EntityType.DROWNED -> "드라운드"
        EntityType.VINDICATOR -> "빈디케이터"
        EntityType.EVOKER -> "소환사"
        EntityType.VEX -> "벡스"
        EntityType.PILLAGER -> "약탈자"
        EntityType.RAVAGER -> "라베저"
        EntityType.PHANTOM -> "팬텀"
        EntityType.ZOGLIN -> "조글린"
        EntityType.WARDEN -> "워든"
        EntityType.BREEZE -> "브리즈"
        EntityType.BOGGED -> "바그드"
        EntityType.CREAKING -> "크리킹"
        EntityType.GHAST -> "가스트"
        EntityType.BLAZE -> "블레이즈"
        EntityType.MAGMA_CUBE -> "마그마 큐브"
        EntityType.WITHER_SKELETON -> "위더 스켈레톤"
        EntityType.PIGLIN -> "피글린"
        EntityType.PIGLIN_BRUTE -> "피글린 야수"
        EntityType.HOGLIN -> "호글린"
        EntityType.SHULKER -> "슐커"
        EntityType.ENDERMITE -> "엔더마이트"
        EntityType.ZOMBIFIED_PIGLIN -> "좀비화된 피글린"
        EntityType.ENDERMAN -> "엔더맨"
        else -> type.name
    }
}