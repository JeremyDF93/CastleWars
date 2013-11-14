package castlewars.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import castlewars.CastleWars;
import castlewars.command.exception.WrongUsageException;

public class CommandSpawn extends CommandBase {

	public CommandSpawn(CastleWars plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "spawn";
	}

	@Override
	public void performCommand(CommandSender sender, Command command, String[] args) {
		if (args.length < 1) {
			throw new WrongUsageException(command.getUsage());
		} else {
			Player player = this.getPlayerFromCommandSender(sender, args[0]);
			if (plugin.getTeamManager().hasSpawn(player)) {
				player.teleport(plugin.getTeamManager().getSpawn(player));
				notifyAdmins(sender, String.format("Sent %s to their spawn", args[0]));
			} else {
				throw new CommandException("The player must be on a team to be able to send them to their spawn!");
			}
		}
	}

	@Override
	public List<String> addTabCompletionOptions(CommandSender sender, Command command, String[] args) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getAllUsernames()) : null;
	}
}
