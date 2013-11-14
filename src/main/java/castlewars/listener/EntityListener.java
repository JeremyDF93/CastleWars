package castlewars.listener;

import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.block.Block;
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

public class EntityListener implements Listener {
	private CastleWars plugin;

	public EntityListener(CastleWars plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();

		if (event instanceof EntityDamageByEntityEvent) {
			Entity otherEntity = ((EntityDamageByEntityEvent) event).getDamager();

			if (entity instanceof Player && otherEntity instanceof Player) {
				Player player = (Player) entity;
				Player otherPlayer = (Player) otherEntity;

				if (plugin.getGameManager().isGameInProgress()) {
					if (plugin.getGameManager().isBuilding() || plugin.getGameManager().isPreparing()) {
						event.setCancelled(true);
					}

					if (plugin.getTeamManager().isSpectating(otherPlayer)) {
						event.setCancelled(true);
					}

					if (plugin.getTeamManager().hasTeam(player)) {
						if (plugin.getTeamManager().getTeam(player).hasPlayer(otherPlayer)) {
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
