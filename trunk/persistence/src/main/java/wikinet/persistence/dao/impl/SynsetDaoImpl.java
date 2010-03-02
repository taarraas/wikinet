package wikinet.persistence.dao.impl;

import wikinet.persistence.dao.SynsetDao;
import wikinet.persistence.domain.Synset;

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
