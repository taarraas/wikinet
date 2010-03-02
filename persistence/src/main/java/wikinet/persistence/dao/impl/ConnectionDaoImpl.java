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
        return null;
/*
        getSession().createSQLQuery().
        getHibernateTemplate().
*/
/*
        query = "select c1.firstSynset.id from Connection c1 where c1.secondSynset.id = :synsetId union " +
                "select c2.secondSynset.id from Connection c2 where c2.firstSynset.id = :synsetId")
*/

/*
        return getHibernateTemplate().findByNamedQueryAndNamedParam("Connection.findAllConnectedSynsets",
                "synsetId", synset.getId());
*/
    }
}
