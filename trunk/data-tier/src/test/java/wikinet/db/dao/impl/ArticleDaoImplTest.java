package wikinet.db.dao.impl;

import org.hibernate.PropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;
import wikinet.db.dao.ArticleDao;
import wikinet.testng.SpringDaoTest;
import wikinet.db.domain.Article;
import wikinet.db.domain.LocalizedArticle;
import wikinet.db.model.Locale;

import java.util.List;

import static org.testng.Assert.*;


/**
 * @author shyiko
 * @since Feb 28, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-dao-module-test.xml"})
public class ArticleDaoImplTest extends SpringDaoTest {

    @Autowired
    private ArticleDao articleDao;

    @Test
    public void testSave() {
        Article article = new Article("word", "link");
        article.setDisambiguation("disambiguation");
        articleDao.save(article);
        Article foundArticle = articleDao.findById(article.getId());
        assertEquals(foundArticle, article);
    }

    @Test
    public void testSaveWithLocalizedArticle() {
        LocalizedArticle localizedArticle = new LocalizedArticle(Locale.RUS, "word");
        Article article = new Article("word", "link");
        article.setDisambiguation("disambiguation");
        article.addLocalizedArticles(localizedArticle);
        articleDao.save(article);
        Article foundArticle = articleDao.findById(article.getId());
        assertEquals(foundArticle.getLocalizedArticles().size(), 1);
        assertEquals(foundArticle, article);
    }

    @Test(expectedExceptions = {PropertyValueException.class})
    public void testExceptionOnSaveWithoutWord() {
        Article article = new Article(null, "link");
        article.setDisambiguation("disambiguation");
        articleDao.save(article);
    }

    @Test
    public void testSaveWithoutDisambiguation() {
        Article article = new Article("word", "link");
        articleDao.save(article);
        Article foundArticle = articleDao.findById(article.getId());
        assertEquals(foundArticle, article);
    }

    @Test(expectedExceptions = {PropertyValueException.class})
    public void testExceptionOnSaveWithoutLink() {
        Article article = new Article("word", null);
        article.setDisambiguation("disambiguation");
        articleDao.save(article);
    }

    @Test
    public void testMultipleSave() {
        Article article1 = new Article("word1", "link");
        article1.setDisambiguation("disambiguation");
        articleDao.save(article1);
        Article article2 = new Article("word2", "link");
        article2.setDisambiguation("disambiguation");
        articleDao.save(article2);
        List<Article> list = articleDao.findAll();
        assertEquals(list.size(), 2);
    }

    @Test
    public void testRemove() {
        LocalizedArticle localizedArticle = new LocalizedArticle(Locale.RUS, "word");
        Article article = new Article("word", "link");
        article.setDisambiguation("disambiguation");
        article.addLocalizedArticles(localizedArticle);
        articleDao.save(article);
        articleDao.delete(article);
        Article foundArticle = articleDao.findById(article.getId());
        assertNull(foundArticle);
    }

    @Test
    public void testGetArticle() {
        Article article = new Article("word", "link");
        article.setDisambiguation("disambiguation");
        articleDao.save(article);
        Article art = articleDao.getArticle("word", "disambiguation2");
        assertNull(art);
        art = articleDao.getArticle("word", "disambiguation");
        assertNotNull(art);
    }

}
