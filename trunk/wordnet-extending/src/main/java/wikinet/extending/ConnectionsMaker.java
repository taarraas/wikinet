package wikinet.extending;

import wikinet.db.domain.Synset;
import wikinet.wiki.ArticleReference;

/**
 * @author taras, shyiko
 */
public interface ConnectionsMaker {
    public void addConnections(Synset synset, ArticleReference article);
}
