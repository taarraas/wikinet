package wikinet.db.dao.impl;

import wikinet.db.dao.ConnectionDao;
import wikinet.db.domain.Connection;
import wikinet.db.domain.ConnectionPK;
import wikinet.db.domain.Synset;

import java.util.List;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public class ConnectionDaoImpl extends GenericDaoImpl<Connection, ConnectionPK> implements ConnectionDao {

    @Override
    public List<Connection> getConnections(Synset from, Synset to) {
        return getHibernateTemplate().findByNamedQueryAndNamedParam("Connection.getConnections",
                new String[] {"firstSynsetId", "secondSynsetId"},
                new Object[] {from.getId(), to.getId()});
    }
    
}
