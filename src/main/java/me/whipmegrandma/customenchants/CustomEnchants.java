package me.whipmegrandma.customenchants;

import me.whipmegrandma.customenchants.manager.EnchantData;
import me.whipmegrandma.customenchants.manager.PhysicalShopManager;
import me.whipmegrandma.customenchants.menu.EnchantPreviewMenu;
import me.whipmegrandma.customenchants.menu.EnchantShopMenu;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.HookManager;
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
		this.checkDependencies();
	}

	private void loadMenus() {
		EnchantPreviewMenu.loadMenu();
		EnchantShopMenu.loadMenu();
	}

	private void loadManagers() {
		EnchantData.loadEnchants();
		PhysicalShopManager.load();
	}

	private void checkDependencies() {

		if (Common.doesPluginExist("PowerCurrency"))
			Common.log("Enabled support for PowerCurrency.");
		else
			Common.log("&4PowerCurrency isn't loaded. All enchants are free.");

		if (HookManager.isPlaceholderAPILoaded())
			Common.log("Enabled support for PlaceholderAPI.");
		else
			Common.log("&4PlaceholderAPI isn't loaded. Some variables may not work.");
	}

	public static CustomEnchants getInstance() {
		return (CustomEnchants) SimplePlugin.getInstance();
	}

	@Override
	public int getMetricsPluginId() {
		return 17039;
	}
}
