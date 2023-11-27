package chronos.exceptions;

import chronos.exceptions.ChronosException;

class NotImplementedException extends ChronosException {
    public NotImplementedException() {}

    public NotImplementedException(String msg) {
        super(msg);
    }

    public NotImplementedException(Throwable cause) {
        super(cause);
    }

    public NotImplementedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
