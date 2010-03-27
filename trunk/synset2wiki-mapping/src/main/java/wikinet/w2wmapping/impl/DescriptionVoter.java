/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.w2wmapping.impl;

import wikinet.w2wmapping.SynsetArticleVoter;

/**
 *
 * @author taras
 */

/**
 * 1. Get words of description of given article.
 * 2. Get words of description of given synset.
 * 3. Get count of common words retrieved in step 1 and 2
 * 4*. Rate is more if count of common words is more
 * @author taras
 */
public class DescriptionVoter implements SynsetArticleVoter{

    @Override
    public double getVote(long synset, String article) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
