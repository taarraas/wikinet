package wikinet.extending.impl;

import wikinet.db.domain.Synset;
import wikinet.extending.ConnectionsMaker;
import wikinet.wiki.ArticleReference;

/**
 * First sentence of article says, that * %title% is a %something% *
 * Find what means %something%, synset for it, and add connection.
 *
 * @author taras, shyiko
 */
public class DescriptionIsAConnection implements ConnectionsMaker {

    @Override
    public void addConnections(Synset synset, ArticleReference article) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
