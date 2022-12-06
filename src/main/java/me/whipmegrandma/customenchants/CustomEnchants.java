package me.whipmegrandma.customenchants;

import me.whipmegrandma.customenchants.manager.EnchantData;
import me.whipmegrandma.customenchants.manager.PhysicalShopManager;
import me.whipmegrandma.customenchants.menu.EnchantPreviewMenu;
import me.whipmegrandma.customenchants.menu.EnchantShopMenu;
import org.mineacademy.fo.plugin.SimplePlugin;

public final class CustomEnchants extends SimplePlugin {

	@Override
	protected void onPluginStart() {
	}

	@Override
	protected void onReloadablesStart() {
		this.setup();
	}

	private void setup() {
		this.loadMenus();
		this.loadManagers();
	}

	private void loadMenus() {
		EnchantPreviewMenu.loadMenu();
		EnchantShopMenu.loadMenu();
	}

	private void loadManagers() {
		EnchantData.loadEnchants();
		PhysicalShopManager.load();
	}

	public static CustomEnchants getInstance() {
		return (CustomEnchants) SimplePlugin.getInstance();
	}
}
