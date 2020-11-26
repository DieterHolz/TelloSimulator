package tellosimulator.log;

/**
 * The Logger class which adds {@code LogRecords} to the {@code Log}
 * @see LogRecord
 * @see Log
 */
public class Logger {

    private final Log log;
    private final String context;

    public Logger(Log log, String context) {
        this.log = log;
        this.context = context;
    }

    public void log(LogRecord record) {
        log.offer(record);
    }

    public void debug(String msg) {
        log(new LogRecord(LogLevel.DEBUG, context, msg));
    }

    public void info(String msg) {
        log(new LogRecord(LogLevel.INFO, context, msg));
    }

    public void warn(String msg) {
        log(new LogRecord(LogLevel.WARN, context, msg));
    }

    public void error(String msg) {
        log(new LogRecord(LogLevel.ERROR, context, msg));
    }

    public Log getLog() {
        return log;
    }

}
