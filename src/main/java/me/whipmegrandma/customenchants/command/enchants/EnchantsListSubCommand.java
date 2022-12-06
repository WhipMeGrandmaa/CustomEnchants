package me.whipmegrandma.customenchants.command.enchants;

import me.whipmegrandma.customenchants.manager.EnchantsManager;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.List;

public final class EnchantsListSubCommand extends SimpleSubCommand {

	public EnchantsListSubCommand(SimpleCommandGroup parent) {
		super(parent, "list");

		this.setPermission("customenchants.command.list");
	}

	@Override
	protected void onCommand() {

		tell(Common.join(EnchantsManager.findEnchantmentNames()));

	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
