package castlewars.command;

import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.ScoreboardTeam;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import castlewars.CastleWars;
import castlewars.TeamManager;
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
		if (args.length >= 1) {
			Player player = getPlayerFromCommandSender(sender);
			EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
			String playerDisplayName = ScoreboardTeam.getPlayerDisplayName(entityPlayer.getScoreboardTeam(), entityPlayer.getName());

			TeamManager teamManager = plugin.getTeamManager();
			if (teamManager.hasTeam(player)) {
				teamManager.sendMessage("*Team*<" + playerDisplayName + "> " + getString(args, 0), teamManager.getTeam(player));
			} else {
				throw new CommandException("You must be in a team to use team chat!");
			}
		} else {
			throw new WrongUsageException(command.getUsage());
		}
	}
}
