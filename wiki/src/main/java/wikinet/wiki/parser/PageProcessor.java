package wikinet.wiki.parser;

/**
 * @author shyiko
 * @since Apr 18, 2010
 */
public interface PageProcessor {

    void process(String title, String text);
    
}
