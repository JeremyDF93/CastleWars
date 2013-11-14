package castlewars;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import castlewars.command.CommandCastleWars;
import castlewars.command.CommandHandler;
import castlewars.command.CommandKit;
import castlewars.command.CommandSetKit;
import castlewars.command.CommandSetSpawn;
import castlewars.command.CommandSpawn;
import castlewars.command.CommandTeamChat;
import castlewars.listener.BlockListener;
import castlewars.listener.EntityListener;
import castlewars.listener.PlayerListener;

public class CastleWars extends JavaPlugin {
	private BlockListener blockListener = new BlockListener(this);
	private EntityListener entityListener = new EntityListener(this);
	private PlayerListener playerListener = new PlayerListener(this);
	private CommandHandler commandHandler = new CommandHandler(this);

	private GameManager gameManager;
	private TeamManager teamManager;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();

		gameManager = new GameManager(this);
		teamManager = new TeamManager(this);

		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(playerListener, this);
		pluginManager.registerEvents(entityListener, this);
		pluginManager.registerEvents(blockListener, this);

		commandHandler.registerCommand(new CommandCastleWars(this));
		commandHandler.registerCommand(new CommandTeamChat(this));
		commandHandler.registerCommand(new CommandSetSpawn(this));
		commandHandler.registerCommand(new CommandSpawn(this));
		commandHandler.registerCommand(new CommandKit(this));
		commandHandler.registerCommand(new CommandSetKit(this));
	}

	public void addPermission(Permission permission) {
		if (getConfig().getBoolean("no-op-permissions", false) && permission.getDefault().equals(PermissionDefault.OP)) {
			permission.setDefault(PermissionDefault.FALSE);
		}

		getServer().getPluginManager().addPermission(permission);
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	public TeamManager getTeamManager() {
		return teamManager;
	}
}
