package castlewars.listener;

import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import castlewars.CastleWars;
import castlewars.TeamManager;

public class EntityListener implements Listener {
	private CastleWars plugin;

	public EntityListener(CastleWars plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player player = (Player) entity;

			if (event instanceof EntityDamageByEntityEvent) {
				Player otherPlayer = null;

				Entity otherEntity = ((EntityDamageByEntityEvent) event).getDamager();
				if (otherEntity instanceof Player) {
					otherPlayer = (Player) otherEntity;
				} else if (otherEntity instanceof Arrow) {
					Arrow arrow = (Arrow) otherEntity;
					if (arrow.getShooter() instanceof Player) {
						otherPlayer = (Player) arrow.getShooter();
					}
				}

				if (otherPlayer != null) {
					if (plugin.getGameManager().isGameInProgress()) {
						TeamManager teamManager = plugin.getTeamManager();
						if (teamManager.hasTeam(player)) {
							if (teamManager.getTeam(player).hasPlayer(otherPlayer)) {
								event.setCancelled(true);
							}
						}
						
						if (teamManager.isSpectating(otherPlayer)) {
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if (plugin.getGameManager().isGameInProgress()) {
			Iterator<Block> iterator = event.blockList().iterator();

			while (iterator.hasNext()) {
				Block block = iterator.next();

				if (block.getType().equals(Material.EMERALD_BLOCK) || block.getType().equals(Material.BEACON)) {
					iterator.remove();
				}
			}
		}
	}

	@EventHandler
	public void onExplosionPrime(ExplosionPrimeEvent event) {
		if (plugin.getGameManager().isBuilding() || plugin.getGameManager().isPreparing()) {
			if (event.getEntity() instanceof TNTPrimed) {
				event.getEntity().getWorld().getBlockAt(event.getEntity().getLocation()).setType(Material.TNT);
				event.setCancelled(true);
			}
		}
	}
}
