package wikinet.extending;

import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.ArticleDao;
import wikinet.db.dao.SynsetDao;
import wikinet.db.dao.WordDao;
import wikinet.db.domain.Article;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.db.model.SynsetType;
import wikinet.wiki.ArticleReference;
import wikinet.wiki.WikiDao;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author taras, shyiko
 */
public class ExtendWordnet {

    @Autowired
    private List<ConnectionsMaker> connectionsMakers;

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private WikiDao wikiDao;

    @Autowired
    private WordDao wordDao;

    @Autowired
    private SynsetDao synsetDao;

    /**
     * Make synset with such information :
     * 0.  Connection to article
     * 1.  Description (from wiki article)
     * 2.  Words that represent this synset (from wiki redirecting)
     * 3.  Connections to other synsets (lot of methods for realising)
     *
     * @return Newly instantiated Synset entity
     */
    public Synset createSynset(ArticleReference article) {
        SynsetType synsetType = null; // TODO:taras

        // create synset with description from wiki
        Synset synset = new Synset(wikiDao.getDescription(article), synsetType);
        // connect article to synset
        String link = null; // TODO:taras
        Article articleObj = articleDao.getArticle(article.getWord(), link);
        articleObj.setDisambiguation(article.getDisambiguation());
        synset.addArticle(articleObj);
        Collection<String> redirectWords = wikiDao.getRedirectWords(article);
        List<Word> words = new LinkedList<Word>();
        for (String redirectWord : redirectWords) {
            words.add(wordDao.findById(redirectWord));
        }
        synset.setWords(words);
        synsetDao.save(synset);

        for (ConnectionsMaker connectionsMaker : connectionsMakers) {
            connectionsMaker.addConnections(synset, article);
        }
        return synset;
    }
}
