package net.moltendorf.bukkit.skyrimappetite

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.material.MaterialData
import java.util.*

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
        log.warning("Config: Invalid data value specified: $key.")
      }

    }
  }

  override fun getValue(data: MaterialData): Int? {
    return value[data.data]
  }
}
