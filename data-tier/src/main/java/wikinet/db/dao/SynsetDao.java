package wikinet.db.dao;

import wikinet.db.domain.Synset;
import wikinet.db.model.ConnectionType;

import java.util.List;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public interface SynsetDao extends GenericDao<Synset, Long> {

    List<Synset> getConnected(Synset synset);
    List<Synset> getConnected(Synset synset, ConnectionType connectionType);
    List<Long> findAllId();

}
