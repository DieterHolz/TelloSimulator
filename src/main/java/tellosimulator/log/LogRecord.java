package tellosimulator.log;

import java.util.Date;

/**
 * Wrapper class for a log entry consisting of a {@code Date}. {@code LogLevel}, a context String
 * (which class it was logged from) and a message String.
 * @see LogLevel
 * @see Logger
 */
public class LogRecord {
    private Date   timestamp;
    private LogLevel logLevel;
    private String context;
    private String message;

    public LogRecord(LogLevel logLevel, String context, String message) {
        this.timestamp = new Date();
        this.logLevel = logLevel;
        this.context   = context;
        this.message   = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public LogLevel getLevel() {
        return logLevel;
    }

    public String getContext() {
        return context;
    }

    public String getMessage() {
        return message;
    }
}
