package wikinet.db.dao;

import wikinet.db.domain.Connection;
import wikinet.db.domain.ConnectionPK;
import wikinet.db.domain.Synset;

import java.util.List;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public interface ConnectionDao extends GenericDao<Connection, ConnectionPK> {

    List<Connection> getConnections(Synset from, Synset to);

}
