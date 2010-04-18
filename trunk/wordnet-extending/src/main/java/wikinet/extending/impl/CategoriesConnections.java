package wikinet.extending.impl;

import wikinet.db.domain.Synset;
import wikinet.extending.ConnectionsMaker;
import wikinet.wiki.parser.prototype.PagePrototype;

/**
 * There are categories in wiki. Use them to add new connections.
 *
 * @author taras, shyiko
 */
public class CategoriesConnections implements ConnectionsMaker {

    @Override
    public void addConnections(Synset synset, PagePrototype pagePrototype) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
