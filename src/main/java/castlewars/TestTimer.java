package castlewars;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TestTimer extends BukkitRunnable {
	private boolean running;
	private CastleWars plugin;

	public TestTimer(CastleWars plugin) {
		this.plugin = plugin;
	}

	public void start() {
		if (!running) {
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
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				Location location = player.getLocation();
				if (plugin.getTeamManager().isSpectating(player) && plugin.getGameManager().isGameInProgress()) {
					if (location.getY() < 116) {
						location.setY(116);
						player.setFlying(true);
						player.teleport(location);
					}
				}
			}
		}
	}
}
