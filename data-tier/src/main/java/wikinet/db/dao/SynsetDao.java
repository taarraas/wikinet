package wikinet.db.dao;

import wikinet.db.domain.Synset;

import java.util.List;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public interface SynsetDao extends GenericDao<Synset, Long> {

    List<Synset> getConnected(Synset synset);
    
}
