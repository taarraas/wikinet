package wikinet.db;

/**
 * @author shyiko
 * @since May 24, 2010
 */
public interface DBController {

    void cleanDB(String... tablesToSkip);
    
}
