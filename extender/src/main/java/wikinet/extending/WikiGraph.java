package wikinet.extending;

import wikinet.wiki.parser.prototype.PagePrototype;

import java.util.Set;

/**
 * @author taras
 */
public interface WikiGraph {

    public Set<PagePrototype> getLinked(PagePrototype article);
    
}
