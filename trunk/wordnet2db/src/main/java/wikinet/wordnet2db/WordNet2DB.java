package wikinet.wordnet2db;

import java.io.IOException;

/**
 * @author shyiko
 * @since Mar 3, 2010
 */
public interface WordNet2DB {

    void importDirectory(String pathToWordnet) throws IOException;

}
