package wikinet.mapping.voter;

import wikinet.db.dao.SynsetDao;
import wikinet.db.domain.Category;
import wikinet.db.domain.Page;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.db.model.ConnectionType;
import wikinet.mapping.Utils;
import wikinet.mapping.Voter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author shyiko, taras
 */
public class CategoriesVoter implements Voter {

    private SynsetDao synsetDao;
    private Utils utils;
    private int wordnetSearchDepth = 2;
    private int wikiSearchDepth = 2;

    public void setSynsetDao(SynsetDao synsetDao) {
        this.synsetDao = synsetDao;
    }

    public void setUtils(Utils utils) {
        this.utils = utils;
    }

    public void setWordnetSearchDepth(int wordnetSearchDepth) {
        this.wordnetSearchDepth = wordnetSearchDepth;
    }

    public void setWikiSearchDepth(int wikiSearchDepth) {
        this.wikiSearchDepth = wikiSearchDepth;
    }

    @Override
    public double getVote(Synset synset, Page page) {
        Set<String> byWordnet = getLinkedWords(synset, wordnetSearchDepth);
        Set<String> byWiki = getCategories(page, wikiSearchDepth);
        return (utils.intersect(byWordnet, byWiki).size() >= 1) ? 1 : 0;
    }

    private Set<String> getLinkedWords(Synset synset, int deep) {
        Set<String> result = new HashSet<String>();
        if (deep!=wordnetSearchDepth) {
            for (Word word : synset.getWords()) {
                result.add(word.getWord());
            }
        }
        if (deep < 1) {
            return result;
        }
        List<Synset> list = synsetDao.getConnected(synset, ConnectionType.DOMAIN_OF_SYNSET_TOPIC);
        list.addAll(synsetDao.getConnected(synset, ConnectionType.HYPERNYM));
        for (Synset synset_ : list) {
            result.addAll(getLinkedWords(synset_, deep - 1));
        }
        return result;
    }

    private Set<String> getCategories(Page page, int deep) {
        Set<String> result = new HashSet<String>();
        for (Category category : page.getCategories()) {
            result.add(category.getName());
            result.addAll(getCategories(category, deep-1));
        }
        return result;
    }

    private Set<String> getCategories(Category category, int deep) {
        Set<String> result = new HashSet<String>();
        result.add(category.getName());
        if (deep < 1)
            return result;
        for (Category category_ : category.getSubcategories()) {
            result.addAll(getCategories(category_, deep - 1));
        }
        return result;
    }
}
