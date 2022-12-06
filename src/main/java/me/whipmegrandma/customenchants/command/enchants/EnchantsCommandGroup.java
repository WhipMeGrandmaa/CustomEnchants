package me.whipmegrandma.customenchants.command.enchants;

import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.ReloadCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.model.SimpleComponent;

import java.util.Arrays;
import java.util.List;

@AutoRegister
public final class EnchantsCommandGroup extends SimpleCommandGroup {

	public EnchantsCommandGroup() {
		super("enchants|e");
	}

	@Override
	protected void registerSubcommands() {
		this.registerSubcommand(new ReloadCommand());
		this.registerSubcommand(new EnchantsGiveSubCommand(this));
		this.registerSubcommand(new EnchantsListSubCommand(this));
		this.registerSubcommand(new EnchantsShopSubCommand(this));
		this.registerSubcommand(new EnchantsEditSubCommand(this));
	}

	@Override
	protected String[] getHelpHeader() {
		return new String[]{Common.colorize("{prefix} The following commands are available:")};
	}


	@Override
	protected List<SimpleComponent> getNoParamsHeader() {
		return Arrays.asList(SimpleComponent.of("{prefix} Use /enchants ? to list the commands."));
	}

}
