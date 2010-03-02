package wikinet.persistence.dao;

import wikinet.persistence.domain.Synset;

import java.util.List;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public interface SynsetDao extends GenericDao<Synset, Long> {

    public List<Synset> getConnected(Synset synset);
    
}
