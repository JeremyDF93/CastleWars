package castlewars.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_7_R3.Block;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import castlewars.CastleWars;
import castlewars.command.exception.WrongUsageException;

public class CommandSiegeBlocks extends CommandBase {
	public CommandSiegeBlocks(CastleWars plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "siegeblocks";
	}

	@Override
	public void performCommand(CommandSender sender, Command command, String[] args) {
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("add")) {
				if (args.length < 2) {
					throw new WrongUsageException("/siegeblocks add <block>");
				}

				addSiegeBlock(sender, args);
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (args.length < 2) {
					throw new WrongUsageException("/siegeblocks remove <block>");
				}

				removeSiegeBlock(sender, args);
			} else if (args[0].equalsIgnoreCase("list")) {
				sender.sendMessage(getStringList(getSiegeMaterials().toArray(new String[0]), 0));
			}
		} else {
			throw new WrongUsageException(command.getUsage());
		}
	}

	@Override
	public List<String> addTabCompletionOptions(CommandSender sender, Command command, String[] args) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, new String[] { "add", "remove", "list" });
		} else {
			if (args[0].equalsIgnoreCase("add")) {
				return getListOfStringsMatchingLastWord(args, Block.REGISTRY.keySet());
			} else if (args[0].equalsIgnoreCase("remove")) {
				return getListOfStringsMatchingLastWord(args, getSiegeMaterials());
			}
		}

		return null;
	}

	public void addSiegeBlock(CommandSender sender, String[] args) {
		Material material = getBlockMaterialByName(args[1]);

		if (!plugin.getGameManager().isSiegeMaterial(material)) {
			plugin.getGameManager().addSiegeMaterial(material);
			notifyAdmins(sender, String.format("Added siege block %s", args[1]));
		} else {
			throw new CommandException(String.format("There is already a siege block with ID %s", args[1]));
		}
	}

	public void removeSiegeBlock(CommandSender sender, String[] args) {
		Material material = getBlockMaterialByName(args[1]);

		if (plugin.getGameManager().isSiegeMaterial(material)) {
			plugin.getGameManager().removeSiegeMaterial(material);
			notifyAdmins(sender, String.format("Removed siege block %s", args[1]));
		} else {
			throw new CommandException(String.format("There is not a siege block with ID %s", args[1]));
		}
	}

	public List<String> getSiegeMaterials() {
		List<String> list = new ArrayList<String>();
		for (Material material : plugin.getGameManager().getSiegeMaterials()) {
			list.add(getBlockNameByMaterial(material));
		}

		return list;
	}
}
