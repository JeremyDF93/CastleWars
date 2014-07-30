package castlewars.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.server.v1_7_R4.Block;
import net.minecraft.server.v1_7_R4.Item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import castlewars.CastleWars;
import castlewars.command.exception.NumberInvalidException;

import com.google.common.primitives.Doubles;

public abstract class CommandBase {
	protected CastleWars plugin;

	public CommandBase(CastleWars plugin) {
		this.plugin = plugin;
		this.addPermission(new Permission(this.getPermissionName(), this.getPermissionDefault()));
	}

	public abstract String getName();

	public abstract void performCommand(CommandSender sender, Command command, String[] args);

	public List<String> addTabCompletionOptions(CommandSender sender, Command command, String[] args) {
		return null;
	}

	public void addPermission(Permission permission) {
		plugin.addPermission(permission);
	}

	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.OP;
	}

	public String getPermissionName() {
		return "castlewars." + this.getName();
	}

	public Player getPlayerByName(String name) {
		Player player = plugin.getServer().getPlayer(name);

		if (player != null) {
			return player;
		} else {
			throw new CommandException(String.format("Can't find player %s", name));
		}
	}

	public Player getPlayerFromCommandSender(CommandSender sender) {
		if (sender instanceof Player) {
			return (Player) sender;
		} else {
			throw new CommandException("You must specify which player you wish to perform this action on");
		}
	}

	public String getString(String[] args, int index) {
		StringBuilder builder = new StringBuilder();
		for (int i = index; i < args.length; ++i) {
			if (i > index) {
				builder.append(" ");
			}

			builder.append(args[i]);
		}

		return builder.toString();
	}

	public String getStringList(String[] args, int index) {
		StringBuilder builder = new StringBuilder();
		for (int i = index; i < args.length; ++i) {
			if (i > index) {
				builder.append(", ");
			}

			builder.append(args[i]);
		}

		return builder.toString();
	}

	public boolean doesStringStartWith(String input, String prefix) {
		return prefix.regionMatches(true, 0, input, 0, input.length());
	}

	public List<String> getListOfStringsMatchingLastWord(String[] args, String... input) {
		String string = args[args.length - 1];
		ArrayList<String> list = new ArrayList<String>();

		for (int i = 0; i < input.length; ++i) {
			if (doesStringStartWith(string, input[i])) {
				list.add(input[i]);
			}
		}

		return list;
	}

	public List<String> getListOfStringsMatchingLastWord(String[] args, Iterable<String> iterable) {
		String string = args[args.length - 1];
		List<String> list = new ArrayList<String>();

		Iterator<String> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			String prefix = iterator.next();
			if (doesStringStartWith(string, prefix)) {
				list.add(prefix);
			}
		}

		return list;
	}

	public Material getBlockMaterialByName(String name) {
		if (Block.REGISTRY.b(name)) {
			Block block = ((Block) Block.REGISTRY.get(name));
			return Material.getMaterial(Block.REGISTRY.b(block));
		} else {
			try {
				int id = Integer.parseInt(name);
				if (Block.REGISTRY.b(id)) {
					return Material.getMaterial(id);
				}
			} catch (NumberFormatException e) {
				;
			}
		}

		throw new CommandException(String.format("There is no such block with ID %s", name));
	}

	public Material getItemMaterialByName(String name) {
		if (Item.REGISTRY.b(name)) {
			Item item = ((Item) Item.REGISTRY.get(name));
			return Material.getMaterial(Item.REGISTRY.b(item));
		} else {
			try {
				int id = Integer.parseInt(name);
				if (Item.REGISTRY.b(id)) {
					return Material.getMaterial(id);
				}
			} catch (NumberFormatException e) {
				;
			}
		}

		throw new CommandException(String.format("There is no such item with ID %s", name));
	}

	public String getBlockNameByMaterial(Material material) {
		return Block.REGISTRY.c(Block.REGISTRY.a(material.getId()));
	}

	public String getItemNameByMaterial(Material material) {
		return Item.REGISTRY.c(Item.REGISTRY.a(material.getId()));
	}

	public void notifyAdmins(CommandSender sender, String message) {
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (sender == player) {
				player.sendMessage(ChatColor.GRAY + message);
			} else {
				if (player.isOp()) {
					player.sendMessage(ChatColor.GRAY + "[" + sender.getName() + "] " + message);
				}
			}
		}

		if (sender instanceof ConsoleCommandSender) {
			plugin.getLogger().info(message);
		} else {
			plugin.getLogger().info("[" + sender.getName() + "] " + message);
		}
	}

	public List<String> getPlayers() {
		List<String> list = new ArrayList<String>();

		Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
		for (Player player : players) {
			list.add(player.getName());
		}
		return list;
	}

	public double getCoordinate(CommandSender sender, double current, String input) {
		return this.getCoordinate(sender, current, input, -30000000, 30000000);
	}

	public double getCoordinate(CommandSender sender, double current, String input, int min, int max) {
		boolean relative = input.startsWith("~");
		double result = relative ? current : 0;

		if (!relative || input.length() > 1) {
			boolean exact = input.contains(".");
			if (relative) {
				input = input.substring(1);
			}

			result += parseDouble(input);

			if (!exact && !relative) {
				result += 0.5f;
			}
		}

		if (min != 0 || max != 0) {
			if (result < min) {
				throw new NumberInvalidException(String.format("The number you have entered (%d) is too small, it must be at least %d", result, min));
			}

			if (result > max) {
				throw new NumberInvalidException(String.format("The number you have entered (%d) is too big, it must be at most %d", result, max));
			}
		}

		return result;
	}

	public boolean parseBoolean(String input) {
		try {
			return Boolean.parseBoolean(input);
		} catch (NumberFormatException e) {
			throw new NumberInvalidException(String.format("%s is not true or false", input));
		}
	}

	public int parseInt(String input) {
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			throw new NumberInvalidException(String.format("%s is not a valid number", input));
		}
	}

	public int parseInt(String input, int min) {
		return this.parseInt(input, min, Integer.MAX_VALUE);
	}

	public int parseInt(String input, int min, int max) {
		int result = this.parseInt(input);

		if (result < min) {
			throw new NumberInvalidException(String.format("The number you have entered (%d) is too small, it must be at least %d", result, min));
		} else if (result > max) {
			throw new NumberInvalidException(String.format("The number you have entered (%d) is too big, it must be at most %d", result, max));
		} else {
			return result;
		}
	}

	public double parseDouble(String input) {
		try {
			double result = Double.parseDouble(input);

			if (!Doubles.isFinite(result)) {
				throw new NumberInvalidException(String.format("%s is not a valid number", input));
			} else {
				return result;
			}
		} catch (NumberFormatException e) {
			throw new NumberInvalidException(String.format("%s is not a valid number", input));
		}
	}
}
