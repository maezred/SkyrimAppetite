package net.moltendorf.bukkit.skyrimappetite

import org.bukkit.material.MaterialData

internal class SimpleValue(private val value: Int?) : Value {

  override fun getValue(data: MaterialData): Int? {
    return value
  }
}
