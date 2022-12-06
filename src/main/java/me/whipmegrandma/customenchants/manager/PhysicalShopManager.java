package me.whipmegrandma.customenchants.manager;

import lombok.Getter;
import org.bukkit.Location;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.ArrayList;
import java.util.List;

public class PhysicalShopManager extends YamlConfig {

	@Getter
	private static final PhysicalShopManager instance = new PhysicalShopManager();

	List<Location> shops = new ArrayList<>();

	private PhysicalShopManager() {

		this.loadConfiguration(NO_DEFAULT, "physicalshoplocations.yml");
	}

	@Override
	protected void onSave() {
		this.set("Shops", shops);
	}

	@Override
	protected void onLoad() {
		this.shops = this.getList("Shops", Location.class);
	}

	public void add(Location shop) {
		shops.add(shop);

		this.save();
	}

	public void remove(Location shop) {
		shops.remove(shop);

		this.save();
	}

	public static void load() {
		new PhysicalShopManager();
	}
}
