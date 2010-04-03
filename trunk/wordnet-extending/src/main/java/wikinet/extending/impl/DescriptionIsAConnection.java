package wikinet.extending.impl;

import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Synset;
import wikinet.extending.ConnectionsMaker;
import wikinet.wiki.ArticleReference;
import wikinet.wiki.dao.WikiDao;

/**
 * First sentence of article says, that * %title% is a %something% *
 * Find what means %something%, synset for it, and add connection.
 *
 * @author taras, shyiko
 */
public class DescriptionIsAConnection implements ConnectionsMaker {
    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private WikiDao wikiDao;

    /**
     * 
     * @param synset
     * @param article
     */
    @Override
    public void addConnections(Synset synset, ArticleReference article) {
        String descriptionText=null;
        
    }

}
