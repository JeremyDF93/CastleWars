package castlewars.command;

import net.minecraft.server.v1_6_R3.EntityPlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.scoreboard.Team;

import castlewars.CastleWars;
import castlewars.command.exception.WrongUsageException;

public class CommandTeamChat extends CommandBase {

	public CommandTeamChat(CastleWars plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "t";
	}

	public PermissionDefault getPermissionDefault() {
		return PermissionDefault.TRUE;
	}

	@Override
	public void performCommand(CommandSender sender, Command command, String[] args) {
		if (args.length < 1) {
			throw new WrongUsageException(command.getUsage());
		} else {
			Player player = getPlayerFromCommandSender(sender);
			EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
			Team team = plugin.getTeamManager().getTeam(player);

			if (team != null) {
				plugin.getTeamManager().sendMessage("*Team*<" + entityPlayer.getScoreboardDisplayName() + "> " + getString(args, 0), team);
			} else {
				throw new CommandException("You must be in a team to use team chat!");
			}
		}
	}
}
