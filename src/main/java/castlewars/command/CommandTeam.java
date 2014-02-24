package castlewars.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import castlewars.CastleWars;
import castlewars.TeamManager;
import castlewars.command.exception.WrongUsageException;

public class CommandTeam extends CommandBase {

	public CommandTeam(CastleWars plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "team";
	}

	@Override
	public void performCommand(CommandSender sender, Command command, String[] args) {
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("join")) {
				if (args.length < 2) {
					if (args.length < 3 && !(sender instanceof Player)) {
						throw new WrongUsageException("/team join <team> <player> [keepInventory]");
					} else {
						throw new WrongUsageException("/team join <team> [player] [keepInventory]");
					}
				}

				joinTeam(sender, args);
			} else if (args[0].equalsIgnoreCase("leave")) {
				if (args.length < 2 && !(sender instanceof Player)) {
					throw new WrongUsageException("/team leave <player>");
				}

				leaveTeam(sender, args);
			}
		} else {
			throw new WrongUsageException(command.getUsage());
		}
	}

	@Override
	public List<String> addTabCompletionOptions(CommandSender sender, Command command, String[] args) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, new String[] { "join", "leave" });
		} else {
			if (args[0].equalsIgnoreCase("join")) {
				if (args.length == 2) {
					return getListOfStringsMatchingLastWord(args, new String[] { "red", "blue" });
				} else if (args.length == 3) {
					return getListOfStringsMatchingLastWord(args, getPlayers());
				} else if (args.length == 4) {
					return getListOfStringsMatchingLastWord(args, new String[] { "true", "false" });
				}
			} else if (args[0].equalsIgnoreCase("leave")) {
				if (args.length == 2) {
					return getListOfStringsMatchingLastWord(args, getPlayers());
				}
			}
		}

		return null;
	}

	public void joinTeam(CommandSender sender, String[] args) {
		Player player;
		if (args.length >= 3) {
			player = getPlayerByName(args[2]);
		} else {
			player = getPlayerFromCommandSender(sender);
		}

		TeamManager teamManager = plugin.getTeamManager();
		if (teamManager.isTeam(args[1])) {
			teamManager.addPlayer(player, args[1]);

			if (plugin.getGameManager().isGameInProgress()) {
				teamManager.setKills(player, 0);
				teamManager.setDeaths(player, 0);
				teamManager.setSpectating(player, 0);

				if (!player.getGameMode().equals(GameMode.CREATIVE)) {
					player.setFlying(false);
					player.setAllowFlight(false);
				}

				if (teamManager.hasSpawn(player)) {
					player.teleport(plugin.getTeamManager().getSpawn(player));
				}

				boolean keepInventory = args.length == 4 ? parseBoolean(args[3]) : false;
				if (!keepInventory) {
					teamManager.kitPlayer(player);
				}
			}

			notifyAdmins(sender, String.format("Added %s to team %s", player.getName(), args[1]));
		} else {
			throw new CommandException(String.format("No team was found by the name of %s", args[1]));
		}
	}

	public void leaveTeam(CommandSender sender, String[] args) {
		Player player;
		if (args.length >= 2) {
			player = getPlayerByName(args[1]);
		} else {
			player = getPlayerFromCommandSender(sender);
		}

		TeamManager teamManager = plugin.getTeamManager();
		if (teamManager.hasTeam(player)) {
			teamManager.removePlayer(player);

			if (plugin.getGameManager().isGameInProgress()) {
				teamManager.setSpectating(player, 1);

				player.setAllowFlight(true);
				player.setFlying(true);
				player.sendMessage(ChatColor.RED + "You have been set as a spectator");
			}

			notifyAdmins(sender, String.format("Removed %s from their team", player.getName()));
		} else {
			throw new CommandException(String.format("Could not remove %s from their team", player.getName()));
		}
	}
}
