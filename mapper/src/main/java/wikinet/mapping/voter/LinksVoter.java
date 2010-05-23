package wikinet.mapping.voter;

import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.LinkedPage;
import wikinet.db.domain.Page;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.mapping.Voter;
import wikinet.mapping.Utils;
import wikinet.db.dao.PageDao;
import wikinet.wiki.parser.prototype.PagePrototype;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author taras, shyiko
 */
public class LinksVoter implements Voter {

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private PageDao pageDao;

    @Autowired
    private Utils utils;

    public void setSynsetDao(SynsetDao synsetDao) {
        this.synsetDao = synsetDao;
    }

    public void setPageDao(PageDao pageDao) {
        this.pageDao = pageDao;
    }

    public void setMappingUtils(Utils utils) {
        this.utils = utils;
    }

    /**
     * @return 1 - if synset connected synsets words are same as words being referenced from article,
     *         0 - if synset connected synsets use different words from article links,
     *         count((synset connected synsets words) intersect (article connected articles titles)) /
     *         count((synset connected synsets words) union (article connected articles titles)) otherwise 
     */
    @Override
    public double getVote(Synset synset, Page page) {
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
            connectedArticleWords.addAll(utils.getWords(connectedArticle.getPage().getWord()));
        }
        int common = utils.intersect(connectedSynsetsWords, connectedArticleWords).size();
        int total = utils.union(connectedSynsetsWords, connectedArticleWords).size();
        return common / total;
    }

}
