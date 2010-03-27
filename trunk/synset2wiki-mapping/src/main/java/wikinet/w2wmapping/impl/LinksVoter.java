/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.w2wmapping.impl;

import wikinet.w2wmapping.SynsetArticleVoter;

/**
 * 1. Get links from this article to other and make list of titles.
 *      [Optional] Get links only from description and from sentences that contains title of article
 * 2. Get connected synsets for this synset, get words for them.
 * 3. Get count of common words retrieved in step 1 and 2
 * 4*. Rate is more if count of common words is more
 */
public class LinksVoter implements SynsetArticleVoter{
    private static final boolean USETARANUHAOPTIONALFEATURE=false; // to watch what gives better results
    @Override
    public double getVote(long synset, String article) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
