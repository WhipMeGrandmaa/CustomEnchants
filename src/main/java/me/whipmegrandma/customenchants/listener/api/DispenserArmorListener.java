package me.whipmegrandma.customenchants.listener.api;

import me.whipmegrandma.customenchants.api.ArmorEquipEvent;
import me.whipmegrandma.customenchants.api.ArmorType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.mineacademy.fo.annotation.AutoRegister;

@AutoRegister
public final class DispenserArmorListener implements Listener {

	@EventHandler
	public void dispenseArmorEvent(BlockDispenseArmorEvent event) {
		ArmorType type = ArmorType.matchType(event.getItem());
		if (type != null) {
			if (event.getTargetEntity() instanceof Player) {
				Player p = (Player) event.getTargetEntity();
				ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DISPENSER, type, null, event.getItem());
				Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
				if (armorEquipEvent.isCancelled()) {
					event.setCancelled(true);
				}
			}
		}
	}
}