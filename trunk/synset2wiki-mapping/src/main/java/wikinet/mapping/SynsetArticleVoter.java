/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.mapping;

import java.util.Map;

/**
 *
 * @author taras
 */
public interface SynsetArticleVoter {
    /**
     *
     * @param synset
     * @param article
     * @return double from 0..1, 1 for most similar
     */
    public double getVote(long synset, String article);
}
