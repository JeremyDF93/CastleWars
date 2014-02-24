package castlewars.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import castlewars.CastleWars;
import castlewars.command.exception.WrongUsageException;

public class CommandCastleWars extends CommandBase {
	public CommandCastleWars(CastleWars plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "castlewars";
	}

	@Override
	public void performCommand(CommandSender sender, Command command, String[] args) {
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("start")) {
				if (!plugin.getGameManager().isGameInProgress()) {
					int buildTime = args.length >= 2 ? parseInt(args[1], 0) : 120;
					int prepareTime = args.length >= 3 ? parseInt(args[2], 1) : 5;
					boolean keepInventory = args.length >= 4 ? parseBoolean(args[3]) : false;

					plugin.getGameManager().start(buildTime, prepareTime, keepInventory);
				} else {
					throw new CommandException("The game is already running!");
				}
			}

			if (args[0].equalsIgnoreCase("stop")) {
				if (plugin.getGameManager().isGameInProgress()) {
					plugin.getGameManager().stop();
					plugin.getServer().broadcastMessage(ChatColor.RED + "The game has stopped!");
				} else {
					throw new CommandException("The game is not running!");
				}
			}
		} else {
			throw new WrongUsageException(command.getUsage());
		}
	}

	@Override
	public List<String> addTabCompletionOptions(CommandSender sender, Command command, String[] args) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] { "start", "stop" }) : null;
	}
}
