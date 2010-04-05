/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.mapping.voters.impl;

import java.util.Collection;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.mapping.MappingUtils;
import wikinet.mapping.voters.SynsetArticleVoter;
import wikinet.wiki.ArticleReference;
import wikinet.wiki.dao.WikiDao;

/**
 *
 * @author taras
 */
public class SynonymousSetVoter implements SynsetArticleVoter{
    private static final int MINIMALWORDSCOUNT = 3;

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private WikiDao wikiDao;

    @Autowired
    private MappingUtils mappingUtils;

    @Override
    public double getVote(long synsetId, ArticleReference article) {
        Collection<String> fromWordNet = null; //TODO:shiyko get words for synset with id synsetId
        Collection<String> fromWiki = wikiDao.getRedirectWords(article);
        int cnt = mappingUtils.intersect(fromWordNet, fromWiki).size();
        if (cnt >= MINIMALWORDSCOUNT
                || fromWordNet.size()<MINIMALWORDSCOUNT
                || fromWiki.size() < MINIMALWORDSCOUNT) {
            return 1;
        } else {
            return 0;
        }
    }
    
}
