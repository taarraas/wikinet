/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.mapping.voters.impl;

import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.mapping.MappingUtils;
import wikinet.mapping.voters.SynsetArticleVoter;
import wikinet.wiki.parser.prototype.PagePrototype;

import java.util.Set;

/**
 * @author taras, shyiko
 */
public class CategoriesVoter implements SynsetArticleVoter {

    private static final int MAXIMUMLEVELWIKI=2,
            MAXIMUMLEVELWORDNET=1;

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private MappingUtils mappingUtils;

    //by ";c Domain of synset - TOPIC"
    private Set<String> getCategories(long synsetId, int deep) {
        throw new UnsupportedOperationException(); //TODO:shiyko
    }

    private Set<String> getCategories(PagePrototype pagePrototype, int deep) {
        throw new UnsupportedOperationException(); //TODO:shiyko
    }

    /**
     * Get  ";c Domain of synset - TOPIC" for synset
     * Get category, category of category, ...(up to MAXIMUMLEVEL) for this article
     * if there exists coincidence return 1
     * otherwise return 0
     * @param synsetId
     * @param article
     * @return
     */
    @Override
    public double getVote(long synsetId, PagePrototype article) {
        Set<String> byWordnet = getCategories(synsetId, MAXIMUMLEVELWORDNET);
        Set<String> byWiki = getCategories(synsetId, MAXIMUMLEVELWIKI);
        return (mappingUtils.intersect(byWordnet, byWiki).size()>=1)?1:0;
    }

}
