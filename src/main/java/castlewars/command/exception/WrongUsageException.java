package castlewars.command.exception;

@SuppressWarnings("serial")
public class WrongUsageException extends RuntimeException {
	public WrongUsageException(String msg) {
		super(msg);
	}
}
