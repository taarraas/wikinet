package wikinet.wiki.parser.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import wikinet.db.model.Locale;
import wikinet.wiki.dao.CategoryDao;
import wikinet.wiki.dao.LinkedPageDao;
import wikinet.wiki.dao.LocalizedPageDao;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Category;
import wikinet.wiki.domain.LocalizedPage;
import wikinet.wiki.domain.Page;
import wikinet.wiki.parser.PageBuilder;
import wikinet.wiki.parser.prototype.UniquePagePrototype;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author shyiko
 * @since Apr 16, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-wiki-module-test.xml"})
public class PagePrototypeSaverImplTest extends AbstractTransactionalTestNGSpringContextTests {

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
    private SessionFactory sessionFactory;
    
    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        Session session = SessionFactoryUtils.getSession(sessionFactory, true);
        for (LocalizedPage localizedPage : localizedPageDao.findAll()) {
            localizedPageDao.delete(localizedPage);
        }
        for (Category category : categoryDao.findAll()) {
            categoryDao.delete(category);
        }
        for (Page page : pageDao.findAll()) {
            pageDao.delete(page);
        }
        session.flush();
    }

    @Test
    public void testSave() throws Exception {
        PagePrototypeSaverImpl prototypeSaver = new PagePrototypeSaverImpl();
        prototypeSaver.setCategoryDao(categoryDao);
        prototypeSaver.setLinkedPageDao(linkedPageDao);
        prototypeSaver.setLocalizedPageDao(localizedPageDao);
        prototypeSaver.setPageDao(pageDao);
        UniquePagePrototype uniquePagePrototype = new UniquePagePrototype("title (x)");
        uniquePagePrototype.addCategory("category");
        uniquePagePrototype.addLink("link").addPosition(1, 2);
        uniquePagePrototype.addLocalizedPage(Locale.POL, "linkp");
        uniquePagePrototype.appendFirstParagraph("fp");
        uniquePagePrototype.appendText("tp");
        prototypeSaver.save(uniquePagePrototype);
        Page page = pageDao.findByWordAndDisambiguation("title", "x");
        Assert.assertNotNull(page);
        Assert.assertEquals(page.getCategories().size(), 1);
        Assert.assertEquals(page.getLinkedPages().size(), 1);
        Assert.assertEquals(page.getLocalizedPages().size(), 1);
        Assert.assertEquals(page.getFirstParagraph(), "fp");
        Assert.assertEquals(page.getText(), "tp");
    }

//    @Test
    public void testSaveComplexPage() throws Exception {
        try {
            pageBuilder.importPage("Abraham Lincoln", getComplexText("src/test/resources/wiki-complex-text-long.log"));
            Page page = pageDao.findByWordAndDisambiguation("Abraham Lincoln", null);
            Assert.assertNotNull(page);
        } finally {
            cleanDB();
        }
    }

//    @Test
    public void testSaveLongComplexPage() throws Exception {
        try {
            pageBuilder.importPage("Anarchism", getComplexText("src/test/resources/wiki-complex-text.log"));
            Page page = pageDao.findByWordAndDisambiguation("Anarchism", null);
            Assert.assertNotNull(page);
        } finally {
            cleanDB();
        }
    }


    private void cleanDB() {
/*
        for (Page page : pageDao.findAll()) {
            pageDao.delete(page);
        }
        for (LocalizedPage page : localizedPageDao.findAll()) {
            localizedPageDao.delete(page);
        }
        for (Category category : categoryDao.findAll()) {
            categoryDao.delete(category);
        }
*/
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
