package castlewars.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import castlewars.CastleWars;
import castlewars.TeamManager;
import castlewars.command.exception.WrongUsageException;

public class CommandKit extends CommandBase {

	public CommandKit(CastleWars plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "kit";
	}

	@Override
	public void performCommand(CommandSender sender, Command command, String[] args) {
		if (args.length >= 1) {
			Player player;
			if (args.length == 1) {
				player = this.getPlayerByName(args[0]);
			} else {
				player = this.getPlayerFromCommandSender(sender);
			}

			TeamManager teamManager = plugin.getTeamManager();
			if (teamManager.hasTeam(player)) {
				teamManager.kitPlayer(player);
				notifyAdmins(sender, String.format("Resupplied %s", player.getName()));
			} else {
				throw new CommandException("The player must be on a team to be able to kit them!");
			}
		} else {
			throw new WrongUsageException(command.getUsage());
		}
	}

	@Override
	public List<String> addTabCompletionOptions(CommandSender sender, Command command, String[] args) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, getPlayers()) : null;
	}
}
