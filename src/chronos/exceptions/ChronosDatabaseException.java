package chronos.exceptions;

import chronos.exceptions.ChronosException;

public class ChronosDatabaseException extends ChronosException {
    public ChronosDatabaseException() {}

    public ChronosDatabaseException(String msg) {
        super(msg);
    }

    public ChronosDatabaseException(Throwable cause) {
        super(cause);
    }

    public ChronosDatabaseException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
