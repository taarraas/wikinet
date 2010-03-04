package wikinet.db.dao.impl;

import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Synset;

import java.util.List;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public class SynsetDaoImpl extends GenericDaoImpl<Synset, Long> implements SynsetDao {

    @Override
    public List<Synset> getConnected(Synset synset) {
        return getHibernateTemplate().findByNamedQueryAndNamedParam("Synset.getConnected", "synsetId", synset.getId());
    }
    
}
