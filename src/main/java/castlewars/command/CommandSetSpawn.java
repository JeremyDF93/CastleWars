package castlewars.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import castlewars.CastleWars;
import castlewars.TeamManager;
import castlewars.command.exception.WrongUsageException;

public class CommandSetSpawn extends CommandBase {

	public CommandSetSpawn(CastleWars plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "setspawn";
	}

	@Override
	public void performCommand(CommandSender sender, Command command, String[] args) {
		if (args.length >= 1) {
			Player player = getPlayerFromCommandSender(sender);

			TeamManager teamManager = plugin.getTeamManager();
			if (teamManager.isTeam(args[0])) {
				teamManager.setSpawn(player.getLocation(), args[0]);
				notifyAdmins(sender, String.format("Set spawn for team %s", args[0]));
			} else {
				throw new CommandException(String.format("No team was found by the name of '%s'", args[0]));
			}
		} else {
			throw new WrongUsageException(command.getUsage());
		}
	}

	@Override
	public List<String> addTabCompletionOptions(CommandSender sender, Command command, String[] args) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] { "red", "blue" }) : null;
	}
}
