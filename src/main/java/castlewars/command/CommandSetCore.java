package castlewars.command;

import java.util.List;

import net.minecraft.server.v1_7_R3.TileEntity;
import net.minecraft.server.v1_7_R3.TileEntityBeacon;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.Player;

import castlewars.CastleWars;
import castlewars.command.exception.WrongUsageException;

public class CommandSetCore extends CommandBase {

	public CommandSetCore(CastleWars plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "setcore";
	}

	@Override
	public void performCommand(CommandSender sender, Command command, String[] args) {
		if (args.length >= 1) {
			Player player = getPlayerFromCommandSender(sender);

			if (plugin.getTeamManager().isTeam(args[0])) {
				Block block = player.getTargetBlock(null, 5);
				CraftWorld craftWorld = (CraftWorld) player.getWorld();
				TileEntity tileEntity = craftWorld.getTileEntityAt(block.getX(), block.getY(), block.getZ());
				if (tileEntity instanceof TileEntityBeacon) {
					TileEntityBeacon tileEntityBeacon = (TileEntityBeacon) tileEntity;
					tileEntityBeacon.a(args[0]);

					notifyAdmins(sender, String.format("Set core for team %s", args[0]));
				} else {
					throw new CommandException(String.format("Unable to set target block as core for team %s", args[0]));
				}
			} else {
				throw new CommandException(String.format("No team was found by the name of %s", args[0]));
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
