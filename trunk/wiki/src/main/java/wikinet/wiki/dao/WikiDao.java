package wikinet.wiki.dao;

import wikinet.wiki.ArticleReference;

import java.util.Collection;
import java.util.Map;

/**
 * @author taras, shyiko
 */
public interface WikiDao {

    /**
     * @param word
     * @return All articles for a <i>word</i>
     */
    public Collection<ArticleReference> disambiguate(String word);

    /**
     * @param article
     * @return All <i>article</i> categories
     */
    public Collection<String> getCategories(ArticleReference article);

    /**
     * @param article
     * @return <i>article</i> description
     */
    public String getDescription(ArticleReference article);

    /**
     * @param article
     * @return pairs <Language, <i>article</i> in Language>
     */
    public Map<String, ArticleReference> getTranslations(ArticleReference article);

    /**
     * @param article
     * @return All words that refers to given i>article</i>. For example, article "LOL"
     * (http://en.wikipedia.org/wiki/Rofl) is accessed via word "LOL" and "ROFL"
     */
    public Collection<String> getRedirectWords(ArticleReference article);

    public Collection<ArticleReference> getConnectedArticles(ArticleReference article);

}
