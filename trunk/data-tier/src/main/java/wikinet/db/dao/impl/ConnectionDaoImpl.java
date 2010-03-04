package wikinet.db.dao.impl;

import wikinet.db.dao.ConnectionDao;
import wikinet.db.domain.Connection;
import wikinet.db.domain.ConnectionPK;
import wikinet.db.domain.Synset;

import javax.persistence.EntityExistsException;
import java.util.List;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public class ConnectionDaoImpl extends GenericDaoImpl<Connection, ConnectionPK> implements ConnectionDao {

    @Override
    public void save(Connection obj) {
        if (this.findById(new ConnectionPK(obj.getSecondSynset(), obj.getFirstSynset())) != null)
            throw new EntityExistsException();
        super.save(obj);
    }

    @Override
    public List<Connection> getConnections(Synset from, Synset to) {
        return getHibernateTemplate().findByNamedQueryAndNamedParam("Connection.getConnections",
                new String[] {"firstSynsetId", "secondSynsetId"},
                new Object[] {from.getId(), to.getId()});
    }
    
}
