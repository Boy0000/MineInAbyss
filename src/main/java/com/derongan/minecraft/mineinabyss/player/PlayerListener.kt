package com.derongan.minecraft.mineinabyss.player

import com.derongan.minecraft.mineinabyss.AbyssContext
import com.derongan.minecraft.mineinabyss.AbyssContext.getPlayerData
import com.derongan.minecraft.mineinabyss.MineInAbyss.Companion.econ
import com.derongan.minecraft.mineinabyss.ascension.effect.effects.MaxHealthChangeEffect
import com.derongan.minecraft.mineinabyss.playerData
import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.messaging.logWarn
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.io.IOException

object PlayerListener : Listener {
    private val playerDataConfigManager = AbyssContext.configManager.playerDataCM

    @EventHandler
    fun onPlayerJoin(joinEvent: PlayerJoinEvent) {
        AbyssContext.playerDataMap[joinEvent.player.uniqueId] = playerDataConfigManager.loadPlayerData(joinEvent.player)
    }

    @EventHandler
    fun onPlayerLeave(playerQuitEvent: PlayerQuitEvent) {
        val (player) = playerQuitEvent
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.run {
            modifiers.filter {
                it.name == MaxHealthChangeEffect.CURSE_MAX_HEALTH
                        && !MaxHealthChangeEffect.activeEffects.contains(it)
            }.forEach {
                removeModifier(it)
            }
        }

        val data: PlayerData = AbyssContext.playerDataMap.remove(player.uniqueId) ?: return
        try {
            playerDataConfigManager.savePlayerData(data)
        } catch (e: IOException) {
            logWarn("Failed to save data for player ${playerQuitEvent.player.uniqueId}")
            e.printStackTrace()
        }
    }

    @EventHandler
    fun onPlayerDeath(pde: PlayerDeathEvent) {
        val player = pde.entity
        val playerData = getPlayerData(player)

        //TODO maybe limit this to only the survival server with a config option
        if (player.lastDamageCause?.cause == EntityDamageEvent.DamageCause.VOID) pde.keepInventory = true
        if (!playerData.isIngame) return
        playerData.isIngame = false
        player.sendMessage("""
            &6&lGame Stats:
            &6Exp earned:&7 ${playerData.exp - playerData.expOnDescent}
            &6Started dive on:&7 ${playerData.descentDate}
            """.trimIndent().color())
    }

    @EventHandler
    fun onPlayerGainEXP(e: PlayerExpChangeEvent) {
        val (player, amount) = e
        if (amount <= 0) return
        econ?.depositPlayer(player, amount.toDouble())
        player.playerData.addExp(amount.toDouble())
    }

}