package castlewars;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.NBTCompressedStreamTools;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagDouble;
import net.minecraft.server.v1_7_R4.NBTTagFloat;
import net.minecraft.server.v1_7_R4.NBTTagList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class TeamManager {
	private CastleWars plugin;

	private ScoreboardManager scoreboardManager;
	private Scoreboard scoreboard;

	private Objective kills;
	private Objective deaths;
	private Objective spectating;

	public TeamManager(CastleWars plugin) {
		this.plugin = plugin;
		this.scoreboardManager = this.plugin.getServer().getScoreboardManager();
		this.scoreboard = this.scoreboardManager.getMainScoreboard();

		this.kills = registerObjective("kills", "Players Killed", "playerKillCount");
		this.deaths = registerObjective("deaths", "Deaths", "deathCount");
		this.spectating = registerObjective("spectating", "Spectating", "dummy");

		registerTeam("red", "Red", ChatColor.RED);
		registerTeam("blue", "Blue", ChatColor.BLUE);

		this.kills.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.deaths.setDisplaySlot(DisplaySlot.PLAYER_LIST);
	}

	public void addPlayer(Player player, String teamName) {
		if (getTeam(teamName) != null) {
			getTeam(teamName).addPlayer(player);
		}
	}

	public void addPlayer(Player player, Team team) {
		team.addPlayer(player);
	}

	public int getDeaths(Player player) {
		return deaths.getScore(player).getScore();
	}

	public int getKills(Player player) {
		return kills.getScore(player).getScore();
	}

	public Team getTeam(Player player) {
		return scoreboard.getPlayerTeam(player);
	}

	public Location getSpawn(Player player) {
		return getSpawn(player, getTeam(player));
	}

	public Location getSpawn(Player player, Team team) {
		NBTTagCompound tagCompound = getTeamTag(team);
		if (tagCompound != null) {
			NBTTagList tagListPos = (NBTTagList) tagCompound.get("Pos");
			NBTTagList tagListRotation = (NBTTagList) tagCompound.get("Rotation");

			Location location = new Location(player.getWorld(), tagListPos.d(0), tagListPos.d(1), tagListPos.d(2),
					tagListRotation.e(0), tagListRotation.e(1));

			return location;
		}

		return null;
	}

	public Team getTeam(String teamName) {
		return scoreboard.getTeam(teamName);
	}

	public boolean hasSpawn(Player player) {
		return hasSpawn(getTeam(player));
	}

	public boolean hasSpawn(Team team) {
		NBTTagCompound tagCompound = getTeamTag(team);
		if (tagCompound != null) {
			return tagCompound.hasKey("Pos") && tagCompound.hasKey("Rotation");
		}

		return false;
	}

	public boolean hasTeam(Player player) {
		return scoreboard.getPlayerTeam(player) != null;
	}

	public boolean isTeam(String teamName) {
		return scoreboard.getTeam(teamName) != null;
	}

	public boolean isSpectating(Player player) {
		return spectating.getScore(player).getScore() == 1;
	}

	private Objective registerObjective(String name, String displayName, String criteria) {
		Objective objective = this.scoreboard.getObjective(name);
		if (objective == null) {
			objective = this.scoreboard.registerNewObjective(name, criteria);
			objective.setDisplayName(displayName);
		}

		return objective;
	}

	private void registerTeam(String teamName, String displayName, ChatColor teamColor) {
		if (scoreboard.getTeam(teamName) == null) {
			Team team = scoreboard.registerNewTeam(teamName);
			team.setPrefix(teamColor.toString());
			team.setSuffix(ChatColor.RESET.toString());
			team.setDisplayName(team.getPrefix() + displayName + team.getSuffix());
		}
	}

	public void removePlayer(Player player) {
		if (hasTeam(player)) {
			scoreboard.getPlayerTeam(player).removePlayer(player);
		}
	}

	public void sendMessage(String message, Team team) {
		Iterator<OfflinePlayer> iterator = team.getPlayers().iterator();

		while (iterator.hasNext()) {
			OfflinePlayer offlinePlayer = iterator.next();

			if (offlinePlayer.isOnline()) {
				Player player = offlinePlayer.getPlayer();
				player.sendMessage(message);
			}
		}
	}

	public void setDeaths(Player player, int i) {
		deaths.getScore(player).setScore(i);
	}

	public void setKills(Player player, int i) {
		kills.getScore(player).setScore(i);
	}

	public void setSpawn(Location location, Player player) {
		setSpawn(location, getTeam(player));
	}

	public void setSpawn(Location location, String teamName) {
		setSpawn(location, getTeam(teamName));
	}

	public void setSpawn(Location location, Team team) {
		NBTTagCompound tagCompound = getTeamTag(team);
		if (tagCompound != null) {
			tagCompound.remove("Pos");
			tagCompound.remove("Rotation");
		} else {
			tagCompound = new NBTTagCompound();
		}

		tagCompound.set("Pos", getDataTag(new double[] { location.getX(), location.getY(), location.getZ() }));
		tagCompound.set("Rotation", getDataTag(new float[] { location.getYaw(), location.getPitch() }));

		setTeamTag(tagCompound, team);
	}

	public void setSpectating(Player player, int i) {
		spectating.getScore(player).setScore(i);
	}

	public NBTTagCompound getTeamTag(Team team) {
		try {
			return NBTCompressedStreamTools.a(new FileInputStream(new File(plugin.getDataFolder(),
					String.format("team_%s.dat", team.getName()))));
		} catch (Exception e) {
			plugin.getLogger().warning(String.format("Failed to load team data file 'team_%s.dat'", team.getName()));
		}
		return null;
	}

	public void setTeamTag(NBTTagCompound tagCompound, Team team) {
		try {
			NBTCompressedStreamTools.a(tagCompound, new FileOutputStream(new File(plugin.getDataFolder(),
					String.format("team_%s.dat", team.getName()))));
		} catch (Exception e) {
			plugin.getLogger().warning(String.format("Failed to save team data file 'team_%s.dat'", team.getName()));
		}
	}

	private NBTTagList getDataTag(double... d) {
		NBTTagList tagList = new NBTTagList();

		for (int i = 0; i < d.length; i++) {
			tagList.add(new NBTTagDouble(d[i]));
		}

		return tagList;
	}

	private NBTTagList getDataTag(float... f) {
		NBTTagList tagList = new NBTTagList();

		for (int i = 0; i < f.length; i++) {
			tagList.add(new NBTTagFloat(f[i]));
		}

		return tagList;
	}

	public void kitPlayer(Player player) {
		EntityHuman entityHuman = ((CraftPlayer) player).getHandle();

		if (plugin.getTeamManager().hasTeam(player)) {
			NBTTagCompound tagCompound = getTeamTag(getTeam(player));
			entityHuman.inventory.b((NBTTagList) tagCompound.get("Inventory"));
		}
	}

	public void setKit(Player player, String teamName) {
		setKit(player, getTeam(teamName));
	}

	public void setKit(Player player, Team team) {
		EntityHuman entityHuman = ((CraftPlayer) player).getHandle();

		NBTTagCompound tagCompound = getTeamTag(team);
		if (tagCompound != null) {
			tagCompound.remove("Inventory");
		} else {
			tagCompound = new NBTTagCompound();
		}

		NBTTagList tagList = new NBTTagList();
		entityHuman.inventory.a(tagList);
		tagCompound.set("Inventory", tagList);

		setTeamTag(tagCompound, team);
	}
}
