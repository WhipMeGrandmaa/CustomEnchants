package me.whipmegrandma.customenchants.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.whipmegrandma.customenchants.api.ArmorEquipEvent;
import me.whipmegrandma.customenchants.enchant.AntiHungerEnchant;
import me.whipmegrandma.customenchants.enchant.DrillEnchant;
import me.whipmegrandma.customenchants.enchant.HasteEnchant;
import me.whipmegrandma.customenchants.enchant.SpeedEnchant;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.model.Triple;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompParticle;

import java.util.HashMap;
import java.util.UUID;

@AutoRegister
public final class EnchantEffectListener implements Listener {

	private HashMap<UUID, Triple<BlockFace, Integer, ItemStack>> drillEnchantDirection = new HashMap<>();

	private final Integer drillEnchantRadius = 1;

	private final Boolean worldGuardLoaded = HookManager.isWorldGuardLoaded();

	@EventHandler
	public void onEquip(ArmorEquipEvent event) {
		ItemStack armor = event.getNewArmorPiece();

		if (armor == null || CompMaterial.isAir(armor) || armor.getEnchantments().isEmpty())
			return;

		Player player = event.getPlayer();
		String name = armor.getType().toString().toLowerCase();

		PotionEffect effect = null;

		if (name.contains("_boots") && armor.containsEnchantment(SpeedEnchant.getInstance()))
			effect = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0);

		if (name.contains("_helmet") && armor.containsEnchantment(AntiHungerEnchant.getInstance()))
			effect = new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0);

		if (effect != null)
			player.addPotionEffect(effect);
	}

	@EventHandler
	public void onUnequip(ArmorEquipEvent event) {
		ItemStack armor = event.getOldArmorPiece();

		if (armor == null || CompMaterial.isAir(armor) || armor.getEnchantments().isEmpty())
			return;

		Player player = event.getPlayer();
		String name = armor.getType().toString().toLowerCase();

		PotionEffectType effect = null;

		if (name.contains("boots") && armor.containsEnchantment(SpeedEnchant.getInstance()))
			effect = PotionEffectType.SPEED;

		if (name.contains("helmet") && armor.containsEnchantment(AntiHungerEnchant.getInstance()))
			effect = PotionEffectType.SATURATION;

		if (effect != null)
			player.removePotionEffect(effect);
	}

	@EventHandler
	public void onDrillMine(BlockBreakEvent event) {

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		ItemStack toolMine = player.getInventory().getItemInMainHand();
		Location location = event.getBlock().getLocation();

		if (!this.drillEnchantDirection.containsKey(uuid))
			return;

		Triple<BlockFace, Integer, ItemStack> tuple = this.drillEnchantDirection.get(uuid);

		BlockFace face = tuple.getFirst();
		Integer level = tuple.getSecond();
		ItemStack toolTuple = tuple.getThird();

		if (!toolTuple.equals(toolMine))
			return;

		switch (face) {
			case DOWN:
			case UP:
				for (int x = location.getBlockX() - drillEnchantRadius; x <= location.getBlockX() + drillEnchantRadius; x++)
					for (int z = location.getBlockZ() - drillEnchantRadius; z <= location.getBlockZ() + drillEnchantRadius; z++) {

						Location locationBlock = new Location(location.getWorld(), x, location.getBlockY(), z);
						this.drillExecute(locationBlock, player);
					}
				break;

			case SOUTH:
			case NORTH:
				for (int x = location.getBlockX() - drillEnchantRadius; x <= location.getBlockX() + drillEnchantRadius; x++)
					for (int y = location.getBlockY() - drillEnchantRadius; y <= location.getBlockY() + drillEnchantRadius; y++) {

						Location locationBlock = new Location(location.getWorld(), x, y, location.getBlockZ());
						this.drillExecute(locationBlock, player);
					}
				break;

			case EAST:
			case WEST:
				for (int y = location.getBlockY() - drillEnchantRadius; y <= location.getBlockY() + drillEnchantRadius; y++)
					for (int z = location.getBlockZ() - drillEnchantRadius; z <= location.getBlockZ() + drillEnchantRadius; z++) {

						Location locationBlock = new Location(location.getWorld(), location.getBlockX(), y, z);
						this.drillExecute(locationBlock, player);
					}
				break;
		}

		this.drillEnchantDirection.remove(uuid);

	}

	@EventHandler
	public void onDrillInteract(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		Block block = event.getClickedBlock();
		BlockFace face = event.getBlockFace();
		ItemStack tool = player.getInventory().getItemInMainHand();

		if (block == null || CompMaterial.isAir(tool) || !tool.containsEnchantment(DrillEnchant.getInstance()) || player.getGameMode() == GameMode.CREATIVE)
			return;

		String name = tool.getType().toString().toLowerCase();

		Enchantment enchant = DrillEnchant.getInstance();
		Integer level = tool.getEnchantmentLevel(enchant);
		boolean test = event.getClickedBlock() == null;

		if (this.worldGuardTest(block.getLocation(), player))
			drillEnchantDirection.put(uuid, new Triple<>(face, level, tool));
	}

	@EventHandler
	public void onItemHeld(PlayerItemHeldEvent event) {
		ItemStack tool = event.getPlayer().getInventory().getItem(event.getNewSlot());

		if (CompMaterial.isAir(tool) || !tool.containsEnchantment(HasteEnchant.getInstance()))
			return;

		Player player = event.getPlayer();
		String name = tool.getType().toString().toLowerCase();

		PotionEffect effect = new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0);

		player.addPotionEffect(effect);
	}

	@EventHandler
	public void onItemUnheld(PlayerItemHeldEvent event) {
		ItemStack tool = event.getPlayer().getInventory().getItem(event.getPreviousSlot());

		if (CompMaterial.isAir(tool) || !tool.containsEnchantment(HasteEnchant.getInstance()))
			return;

		Player player = event.getPlayer();
		String name = tool.getType().toString().toLowerCase();

		PotionEffectType effect = PotionEffectType.FAST_DIGGING;

		player.removePotionEffect(effect);
	}

	private void drillExecute(Location locationBlock, Player player) {
		Block block = locationBlock.getBlock();
		ItemStack tool = player.getInventory().getItemInMainHand();

		if (block.getType() == CompMaterial.BEDROCK.toMaterial() || CompMaterial.isAir(block))
			return;

		if (this.worldGuardTest(locationBlock, player) && !block.getDrops(tool).isEmpty()) {
			this.drillExpDrop(block);
			block.breakNaturally(tool);

			boolean chance = RandomUtil.chance(25);

			if (chance)
				CompParticle.CLOUD.spawn(locationBlock);
		}
	}

	private void drillExpDrop(Block block) {
		String material = block.getType().toString().toLowerCase();

		if (material.contains("nether_gold_ore"))
			this.expTimes(0, 1, block);

		if (material.contains("coal_ore"))
			this.expTimes(0, 2, block);

		if (material.contains("diamond_ore") || material.contains("emerald_ore"))
			this.expTimes(3, 7, block);

		if (material.contains("lapis_ore") || material.contains("quartz_ore"))
			this.expTimes(2, 5, block);

		if (material.contains("redstone_ore"))
			this.expTimes(1, 5, block);

		if (material.contains("spawner"))
			this.expTimes(15, 43, block);

		if ("sculk".equals(material))
			this.expTimes(1, 1, block);

		if ("sculk_catalyst".equals(material) || "sculk_shrieker".equals(material) || "sculk_sensor".equals(material))
			this.expTimes(5, 5, block);
	}

	private void expTimes(Integer min, Integer max, Block block) {
		Valid.checkBoolean(min < max, "Max must be higher than low.");

		Location location = block.getLocation();
		World world = location.getWorld();

		Integer random = min + RandomUtil.nextInt((max + 1) - min);

		world.spawn(location, ExperienceOrb.class).setExperience(random);
	}

	private boolean worldGuardTest(Location locationBlock, Player player) {

		if (this.worldGuardLoaded) {

			LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
			com.sk89q.worldedit.util.Location locationAdapted = BukkitAdapter.adapt(locationBlock);
			RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionQuery query = regionContainer.createQuery();

			if (player.isOp())
				return true;

			return query.testBuild(locationAdapted, localPlayer, Flags.BLOCK_BREAK);
		} else
			return true;
	}

}
