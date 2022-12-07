package me.whipmegrandma.customenchants.manager;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.ArrayList;
import java.util.List;

public class PhysicalShopManager extends YamlConfig {

	@Getter
	private static PhysicalShopManager instance = new PhysicalShopManager();

	List<Location> shops = new ArrayList<>();

	private PhysicalShopManager() {
		instance = this;
		this.loadConfiguration(NO_DEFAULT, "physicalshoplocations.yml");
	}

	@Override
	protected void onSave() {
		this.set("Shops", this.shops);
	}

	@Override
	protected void onLoad() {
		this.shops = this.getList("Shops", Location.class);
	}

	public void add(Location shop) {
		this.shops.add(shop);

		this.save();
	}

	public void remove(Location shop) {
		this.shops.remove(shop);

		this.save();
	}

	public static boolean isEntityValid(Entity entity) {
		return entity instanceof Villager || CompMetadata.hasMetadata(entity, "CustomEnchants:Plugin");
	}

	public static void load() {
		new PhysicalShopManager();
	}
}
