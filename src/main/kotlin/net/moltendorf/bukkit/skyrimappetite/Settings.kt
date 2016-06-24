package net.moltendorf.bukkit.skyrimappetite

import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.material.MaterialData
import java.util.*

/**
 * Created by moltendorf on 15/05/23.

 * @author moltendorf
 */
class Settings {

  var enabled = true // Whether or not the plugin is enabled at all; interface mode.

  private val foodValues = mutableMapOf(
    Pair(Material.APPLE, SimpleValue(4)),
    Pair(Material.BAKED_POTATO, SimpleValue(5)),
    //Pair(Material.BEETROOT, new SimpleValue(1)), // 1.9
    //Pair(Material.BEETROOT_SOUP, new SimpleValue(6)), // 1.9
    Pair(Material.BREAD, SimpleValue(5)),
    Pair(Material.CAKE_BLOCK, SimpleValue(2)),
    Pair(Material.CARROT_ITEM, SimpleValue(3)),
    //Pair(Material.CHORUS_FRUIT, new SimpleValue(4)), // 1.9
    Pair(Material.COOKED_CHICKEN, SimpleValue(6)),
    Pair(Material.COOKED_FISH, ComplexValue(mapOf(
      Pair(0.toByte(), 5), // Cooked Fish
      Pair(1.toByte(), 6) // Cooked Salmon
    ))),
    Pair(Material.COOKED_MUTTON, SimpleValue(6)),
    Pair(Material.GRILLED_PORK, SimpleValue(8)),
    Pair(Material.COOKED_RABBIT, SimpleValue(5)),
    Pair(Material.COOKIE, SimpleValue(2)),
    Pair(Material.GOLDEN_APPLE, SimpleValue(4)),
    Pair(Material.GOLDEN_CARROT, SimpleValue(6)),
    Pair(Material.MELON, SimpleValue(2)),
    Pair(Material.MUSHROOM_SOUP, SimpleValue(6)),
    Pair(Material.POISONOUS_POTATO, SimpleValue(2)),
    Pair(Material.POTATO_ITEM, SimpleValue(1)),
    Pair(Material.PUMPKIN_PIE, SimpleValue(8)),
    Pair(Material.RABBIT_STEW, SimpleValue(10)),
    Pair(Material.RAW_BEEF, SimpleValue(3)),
    Pair(Material.RAW_CHICKEN, SimpleValue(2)),
    Pair(Material.RAW_FISH, ComplexValue(mapOf(
      Pair(0.toByte(), 2), // Raw Fish
      Pair(1.toByte(), 2), // Raw Salmon
      Pair(2.toByte(), 1), // Clownfish
      Pair(3.toByte(), 1) // Pufferfish
    ))),
    Pair(Material.MUTTON, SimpleValue(2)),
    Pair(Material.PORK, SimpleValue(3)),
    Pair(Material.RABBIT, SimpleValue(3)),
    Pair(Material.ROTTEN_FLESH, SimpleValue(4)),
    Pair(Material.SPIDER_EYE, SimpleValue(2)),
    Pair(Material.COOKED_BEEF, SimpleValue(8)))

  init {
    val instance = SkyrimAppetite.instance
    val log = instance!!.logger

    // Make sure the default configuration is saved.
    instance.saveDefaultConfig()

    val config = instance.config

    enabled = config.getBoolean("enabled", enabled)

    val foodsSection = config.getConfigurationSection("foods")

    if (foodsSection != null) {
      foodValues.clear()

      for (key in foodsSection.getKeys(false)) {
        try {
          val foodValue = foodsSection.getInt(key, -1)

          if (foodValue < 1) {
            val foodSection = foodsSection.getConfigurationSection(key)

            if (foodSection == null) {
              log.warning("Config: Invalid value type for $key specified.")
            } else {
              foodValues.put(Material.valueOf(key), ComplexValue(foodSection))
            }
          } else {
            foodValues.put(Material.valueOf(key), SimpleValue(foodValue))
          }
        } catch (exception: IllegalArgumentException) {
          log.warning("Config: Invalid material $key specified.")
        }

      }
    }

    val players = config.getConfigurationSection("players")

    if (players != null) {
      val foodLevels = instance.listeners!!.foodLevels

      for (key in players.getKeys(false)) {
        try {
          val playerId = UUID.fromString(key)

          val foodLevel = players.getInt(key, -1)

          if (foodLevel >= 0) {
            foodLevels.put(playerId, foodLevel)
          } else {
            log.warning("Config: Invalid food level for $key specified.")
          }
        } catch (exception: IllegalArgumentException) {
          log.warning("Config: Invalid UUID $key.")
        }

      }
    }
  }

  val maxFoodLevel: Int
    get() = 10

  fun getFoodValue(material: Material, data: MaterialData): Int {
    val value = foodValues[material]

    if (value == null) {
      SkyrimAppetite.instance!!.logger.warning("Config: No value specified for $material.")

      return 1
    } else {
      val integer = value.getValue(data)

      if (integer == null) {
        SkyrimAppetite.instance!!.logger.warning("Config: No data value specified for $material.")

        return 1
      } else {
        return integer
      }
    }
  }

  fun save() {
    val instance = SkyrimAppetite.instance

    val config = instance!!.config
    val players = config.createSection("players")

    for (entry in instance.listeners!!.foodLevels.entries) {
      players.set(entry.key.toString(), entry.value)
    }

    instance.saveConfig()
  }

  companion object {
    val instance: Settings
      get() = SkyrimAppetite.instance!!.settings!!
  }
}

internal interface Value {
  fun getValue(data: MaterialData): Int?
}

internal class SimpleValue(private val value: Int?) : Value {

  override fun getValue(data: MaterialData): Int? {
    return value
  }
}

internal class ComplexValue : Value {
  private val value: Map<Byte, Int>

  constructor(value: Map<Byte, Int>) {
    this.value = value
  }

  constructor(config: ConfigurationSection) {
    value = HashMap<Byte, Int>()

    for (key in config.getKeys(false)) {
      try {
        value.put(java.lang.Byte.valueOf(key), config.getInt(key, 1))
      } catch (exception: NumberFormatException) {
        SkyrimAppetite.instance!!.logger.warning("Config: Invalid data value specified: $key.")
      }

    }
  }

  override fun getValue(data: MaterialData): Int? {
    return value[data.data]
  }
}
