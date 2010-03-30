package wikinet.mapping.voters.impl;

import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Synset;
import wikinet.mapping.MappingUtils;
import wikinet.mapping.voters.SynsetArticleVoter;
import wikinet.wiki.ArticleReference;
import wikinet.wiki.dao.WikiDao;

import java.util.Set;

/**
 * @author taras, shyiko
 */
public class DescriptionVoter implements SynsetArticleVoter {

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private WikiDao wikiDao;

    @Autowired
    private MappingUtils mappingUtils;

    /**
     * @param synsetId
     * @param article
     * @return 1 - if article description use same words as synset description,
     *         0 - if article description use different words from synset description,
     *         <i>count((article description words) intersect (synset description words)) /
     *         count((article description words) union (synset description words))</i> otherwise
     */
    @Override
    public double getVote(long synsetId, ArticleReference article) {
        Synset synset = synsetDao.findById(synsetId);
        Set<String> synsetWords = mappingUtils.getWords(synset.getDescription());
        Set<String> wikiWords = mappingUtils.getWords(wikiDao.getDescription(article));
        int wordsInCommon = mappingUtils.intersect(synsetWords, wikiWords).size();
        int wordsTotal = mappingUtils.union(synsetWords, wikiWords).size();
        return wordsInCommon / wordsTotal;
    }

}
