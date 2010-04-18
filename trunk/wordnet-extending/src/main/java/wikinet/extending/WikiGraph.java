/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.extending;

import wikinet.wiki.parser.prototype.PagePrototype;

import java.util.Set;

/**
 *
 * @author taras
 */
public interface WikiGraph {

    public Set<PagePrototype> getLinked(PagePrototype article);
    
}
