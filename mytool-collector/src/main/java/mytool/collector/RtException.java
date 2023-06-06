package mytool.collector;

public class RtException extends RuntimeException {

	public RtException() {
		super();
	}

	public RtException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RtException(String message, Throwable cause) {
		super(message, cause);
	}

	public RtException(String message) {
		super(message);
	}

	public RtException(Throwable cause) {
		super(cause);
	}

	public static RuntimeException toRtException(Throwable t) {

		if (t instanceof RuntimeException) {
			return (RuntimeException) t;
		} else {
			return new RtException(t);
		}
	}

	public static RtException toRtException(String string) {
		//
		return new RtException(string);
	}

}
