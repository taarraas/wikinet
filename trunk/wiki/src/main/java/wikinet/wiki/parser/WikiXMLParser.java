package wikinet.wiki.parser;

import java.io.File;

/**
 * @author shyiko
 * @since Mar 30, 2010
 */
public interface WikiXMLParser {

    public void importFile(File file) throws Exception;
    public void importFile(String fileName) throws Exception;
    
}
