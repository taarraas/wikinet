/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wikinet.extending;

import wikinet.db.domain.Synset;
import wikinet.extending.impl.CategoriesConnections;
import wikinet.extending.impl.DescriptionIsAConnection;
import wikinet.wiki.WikiQueries;

/**
 *
 * @author taras
 */
public class ExtendWordnet {
    private static ExtendWordnet instance=new ExtendWordnet();

    public static ExtendWordnet getInstance() {
        return instance;
    }

    ConnectionsMaker categories=new CategoriesConnections(),
            description=new DescriptionIsAConnection();

    /**
     * Make synset with such information :
     *  0.  Connection to article
     *  1.  Description (from wiki article)
     *  2.  Words that represent this synset (from wiki redirecting)
     *  3.  Connections to other synsets (lot of methods for realising)
     * @param article
     * @return created synset entity
     */
    public Synset newSynset(String article) {
        Synset synset = new Synset();
        //synset.addArticle(ArticleDao.getArticle(article)); //TODO
        WikiQueries wikiQueries = null; //TODO
        synset.setDescription(wikiQueries.getDescription(article));
        //synset.setWords(wikiQueries.getRedirectWords(article)); //TODO
        //save synset TODO
        categories.addConnections(synset, article);
        description.addConnections(synset, article);
        return synset;
    }
}
