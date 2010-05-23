package wikinet.mapping;

import wikinet.db.domain.Page;
import wikinet.db.domain.Synset;
import wikinet.wiki.parser.prototype.PagePrototype;

/**
 * @author taras, shyiko
 */
public interface Voter {

    /**
     * @return similarity level in range 0..1, where 1 means strongest relation
     */
    public double getVote(Synset synset, Page page);
}
