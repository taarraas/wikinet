package wikinet.db.dao.impl;

import org.springframework.orm.hibernate3.HibernateObjectRetrievalFailureException;
import wikinet.db.dao.ArticleDao;
import wikinet.db.domain.Article;

import java.util.List;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public class ArticleDaoImpl extends GenericDaoImpl<Article, Long> implements ArticleDao {

    @Override
    public Article getArticle(String word, String disambiguation) {
        List articlesList = getHibernateTemplate().findByNamedQueryAndNamedParam("Article.getByWordAndDisambiguation",
                new String[]{"word", "disambiguation"},
                new Object[]{word, disambiguation});
        int size = articlesList.size();
        if (size > 1)
            throw new RuntimeException("Got more than one article with word = \"" + word +
                        "\" and disambiguation = \"" + disambiguation + "\"");
        if (size == 0)
            return null;
        return (Article) articlesList.get(0);
    }

}
