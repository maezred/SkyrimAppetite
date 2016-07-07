package net.moltendorf.bukkit.skyrimappetite.settings

import org.bukkit.material.MaterialData

interface Value {
  fun getValue(data: MaterialData): Int?
}
