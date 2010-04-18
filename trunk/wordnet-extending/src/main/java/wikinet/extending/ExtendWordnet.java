package wikinet.extending;

import org.springframework.beans.factory.annotation.Autowired;
import wikinet.db.dao.ArticleDao;
import wikinet.db.dao.SynsetDao;
import wikinet.db.dao.WordDao;
import wikinet.db.domain.Article;
import wikinet.db.domain.Synset;
import wikinet.db.domain.Word;
import wikinet.db.model.SynsetType;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Page;
import wikinet.wiki.parser.prototype.PagePrototype;

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
    private WordDao wordDao;

    @Autowired
    private SynsetDao synsetDao;

    @Autowired
    private PageDao pageDao;

    /**
     * Make synset with such information :
     * 0.  Connection to article
     * 1.  Description (from wiki article)
     * 2.  Words that represent this synset (from wiki redirecting)
     * 3.  Connections to other synsets (lot of methods for realising)
     *
     * @return Newly instantiated Synset entity
     */
    public Synset createSynset(PagePrototype pagePrototype) {
        SynsetType synsetType = null; // TODO:taras

        Page page = pageDao.findByWordAndDisambiguation(pagePrototype.getWord(), pagePrototype.getDisambiguation());
        // create synset with description from wiki
        Synset synset = new Synset(page.getFirstParagraph(), synsetType);
        // connect article to synset
        String link = null; // TODO:taras
        Article articleObj = articleDao.getArticle(pagePrototype.getWord(), link);
        articleObj.setDisambiguation(pagePrototype.getDisambiguation());
        synset.addArticle(articleObj);
        List<Word> words = new LinkedList<Word>();
        for (Page rp : page.getRedirects()) {
            words.add(wordDao.findById(rp.getWord()));
        }
        synset.setWords(words);
        synsetDao.save(synset);

        for (ConnectionsMaker connectionsMaker : connectionsMakers) {
            connectionsMaker.addConnections(synset, pagePrototype);
        }
        return synset;
    }
}
