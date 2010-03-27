package wikinet.db.dao;

import wikinet.db.domain.Article;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
public interface ArticleDao extends GenericDao<Article, Long> {
    /**
     * 
     * @param article in form "Title[disambiguation]"
     * @return
     */
    Article getArticle(String article);
}
