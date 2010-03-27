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
    public Collection<String> disambiguate(String word);

    /**
     * Returns categories for article
     * @param article
     * @return
     */
    public Collection<String> getCategories(String article);

    /**
     * Returns first paragraph of article
     * @param article
     * @return
     */
    public String getDescription(String article);

    /**
     *
     * @param article
     * @return pairs of language and name of article
     */
    public Map<String, String> translate(String article);

    /**
     * There are many ways to find the article in wikipedia.
     * For example, http://en.wikipedia.org/wiki/Rofl display article "Lol" and (Redirected from Rofl)
     * This method returns all words, which redirects to given article.
     * @param article
     * @return
     */
    public Collection<String> getRedirectWords(String article);
}
