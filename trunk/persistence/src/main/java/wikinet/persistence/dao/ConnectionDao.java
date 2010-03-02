package wikinet.persistence.dao;

import wikinet.persistence.domain.Connection;
import wikinet.persistence.domain.ConnectionPK;
import wikinet.persistence.domain.Synset;

import java.util.List;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public interface ConnectionDao extends GenericDao<Connection, ConnectionPK> {

    List<Synset> getConnectedSynsets(Synset synset);

}
