package wikinet.extending;

import wikinet.db.domain.Synset;
import wikinet.wiki.parser.prototype.PagePrototype;

/**
 * @author taras, shyiko
 */
public interface ConnectionsMaker {
    public void addConnections(Synset synset, PagePrototype article);
}
