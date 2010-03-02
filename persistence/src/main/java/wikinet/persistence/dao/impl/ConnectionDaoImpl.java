package wikinet.persistence.dao.impl;

import wikinet.persistence.dao.ConnectionDao;
import wikinet.persistence.domain.Connection;
import wikinet.persistence.domain.ConnectionPK;
import wikinet.persistence.domain.Synset;

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
    public List<Synset> getConnectedSynsets(Synset synset) {
        String query = String.format(
                "SELECT * FROM Synset x WHERE x.id IN (" +
                "SELECT s.id FROM Connection c INNER JOIN Synset s ON c.ssid = s.id WHERE c.fsid = %1$s " +
                "UNION SELECT s.id FROM Connection c INNER JOIN Synset s ON c.fsid = s.id WHERE c.ssid = %1$s " +
                ")", synset.getId());
        return getSession().createSQLQuery(query).addEntity(Synset.class).list();
    }
}
