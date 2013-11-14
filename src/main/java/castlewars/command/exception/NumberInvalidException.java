package castlewars.command.exception;

@SuppressWarnings("serial")
public class NumberInvalidException extends NumberFormatException {
	public NumberInvalidException(String msg) {
		super(msg);
	}
}
