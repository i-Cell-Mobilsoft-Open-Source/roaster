package hu.icellmobilsoft.roaster.tm4j.common.config;

public class InvalidConfigException extends RuntimeException {
    public InvalidConfigException() {
        super();
    }

    public InvalidConfigException(String message) {
        super(message);
    }

    public InvalidConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigException(Throwable cause) {
        super(cause);
    }
}
