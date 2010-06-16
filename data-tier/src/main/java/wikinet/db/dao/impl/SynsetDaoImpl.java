package wikinet.db.dao.impl;

import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Synset;
import wikinet.db.model.ConnectionType;

import java.util.List;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public class SynsetDaoImpl extends GenericDaoImpl<Synset, Long> implements SynsetDao {

    @Override
    public List<Synset> getConnected(Synset synset) {
        return getSession().getNamedQuery("Synset.getConnected")
               .setLong("synsetId", synset.getId()).list();
    }

    @Override
    public List<Synset> getConnected(Synset synset, ConnectionType connectionType) {
        return getSession().getNamedQuery("Synset.getConnectedWithConnectionType")
               .setLong("synsetId", synset.getId())
               .setLong("connectionType", connectionType.ordinal()).list();
    }

    @Override
    public List<Long> findAllId() {
        return getSession().createQuery("select c.id from Synset c").list();
    }
}
