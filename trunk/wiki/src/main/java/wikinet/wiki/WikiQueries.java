/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.wiki;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author taras
 */
public interface WikiQueries {

    /**
     * Returns all articles for word
     * @param word
     * @return
     */
    Collection<String> disambiguate(String word);

    /**
     * Returns categories for article
     * @param article
     * @return
     */
    Collection<String> getCategories(String article);

    /**
     * Returns first paragraph of article
     * @param article
     * @return
     */
    String getDescription(String article);

    /**
     *
     * @param article
     * @return pairs of language and name of article
     */
    Map<String, String> translate(String article);
}
