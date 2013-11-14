package castlewars.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import castlewars.CastleWars;
import castlewars.command.exception.WrongUsageException;

public class CommandSetKit extends CommandBase {

	public CommandSetKit(CastleWars plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "setkit";
	}

	@Override
	public void performCommand(CommandSender sender, Command command, String[] args) {
		if (args.length < 1) {
			throw new WrongUsageException(command.getUsage());
		} else {
			Player player = getPlayerFromCommandSender(sender);
			if (plugin.getTeamManager().isTeam(args[0])) {
				plugin.getTeamManager().setKit(player, args[0]);
				notifyAdmins(sender, String.format("Set kit for team %s", args[0]));
			} else {
				throw new CommandException(String.format("No team was found by the name of '%s'", args[0]));
			}
		}
	}

	@Override
	public List<String> addTabCompletionOptions(CommandSender sender, Command command, String[] args) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] { "red", "blue" }) : null;
	}
}
