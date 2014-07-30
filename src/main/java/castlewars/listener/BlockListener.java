package castlewars.listener;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.ScoreboardTeam;

import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scoreboard.Team;

import castlewars.CastleWars;

public class BlockListener implements Listener {
	private CastleWars plugin;

	public BlockListener(CastleWars snowball) {
		plugin = snowball;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		Team playerTeam = plugin.getTeamManager().getTeam(player);
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		String playerDisplayName = ScoreboardTeam.getPlayerDisplayName(entityPlayer.getScoreboardTeam(), entityPlayer.getName());

		if (plugin.getGameManager().isGameInProgress()) {
			if (plugin.getTeamManager().isSpectating(player)) {
				event.setCancelled(true);
			} else {
				if (block.getState() instanceof Beacon) {
					Beacon beacon = (Beacon) block.getState();
					Team otherTeam = plugin.getTeamManager().getTeam(beacon.getInventory().getName());

					if (playerTeam != null && otherTeam != null) {
						if (!playerTeam.equals(otherTeam) && plugin.getGameManager().isPlaying()) {
							plugin.getServer().broadcastMessage(playerDisplayName + " has destroyed " + otherTeam.getDisplayName() + " teams core!");
							plugin.getServer().broadcastMessage(playerTeam.getDisplayName() + " team wins the battle!");
							return;
						} else {
							event.setCancelled(true);
						}
					}
				}

				if (plugin.getGameManager().isPlaying() && !event.isCancelled()) {
					event.setCancelled(!plugin.getGameManager().isSiegeMaterial(block.getType()));
				}
			}
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		if (plugin.getGameManager().isBuilding() || plugin.getGameManager().isPreparing()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (plugin.getGameManager().isGameInProgress()) {
			if (plugin.getTeamManager().isSpectating(player)) {
				event.setCancelled(true);
			} else {
				if (plugin.getGameManager().isPlaying()) {
					event.setCancelled(!plugin.getGameManager().isSiegeMaterial(block.getType()));
				}
			}
		}
	}
}
