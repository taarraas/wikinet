package wikinet.wiki.parser;

/**
 * @author shyiko
 * @since Apr 18, 2010
 */
public class ParseException extends RuntimeException {

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
