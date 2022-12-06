package me.whipmegrandma.customenchants.command.enchants;

import me.whipmegrandma.customenchants.menu.EnchantShopMenu;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public final class EnchantsShopSubCommand extends SimpleSubCommand {

	public EnchantsShopSubCommand(SimpleCommandGroup parent) {
		super(parent, "shop");

		this.setUsage("<username> <enchant> <level>");
		this.setPermission("customenchants.command.shop");
	}

	@Override
	protected void onCommand() {

		if (args.length == 0) {
			checkConsole();

			EnchantShopMenu.getInstance().displayTo(getPlayer());
			return;
		}

		Player player = findPlayer(args[0]);

		EnchantShopMenu.getInstance().displayTo(player);
	}

	@Override
	protected List<String> tabComplete() {

		if (args.length == 1)
			return completeLastWordPlayerNames();

		return NO_COMPLETE;
	}
}
