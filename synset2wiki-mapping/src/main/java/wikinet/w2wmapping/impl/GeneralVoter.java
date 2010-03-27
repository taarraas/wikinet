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
public class GeneralVoter implements SynsetArticleVoter{
    DescriptionVoter descriptionVoter=new DescriptionVoter();
    LinksVoter linksVoter=new LinksVoter();
    @Override
    public double getVote(long synset, String article) {
        return (descriptionVoter.getVote(synset, article)+linksVoter.getVote(synset, article))/2;
    }

}
