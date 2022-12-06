package me.whipmegrandma.customenchants.command;

import me.whipmegrandma.customenchants.menu.EnchantPreviewMenu;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;

import java.util.List;

@AutoRegister
public final class CustomEnchantCommand extends SimpleCommand {

	public CustomEnchantCommand() {
		super("customenchant|ce");

	}

	@Override
	protected void onCommand() {
		checkConsole();

		EnchantPreviewMenu.getInstance().displayTo(getPlayer());
	}

	@Override
	protected List<String> tabComplete() {
		return NO_COMPLETE;
	}
}
