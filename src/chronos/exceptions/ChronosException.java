package chronos.exceptions;

import java.lang.Exception;

class ChronosException extends Exception {
    public ChronosException() {}

    public ChronosException(String msg) {
        super(msg);
    }

    public ChronosException(Throwable cause) {
        super(cause);
    }

    public ChronosException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
