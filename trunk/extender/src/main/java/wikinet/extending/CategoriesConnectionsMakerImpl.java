package wikinet.extending;

import wikinet.db.domain.Page;
import wikinet.db.domain.Synset;
import wikinet.extending.ConnectionsMaker;

/**
 * There are categories in wiki. Use them to add new connections.
 *
 * @author taras, shyiko
 */
public class CategoriesConnectionsMakerImpl implements ConnectionsMaker {

    @Override
    public void addConnections(Synset synset, Page page) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
