package me.whipmegrandma.customenchants.enchant;

import lombok.Getter;
import me.whipmegrandma.customenchants.manager.EnchantsManager;
import org.mineacademy.fo.model.SimpleEnchantment;

public final class DrillEnchant extends SimpleEnchantment {

	@Getter
	private final static DrillEnchant instance = new DrillEnchant();

	private DrillEnchant() {
		super("Drill", 1);

		EnchantsManager.register(this);
	}
}
