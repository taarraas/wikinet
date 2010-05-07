package wikinet.extending;

import wikinet.db.domain.Synset;
import wikinet.db.domain.Page;

/**
 * @author taras, shyiko
 */
public interface ConnectionsMaker {
    
    public void addConnections(Synset synset, Page article);

}
