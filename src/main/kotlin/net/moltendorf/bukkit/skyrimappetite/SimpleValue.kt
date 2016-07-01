package net.moltendorf.bukkit.skyrimappetite

import org.bukkit.material.MaterialData

class SimpleValue(private val value: Int) : Value {
  override fun getValue(data: MaterialData): Int {
    return value
  }
}
