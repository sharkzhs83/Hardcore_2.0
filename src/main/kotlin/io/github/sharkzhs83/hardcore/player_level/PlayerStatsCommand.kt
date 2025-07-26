package io.github.sharkzhs83.hardcore.player_level

import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PlayerStatsCommand(private val plugin: JavaPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("플레이어만 사용할 수 있습니다.")
            return true
        }
        val uuid = sender.uniqueId.toString()
        val cfg = plugin.config
        if (command.name.equals("stats", true)) {
            val msg = Component.text(
                "남은 포인트: ${cfg.getInt("players.$uuid.points")} " +
                        "체력: ${cfg.getInt("players.$uuid.health")} " +
                        "근접: ${cfg.getInt("players.$uuid.melee")} " +
                        "원거리: ${cfg.getInt("players.$uuid.ranged")} " +
                        "폭발: ${cfg.getInt("players.$uuid.explosive")} " +
                        "스피드: ${cfg.getInt("players.$uuid.speed")} " +
                        "방어력: ${cfg.getInt("players.$uuid.defense")}")
            sender.sendMessage(msg)
            return true
        }
        return false
    }
}
