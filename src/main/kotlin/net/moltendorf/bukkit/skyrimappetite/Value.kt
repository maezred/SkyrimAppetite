package net.moltendorf.bukkit.skyrimappetite

import org.bukkit.material.MaterialData

interface Value {
  fun getValue(data: MaterialData): Int?
}
