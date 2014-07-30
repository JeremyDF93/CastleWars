package castlewars.listener;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.ScoreboardTeam;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import castlewars.CastleWars;

public class PlayerListener implements Listener {
	private CastleWars plugin;

	public PlayerListener(CastleWars plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		String playerDisplayName = ScoreboardTeam.getPlayerDisplayName(entityPlayer.getScoreboardTeam(), entityPlayer.getName());
		
		if (plugin.getTeamManager().isSpectating(player)) {
			event.setFormat("*<" + playerDisplayName + "> " + event.getMessage());
		} else {
			event.setFormat("<" + playerDisplayName + "> " + event.getMessage());
		}
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();

		if (plugin.getGameManager().isGameInProgress() && plugin.getTeamManager().isSpectating(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();

		if (plugin.getGameManager().isGameInProgress() && plugin.getTeamManager().isSpectating(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (plugin.getGameManager().isGameInProgress() && plugin.getTeamManager().isSpectating(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		String playerDisplayName = ScoreboardTeam.getPlayerDisplayName(entityPlayer.getScoreboardTeam(), entityPlayer.getName());

		if (!plugin.getTeamManager().hasTeam(player) && plugin.getGameManager().isGameInProgress()) {
			plugin.getTeamManager().setSpectating(player, 1);
			player.setAllowFlight(true);
			player.setFlying(true);
			player.sendMessage(ChatColor.RED + "You have joined a game in progress and have been set as a spectator.");
		}
		
		event.setJoinMessage(ChatColor.YELLOW + playerDisplayName + ChatColor.YELLOW + " joined the game.");
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		String playerDisplayName = ScoreboardTeam.getPlayerDisplayName(entityPlayer.getScoreboardTeam(), entityPlayer.getName());
		event.setLeaveMessage(ChatColor.YELLOW + playerDisplayName + ChatColor.YELLOW + " left the game.");
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		String playerDisplayName = ScoreboardTeam.getPlayerDisplayName(entityPlayer.getScoreboardTeam(), entityPlayer.getName());
		event.setQuitMessage(ChatColor.YELLOW + playerDisplayName + ChatColor.YELLOW + " left the game.");
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		String playerDisplayName = ScoreboardTeam.getPlayerDisplayName(entityPlayer.getScoreboardTeam(), entityPlayer.getName());

		if (plugin.getGameManager().isPlaying() && plugin.getTeamManager().hasTeam(player) && !plugin.getTeamManager().isSpectating(player)) {
			if (plugin.getTeamManager().getDeaths(player) >= plugin.getConfig().getInt("player-lives", 3)) {
				plugin.getTeamManager().setSpectating(player, 1);
				player.setAllowFlight(true);
				player.setFlying(true);
				player.sendMessage(ChatColor.RED + "You have no more lives and will now be set as a spectator.");
				plugin.getServer().broadcastMessage(ChatColor.YELLOW + playerDisplayName + ChatColor.YELLOW + " has been defeated an is now spectating.");
			} else {
				plugin.getTeamManager().kitPlayer(player);
			}
		}

		if (plugin.getTeamManager().hasSpawn(player)) {
			event.setRespawnLocation(plugin.getTeamManager().getSpawn(player));
		}
	}
}
