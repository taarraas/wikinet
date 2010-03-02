package wikinet.persistence.dao.impl;

import wikinet.persistence.dao.ConnectionDao;
import wikinet.persistence.domain.Connection;
import wikinet.persistence.domain.ConnectionPK;

import javax.persistence.EntityExistsException;

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

}
