package castlewars.command.exception;

import org.bukkit.command.CommandException;

@SuppressWarnings("serial")
public class NumberInvalidException extends CommandException {
	public NumberInvalidException(String msg) {
		super(msg);
	}
}
