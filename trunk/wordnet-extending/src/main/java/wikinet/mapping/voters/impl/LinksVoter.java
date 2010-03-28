package wikinet.mapping.voters.impl;

import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.mapping.MappingUtils;
import wikinet.mapping.voters.SynsetArticleVoter;
import wikinet.wiki.ArticleReference;
import wikinet.wiki.WikiDao;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author taras, shyiko
 */
public class LinksVoter implements SynsetArticleVoter {

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private WikiDao wikiDao;

    @Autowired
    private MappingUtils mappingUtils;

    /**
     * @param synsetId
     * @param article
     * @return 1 - if synset connected synsets words are same as words being referenced from article,
     *         0 - if synset connected synsets use different words from article links,
     *         count((synset connected synsets words) intersect (article connected articles titles)) /
     *         count((synset connected synsets words) union (article connected articles titles)) otherwise 
     */
    @Override
    public double getVote(long synsetId, ArticleReference article) {
        Synset synset = synsetDao.findById(synsetId);
        List<Synset> list = synsetDao.getConnected(synset);
        Set<String> connectedSynsetsWords = new HashSet<String>();
        for (Synset syn : list) {
            for (Word word : syn.getWords()) {
                connectedSynsetsWords.add(word.getWord());
            }
        }
        Collection<ArticleReference> connectedArticles = wikiDao.getConnectedArticles(article);
        Set<String> connectedArticleWords = new HashSet<String>();
        for (ArticleReference connectedArticle : connectedArticles) {
            connectedArticleWords.addAll(mappingUtils.getWords(connectedArticle.getWord()));
        }
        int common = mappingUtils.intersect(connectedSynsetsWords, connectedArticleWords).size();
        int total = mappingUtils.union(connectedSynsetsWords, connectedArticleWords).size();
        return common / total;
    }

}
