package wikinet.mapping.voters.impl;

import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.mapping.MappingUtils;
import wikinet.mapping.voters.SynsetArticleVoter;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.LinkedPage;
import wikinet.wiki.domain.Page;
import wikinet.wiki.parser.prototype.PagePrototype;

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
    private PageDao pageDao;

    @Autowired
    private MappingUtils mappingUtils;

    /**
     * @param synsetId
     * @param page
     * @return 1 - if synset connected synsets words are same as words being referenced from article,
     *         0 - if synset connected synsets use different words from article links,
     *         count((synset connected synsets words) intersect (article connected articles titles)) /
     *         count((synset connected synsets words) union (article connected articles titles)) otherwise 
     */
    @Override
    public double getVote(long synsetId, PagePrototype page) {
        Synset synset = synsetDao.findById(synsetId);
        List<Synset> list = synsetDao.getConnected(synset);
        Set<String> connectedSynsetsWords = new HashSet<String>();
        for (Synset syn : list) {
            for (Word word : syn.getWords()) {
                connectedSynsetsWords.add(word.getWord());
            }
        }
        Page p = pageDao.findByWordAndDisambiguation(page.getWord(), page.getDisambiguation());
        Set<String> connectedArticleWords = new HashSet<String>();
        Set<LinkedPage> connectedArticles = p.getLinkedPages();
        for (LinkedPage connectedArticle : connectedArticles) {
            connectedArticleWords.addAll(mappingUtils.getWords(connectedArticle.getPage().getWord()));
        }
        int common = mappingUtils.intersect(connectedSynsetsWords, connectedArticleWords).size();
        int total = mappingUtils.union(connectedSynsetsWords, connectedArticleWords).size();
        return common / total;
    }

}
