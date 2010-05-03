package wikinet.wiki.parser.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import wikinet.db.model.Locale;
import wikinet.wiki.dao.CategoryDao;
import wikinet.wiki.dao.LinkedPageDao;
import wikinet.wiki.dao.LocalizedPageDao;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Category;
import wikinet.wiki.domain.LinkedPage;
import wikinet.wiki.domain.LocalizedPage;
import wikinet.wiki.domain.Page;
import wikinet.wiki.parser.PageBuilder;
import wikinet.wiki.parser.PagePrototypeSaver;
import wikinet.wiki.parser.prototype.PagePrototype;
import wikinet.wiki.parser.prototype.UniquePagePrototype;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

/**
 * @author shyiko
 * @since Apr 16, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-wiki-module-test.xml"})
public class PagePrototypeSaverImplTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private LinkedPageDao linkedPageDao;

    @Autowired
    private LocalizedPageDao localizedPageDao;

    @Autowired
    private PageDao pageDao;

    @Autowired
    private PageBuilder pageBuilder;

    @Autowired
    private PagePrototypeSaver prototypeSaver;

    @Autowired
    protected SessionFactory sessionFactory;

    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        sessionFactory.getCurrentSession().beginTransaction();
        for (LocalizedPage localizedPage : localizedPageDao.findAll()) {
            localizedPageDao.delete(localizedPage);
        }
        for (Category category : categoryDao.findAll()) {
            categoryDao.delete(category);
        }
        for (LinkedPage linkedPage : linkedPageDao.findAll()) {
            linkedPageDao.delete(linkedPage);
        }
        for (Page page : pageDao.findAll()) {
            pageDao.delete(page);
        }
        sessionFactory.getCurrentSession().getTransaction().commit();
    }

    @Test
    public void testSave() throws Exception {
        UniquePagePrototype uniquePagePrototype = new UniquePagePrototype("title (x)");
        uniquePagePrototype.addCategory("category");
        uniquePagePrototype.addLink("link").addPosition(1, 2);
        uniquePagePrototype.addLocalizedPage(Locale.POL, "linkp");
        uniquePagePrototype.appendFirstParagraph("fp");
        uniquePagePrototype.appendText("tp");
        prototypeSaver.save(uniquePagePrototype);
        sessionFactory.getCurrentSession().beginTransaction();
        try {
            Page page = pageDao.findByWordAndDisambiguation("title", "x");
            Assert.assertNotNull(page);
            Assert.assertEquals(page.getCategories().size(), 1);
            Assert.assertEquals(page.getLinkedPages().size(), 1);
            Assert.assertEquals(page.getLocalizedPages().size(), 1);
            Assert.assertEquals(page.getFirstParagraph(), "fp");
            Assert.assertEquals(page.getText(), "tp");
        } finally {
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }
    }

    @Test
    public void testSaveComplexPage() throws Exception {
        PagePrototype prototype =
                pageBuilder.buildPagePrototype("Abraham Lincoln",
                        getComplexText("src/test/resources/wiki-complex-text-long.log"));
        prototypeSaver.save(prototype);
        sessionFactory.getCurrentSession().beginTransaction();
        try {
            List<Page> pageList = pageDao.findByWord("Abraham Lincoln");
            Page page = pageDao.findByWordAndDisambiguation("Abraham Lincoln", null);
            Assert.assertNotNull(page);
        } finally {
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }
    }

    @Test
    public void testSaveLongComplexPage() throws Exception {
        PagePrototype prototype = pageBuilder.buildPagePrototype("Anarchism",
                getComplexText("src/test/resources/wiki-complex-text.log"));
        prototypeSaver.save(prototype);
        sessionFactory.getCurrentSession().beginTransaction();
        try {
            Page page = pageDao.findByWordAndDisambiguation("Anarchism", null);
            Assert.assertNotNull(page);
        } finally {
            sessionFactory.getCurrentSession().getTransaction().rollback();
        }
    }

    private String getComplexText(String fileName) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        StringBuilder sb = new StringBuilder();
        try {
            String s;
            while ((s = reader.readLine()) != null) {
                sb.append(s + "\n");
            }
        } finally {
            reader.close();
        }
        return sb.toString();
    }

}
