package wikinet.persistence.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import wikinet.persistence.dao.ArticleDao;
import wikinet.persistence.domain.Article;
import wikinet.persistence.domain.LocalizedArticle;
import wikinet.persistence.model.Locale;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author shyiko
 * @since Feb 28, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-module-test.xml"})
public class ArticleDaoImplTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private ArticleDao articleDao;

    @Test
    public void testSave() {
        Article article = new Article();
        article.setId(1L);
        article.setWord("word");
        article.setDisambiguation("disambiguation");
        article.setLink("link");
        articleDao.save(article);
        Article foundArticle = articleDao.findById(1L);
        assertEquals(foundArticle, article);
    }

    @Test(expectedExceptions = {DataIntegrityViolationException.class})
    public void testExceptionOnSaveWithoutWord() {
        Article article = new Article();
        article.setDisambiguation("disambiguation");
        article.setLink("link");
        articleDao.save(article);
    }

    @Test
    public void testSaveWithoutDisambiguation() {
        Article article = new Article();
        article.setId(1L);
        article.setWord("word");
        article.setLink("link");
        articleDao.save(article);
        Article foundArticle = articleDao.findById(1L);
        assertEquals(foundArticle, article);
    }

    @Test(expectedExceptions = {DataIntegrityViolationException.class})
    public void testExceptionOnSaveWithoutLink() {
        Article article = new Article();
        article.setWord("word");
        article.setDisambiguation("disambiguation");
        articleDao.save(article);
    }

    @Test
    public void testMultipleSave() {
        Article article1 = new Article();
        article1.setWord("word1");
        article1.setDisambiguation("disambiguation");
        article1.setLink("link");
        articleDao.save(article1);
        Article article2 = new Article();
        article2.setWord("word2");
        article2.setDisambiguation("disambiguation");
        article2.setLink("link");
        articleDao.save(article2);
        List<Article> list = articleDao.findAll();
        assertEquals(list.size(), 2);
    }

    @Test
    public void testRemove() {
        LocalizedArticle localizedArticle = new LocalizedArticle();
        localizedArticle.setLocale(Locale.RUS);
        localizedArticle.setWord("word");
        Article article = new Article();
        article.setId(1L);
        article.setWord("word");
        article.setDisambiguation("disambiguation");
        article.setLink("link");
        article.addLocalizedArticles(localizedArticle);
        articleDao.save(article);
        articleDao.delete(article);
        Article foundArticle = articleDao.findById(1L);
        assertNull(foundArticle);
    }

}
