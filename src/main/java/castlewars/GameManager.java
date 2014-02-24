package castlewars;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GameManager {
	private CastleWars plugin;
	private GameTimer gameTimer;

	private List<Material> allowedMaterials = new ArrayList<Material>();

	{
		// Redstone
		// allowedMaterials.add(Material.DISPENSER);
		// allowedMaterials.add(Material.PISTON_STICKY_BASE);
		// allowedMaterials.add(Material.PISTON_BASE);
		allowedMaterials.add(Material.TNT);
		allowedMaterials.add(Material.LEVER);
		allowedMaterials.add(Material.STONE_PLATE);
		allowedMaterials.add(Material.WOOD_PLATE);
		allowedMaterials.add(Material.REDSTONE_TORCH_ON);
		allowedMaterials.add(Material.STONE_BUTTON);
		allowedMaterials.add(Material.TRIPWIRE_HOOK);
		allowedMaterials.add(Material.WOOD_BUTTON);
		allowedMaterials.add(Material.REDSTONE_WIRE);
		allowedMaterials.add(Material.DIODE_BLOCK_OFF);
		allowedMaterials.add(Material.REDSTONE_COMPARATOR_OFF);

		// Rail
		allowedMaterials.add(Material.POWERED_RAIL);
		allowedMaterials.add(Material.DETECTOR_RAIL);
		allowedMaterials.add(Material.RAILS);
		allowedMaterials.add(Material.ACTIVATOR_RAIL);

		// Other
		allowedMaterials.add(Material.FIRE);
		allowedMaterials.add(Material.TORCH);
		// allowedMaterials.add(Material.CHEST);
		// allowedMaterials.add(Material.WORKBENCH);
		// allowedMaterials.add(Material.FURNACE);
		allowedMaterials.add(Material.LADDER);
		allowedMaterials.add(Material.VINE);
		// allowedMaterials.add(Material.ANVIL);
	}

	public GameManager(CastleWars plugin) {
		this.plugin = plugin;
	}

	public void start(int buildTime, int prepareTime, boolean keepInventory) {
		gameTimer = new GameTimer(plugin, buildTime, prepareTime);
		gameTimer.runTaskTimer(plugin, 1200, 1200);
		gameTimer.start();

		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (plugin.getTeamManager().hasTeam(player)) {
				plugin.getTeamManager().setSpectating(player, 0);

				if (!player.getGameMode().equals(GameMode.CREATIVE)) {
					player.setFlying(false);
					player.setAllowFlight(false);
				}

				if (plugin.getTeamManager().hasSpawn(player)) {
					player.teleport(plugin.getTeamManager().getSpawn(player));
				}

				if (!keepInventory) {
					plugin.getTeamManager().kitPlayer(player);
				}
			} else {
				plugin.getTeamManager().setSpectating(player, 1);
				player.setAllowFlight(true);
				player.setFlying(true);
			}

			plugin.getTeamManager().setKills(player, 0);
			plugin.getTeamManager().setDeaths(player, 0);
		}
	}

	/*
	public void setInventory(Player player) {
		ItemStack[] content = new ItemStack[36];
		content[0] = new ItemStack(Material.IRON_SWORD, 1);
		content[1] = new ItemStack(Material.BOW, 1);
		content[2] = new ItemStack(Material.FLINT_AND_STEEL, 1);
		content[3] = new ItemStack(Material.IRON_PICKAXE, 1);
		content[4] = new ItemStack(Material.IRON_SPADE, 1);
		content[5] = new ItemStack(Material.IRON_AXE, 1);
		content[6] = new ItemStack(Material.TORCH, 16);
		content[7] = new ItemStack(Material.POTION, 1);
		content[7].setDurability((short) 2);
		content[8] = new ItemStack(Material.COOKED_BEEF, 4);
		content[9] = new ItemStack(Material.COBBLESTONE, 64);
		content[10] = new ItemStack(Material.COBBLESTONE, 64);
		content[11] = new ItemStack(Material.LOG, 32);
		content[12] = new ItemStack(Material.ARROW, 64);
		content[13] = new ItemStack(Material.SULPHUR, 5);

		ItemStack[] armorContent = new ItemStack[4];
		armorContent[0] = new ItemStack(Material.IRON_BOOTS, 1);
		armorContent[1] = new ItemStack(Material.IRON_LEGGINGS, 1);
		armorContent[2] = new ItemStack(Material.IRON_CHESTPLATE, 1);
		armorContent[3] = new ItemStack(Material.IRON_HELMET, 1);

		PlayerInventory inventory = player.getInventory();
		inventory.setContents(content);
		inventory.setArmorContents(armorContent);
	}
	*/

	public void stop() {
		gameTimer.stop();
		gameTimer = null;
	}

	public boolean isGameInProgress() {
		return gameTimer != null;
	}

	public boolean isSiegeMaterial(Material material) {
		return allowedMaterials.contains(material);
	}

	public List<Material> getSiegeMaterials() {
		return allowedMaterials;
	}

	public void addSiegeMaterial(Material material) {
		allowedMaterials.add(material);
	}

	public void removeSiegeMaterial(Material material) {
		allowedMaterials.remove(material);
	}

	public boolean isBuilding() {
		return isGameInProgress() ? gameTimer.building : false;
	}

	public boolean isPreparing() {
		return isGameInProgress() ? gameTimer.preparing : false;
	}

	public boolean isPlaying() {
		return isGameInProgress() ? gameTimer.playing : false;
	}
}
