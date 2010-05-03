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
        return (Article) getSession().getNamedQuery("Article.getByWordAndDisambiguation")
                .setString("word", word).setString("disambiguation", disambiguation).uniqueResult();
    }

}
