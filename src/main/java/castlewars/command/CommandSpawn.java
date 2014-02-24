package castlewars.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import castlewars.CastleWars;

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
		Player player;
		if (args.length == 1) {
			player = this.getPlayerByName(args[0]);
		} else {
			player = this.getPlayerFromCommandSender(sender);
		}

		if (plugin.getTeamManager().hasSpawn(player)) {
			player.teleport(plugin.getTeamManager().getSpawn(player));
			notifyAdmins(sender, String.format("Sent %s to their spawn", player.getName()));
		} else {
			throw new CommandException("The player must be on a team to be able to send them to their spawn!");
		}
	}

	@Override
	public List<String> addTabCompletionOptions(CommandSender sender, Command command, String[] args) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, getPlayers()) : null;
	}
}
