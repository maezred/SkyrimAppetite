package net.moltendorf.bukkit.skyrimappetite

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.material.MaterialData

/**
 * Created by moltendorf on 2016-02-28.
 */
class Listeners : Listener {
  private fun playerConsumedFood(player: Player, material: Material, data: MaterialData) {
    val playerId = player.uniqueId
    var foodLevel: Int? = settings.foodLevels[playerId]
    val foodValue = settings.getFoodValue(material, data)
    val playerFoodLevel = player.foodLevel
    val statusMultiplier = if (playerFoodLevel >= settings.foodMidpoint) settings.foodMidpoint / 10.0 else 10.0 / settings.foodMidpoint
    val foodMultiplier = settings.foodMultiplier * statusMultiplier

    if (foodLevel == null) {
      foodLevel = 0

      player.sendMessage("Since this is your first time eating food with SkyrimAppetite installed, here's how hunger works: we count how much food you've eaten; if you've eaten enough, you gain a point of hunger.")
      player.sendMessage("In short, you must eat a ton of food now.")
    }

    foodLevel += foodValue

    if (foodLevel >= foodMultiplier || playerFoodLevel <= 0) {
      val foodPoints = (foodLevel / foodMultiplier).toInt()

      player.foodLevel = playerFoodLevel - foodValue + foodPoints

      foodLevel = (foodLevel % foodMultiplier).toInt()
    } else {
      player.foodLevel = playerFoodLevel - foodValue
    }

    settings.foodLevels[playerId] = foodLevel
  }

  @EventHandler(priority = EventPriority.MONITOR)
  fun playerItemConsumeEventMonitor(event: PlayerItemConsumeEvent) {
    val item = event.item

    playerConsumedFood(event.player, item.type, item.data)
  }

  @EventHandler(priority = EventPriority.MONITOR)
  fun playerInteractEventMonitor(event: PlayerInteractEvent) {
    val block = event.clickedBlock ?: return
    val material = block.type
    val player = event.player

    if (material == Material.CAKE_BLOCK && player.foodLevel < 20) {
      playerConsumedFood(player, material, material.getNewData(0.toByte()))
    }
  }
}
