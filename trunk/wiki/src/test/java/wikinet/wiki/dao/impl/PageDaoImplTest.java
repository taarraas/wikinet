package wikinet.wiki.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import wikinet.db.model.Locale;
import wikinet.wiki.dao.CategoryDao;
import wikinet.wiki.dao.LocalizedPageDao;
import wikinet.wiki.dao.PageDao;
import wikinet.wiki.domain.Category;
import wikinet.wiki.domain.LinkedPage;
import wikinet.wiki.domain.LocalizedPage;
import wikinet.wiki.domain.Page;

import java.util.List;

/**
 * @author shyiko
 * @since Apr 3, 2010
 */
@ContextConfiguration(locations = {"classpath:spring-wiki-module-test.xml"})
public class PageDaoImplTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private PageDao pageDao;

    @Autowired
    private LocalizedPageDao localizedPageDao;

    @Autowired
    private CategoryDao categoryDao;

    @Test
    public void testSave() throws Exception {
        Page page = new Page("title");
        page.setParagraph("paragraph");
        page.setText("big_text");
        pageDao.save(page);
        Page byId = pageDao.findById("title");
        Assert.assertNotNull(byId);
        Assert.assertEquals(byId.getParagraph(), "paragraph");
        Assert.assertEquals(byId.getText(), "big_text");
        Assert.assertEquals(pageDao.findAll().size(), 1);
    }

    @Test
    public void testComplexSave() throws Exception {
        Page redirectPage = new Page("redirect");
        pageDao.save(redirectPage);
        LocalizedPage localizedPage = new LocalizedPage("localized", Locale.POL);
        localizedPageDao.save(localizedPage);
        Category category = new Category("category");
        categoryDao.save(category);
        Page page = new Page("title");
        Page lp = new Page("lp");
        pageDao.save(lp);
        page.addLinkedPage(new LinkedPage(1, 2, lp));
        page.addRedirect(redirectPage);
        page.addLocalizedPage(localizedPage);
        page.addCategory(category);
        pageDao.save(page);
        Page foundPage = pageDao.findById("title");
        Assert.assertNotNull(foundPage);
        Assert.assertEquals(foundPage.getLinkedPages().size(), 1);
        LinkedPage flp = foundPage.getLinkedPages().iterator().next();
        Assert.assertEquals(flp.getStartPos(), 1);
        Assert.assertEquals(flp.getLength(), 2);
        Assert.assertEquals(flp.getPage().getTitle(), lp.getTitle());
        Assert.assertEquals(foundPage.getRedirects().size(), 1);
        Assert.assertEquals(foundPage.getRedirects().iterator().next().getTitle(), "redirect");
        List<LocalizedPage> localizedPages = foundPage.getLocalizedPages();
        Assert.assertEquals(localizedPages.size(), 1);
        LocalizedPage linkedLocalizedPage = localizedPages.iterator().next();
        Assert.assertEquals(linkedLocalizedPage.getTitle(), "localized");
        Assert.assertEquals(linkedLocalizedPage.getLocale(), Locale.POL);
        Assert.assertEquals(foundPage.getCategories().size(), 1);
        Assert.assertEquals(foundPage.getCategories().iterator().next().getName(), "category");
        Assert.assertEquals(pageDao.findAll().size(), 3);
        Assert.assertEquals(localizedPageDao.findAll().size(), 1);
        Assert.assertEquals(categoryDao.findAll().size(), 1);

        pageDao.delete(page);
        pageDao.delete(lp);
        pageDao.delete(redirectPage);        
        Assert.assertEquals(pageDao.findAll().size(), 0);
    }

    @Test
    public void testDelete() throws Exception {
        Page page = new Page("title");
        pageDao.save(page);
        pageDao.delete(page);
        Assert.assertEquals(pageDao.findAll().size(), 0);
    }
}
