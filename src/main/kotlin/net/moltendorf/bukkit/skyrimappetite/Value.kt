package net.moltendorf.bukkit.skyrimappetite

import org.bukkit.material.MaterialData

internal interface Value {
  fun getValue(data: MaterialData): Int?
}
