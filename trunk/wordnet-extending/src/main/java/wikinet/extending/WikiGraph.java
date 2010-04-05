/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.extending;

import java.util.Set;
import wikinet.wiki.ArticleReference;

/**
 *
 * @author taras
 */
public interface WikiGraph {
    public Set<ArticleReference> getLinked(ArticleReference article);
}
