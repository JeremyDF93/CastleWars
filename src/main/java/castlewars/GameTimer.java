package castlewars;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTimer extends BukkitRunnable {
	private CastleWars plugin;

	private int buildTime;
	private int prepareTime;

	private boolean running;

	public boolean building;
	public boolean preparing;
	public boolean playing;

	public GameTimer(CastleWars plugin, int buildTime, int prepareTime) {
		this.plugin = plugin;
		this.buildTime = buildTime;
		this.prepareTime = prepareTime;
	}

	public void start() {
		if (!running) {
			if (buildTime == 0) {
				building = false;
				preparing = true;
				plugin.getServer().broadcastMessage(ChatColor.RED + "The game has started! You have " + prepareTime + " minute(s) to setup for battle!");
			} else {
				building = true;
				plugin.getServer().broadcastMessage(ChatColor.RED + "The game has started! You have " + buildTime + " minute(s) to build!");
			}

			running = true;
		}
	}

	public void stop() {
		if (running) {
			running = false;
			cancel();
		}
	}

	@Override
	public void run() {
		if (running) {
			if (building) {
				if (--buildTime == 0) {
					building = false;
					preparing = true;
					plugin.getServer().broadcastMessage(ChatColor.RED + "Build time is up! You have " + prepareTime + " minute(s) to setup for battle!");
				} else {
					if (buildTime % 30 == 0 || buildTime <= 30 && buildTime % 10 == 0 || buildTime <= 5) {
						plugin.getServer().broadcastMessage(ChatColor.RED + String.valueOf(buildTime) + " minute(s) left to build!");
					}
				}

				return;
			}

			if (preparing) {
				if (--prepareTime == 0) {
					preparing = false;
					playing = true;
					plugin.getServer().broadcastMessage(ChatColor.RED + "Setup time is up! Let the battle begin!");
				} else {
					if (prepareTime % 5 == 0 || prepareTime <= 5) {
						plugin.getServer().broadcastMessage(ChatColor.RED + String.valueOf(prepareTime) + " minute(s) left to setup!");
					}
				}

				return;
			}

			if (playing) {
				for (Player player : plugin.getServer().getOnlinePlayers()) {
					if (plugin.getTeamManager().hasTeam(player)) {
						plugin.getTeamManager().setKills(player, 0);
						plugin.getTeamManager().setDeaths(player, 0);
					}
				}
				
				stop();
			}
		}
	}
}
